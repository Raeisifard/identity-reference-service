package com.isc.identityreference.refresh;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.application.LookupKeyFingerprint;
import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.cache.redis.RedisL2Cache;
import com.isc.identityreference.domain.freshness.*;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.domain.provider.*;
import com.isc.identityreference.observability.IdentityReferenceMetrics;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service public class IdentityReferenceRefreshService {
 private final IdentityReferenceStore store; private final IdentityProviderRegistry providers; private final CaffeineL1Cache l1; private final RedisL2Cache l2; private final RefreshPolicyResolver policies; private final RefreshProperties properties; private final RefreshLeaseManager leases; private final RefreshQuotaGuard quota; private final IdentityReferenceMetrics metrics;
 public IdentityReferenceRefreshService(IdentityReferenceStore store,IdentityProviderRegistry providers,CaffeineL1Cache l1,@org.springframework.beans.factory.annotation.Autowired(required=false) RedisL2Cache l2,RefreshPolicyResolver policies,RefreshProperties properties,RefreshLeaseManager leases,RefreshQuotaGuard quota,IdentityReferenceMetrics metrics){this.store=store;this.providers=providers;this.l1=l1;this.l2=l2;this.policies=policies;this.properties=properties;this.leases=leases;this.quota=quota;this.metrics=metrics;}
 public RefreshResult getOrRefresh(IdentityLookupKey key,String providerId,Instant now){
  String hash=LookupKeyFingerprint.of(key); Optional<IdentityReference> ref=l1.get(hash); if(ref.isEmpty()&&l2!=null)ref=l2.get(hash); if(ref.isEmpty())ref=store.find(key);
  if(ref.isPresent()){FreshnessState state=ref.get().freshnessAt(now); if(state==FreshnessState.FRESH){cache(hash,ref.get());return new RefreshResult(RefreshResult.Status.SKIPPED,ref.get(),"fresh","-");}
   if(state==FreshnessState.STALE&&properties.isStaleWhileRefresh()){cache(hash,ref.get());CompletableFuture.runAsync(()->refresh(key,providerId,Instant.now(),true,"automatic"));metrics.refreshOutcome(providerId,"automatic","stale-served");return new RefreshResult(RefreshResult.Status.STALE_SERVED,ref.get(),"stale reference served while refresh runs","-");}}
  return refresh(key,providerId,now,true,"automatic");
 }
 public RefreshResult forceRefresh(IdentityLookupKey key,String providerId,Instant now){return refresh(key,providerId,now,false,"admin");}
 public RefreshResult scheduledRefresh(IdentityLookupKey key,String providerId,Instant now){return refresh(key,providerId,now,true,"scheduled");}
 public RefreshResult refresh(IdentityLookupKey key,String providerId,Instant now,boolean requireLease){return refresh(key,providerId,now,requireLease,"automatic");}
 private RefreshResult refresh(IdentityLookupKey key,String providerId,Instant now,boolean requireLease,String trigger){
  String operationId=UUID.randomUUID().toString(); long start=System.nanoTime(); RefreshPolicy policy=policies.resolve(providerId);
  if(requireLease&&!leases.tryAcquire(LookupKeyFingerprint.of(key),properties.getLeaseDuration())){metrics.refreshOutcome(providerId,trigger,"locked");return new RefreshResult(RefreshResult.Status.LOCKED,null,"Refresh already in progress",operationId);}
  if(!quota.tryEnter(policy)){metrics.refreshOutcome(providerId,trigger,"quota-deferred");return new RefreshResult(RefreshResult.Status.LOCKED,null,"Provider quota temporarily exhausted",operationId);}
  try{
   IdentityProvider provider=providers.find(providerId).orElse(null); if(provider==null){metrics.refreshOutcome(providerId,trigger,"provider-unavailable");return new RefreshResult(RefreshResult.Status.FAILED,null,"Provider is not available",operationId);}
   ProviderLookupResult result=lookupWithRetry(provider,key,policy);
   if(result.status()==ProviderLookupStatus.NOT_FOUND){metrics.refreshOutcome(providerId,trigger,"not-found");return new RefreshResult(RefreshResult.Status.NOT_FOUND,null,"No matching identity was found",operationId);}
   if(result.status()!=ProviderLookupStatus.FOUND){metrics.refreshOutcome(providerId,trigger,"provider-failure");return new RefreshResult(RefreshResult.Status.FAILED,null,"Provider refresh failed",operationId);}
   Instant retrieved=result.retrievedAt(); Freshness freshness=new Freshness(retrieved,retrieved.plus(policy.ttl()),retrieved.plus(policy.ttl()).plus(policy.staleGrace())); IdentityReference existing=store.find(key).orElse(null);
   IdentityReference reference=new IdentityReference(existing==null?IdentityReferenceId.newId():existing.id(),key,result.attributes(),List.of(new ProviderRecord(result.providerId(),result.providerRecordId(),result.authority(),ProviderRecordState.CURRENT,result.attributes(),freshness,retrieved)),existing==null?List.of():existing.biometricReferences(),IdentityLifecycleState.ACTIVE,freshness,existing==null?now:existing.createdAt(),now);
   store.save(reference); cache(LookupKeyFingerprint.of(key),reference); metrics.refreshOutcome(providerId,trigger,"refreshed"); metrics.refreshDuration(providerId,trigger,System.nanoTime()-start); return new RefreshResult(RefreshResult.Status.REFRESHED,reference,"Identity reference refreshed",operationId);
  }catch(RuntimeException ex){metrics.refreshOutcome(providerId,trigger,"error");metrics.refreshDuration(providerId,trigger,System.nanoTime()-start);return new RefreshResult(RefreshResult.Status.FAILED,null,"Refresh failed",operationId);} finally{quota.exit(providerId);}
 }
 private ProviderLookupResult lookupWithRetry(IdentityProvider provider,IdentityLookupKey key,RefreshPolicy policy){ProviderLookupResult result=null;for(int attempt=1;attempt<=policy.maxAttempts();attempt++){result=provider.lookup(key);if(result.status()!=ProviderLookupStatus.ERROR||result.error()==null||!result.error().retryable())return result;if(attempt<policy.maxAttempts())try{long millis=Math.min(policy.maxBackoff().toMillis(),policy.initialBackoff().toMillis()*(1L<<(attempt-1)));Thread.sleep(millis);}catch(InterruptedException e){Thread.currentThread().interrupt();return result;}}return result;}
 private void cache(String hash,IdentityReference reference){l1.put(hash,reference);if(l2!=null)l2.put(hash,reference);}
}