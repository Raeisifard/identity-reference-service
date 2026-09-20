package com.isc.identityreference.refresh;
import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.cache.redis.RedisL2Cache;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.observability.IdentityReferenceMetrics;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class IdentityReferenceRefreshServiceTest{
 @Test void forceRefreshRetrievesPersistsCachesAndReleasesLease(){
  InMemoryStore store=new InMemoryStore();IdentityLookupKey key=new IdentityLookupKey("ID-1",LocalDate.of(1990,1,1));
  IdentityProvider provider=new IdentityProvider(){public ProviderDescriptor descriptor(){return new ProviderDescriptor("mock-provider","Mock");}public ProviderLookupResult lookup(IdentityLookupKey k){return ProviderLookupResult.found("mock-provider","record-1",ProviderAuthority.AUTHORITATIVE,new IdentityAttributes("Ada","Lovelace","Byron",k.birthDate(),"F",k.nationalId()),Instant.now());}};
  RefreshProperties p=new RefreshProperties();p.getProviders().put("mock-provider",new RefreshProperties.Provider());ObjectProvider<RedisL2Cache> l2=mock(ObjectProvider.class);when(l2.getIfAvailable()).thenReturn(null);InMemoryRefreshLeaseManager leases=new InMemoryRefreshLeaseManager();
  var service=new IdentityReferenceRefreshService(store,new IdentityProviderRegistry(List.of(provider)),new CaffeineL1Cache(10),l2,new RefreshPolicyResolver(p),p,leases,new RefreshQuotaGuard(),new IdentityReferenceMetrics(new SimpleMeterRegistry()),Runnable::run,new SimpleAsyncTaskExecutor());
  RefreshResult result=service.forceRefresh(key,"mock-provider",Instant.now());assertEquals(RefreshResult.Status.REFRESHED,result.status());assertNotNull(result.reference());assertTrue(leases.tryAcquire("same",Duration.ofSeconds(1)).isPresent());
 }
 @Test void leaseOwnershipPreventsConcurrentDuplicateWork(){
  InMemoryRefreshLeaseManager leases=new InMemoryRefreshLeaseManager();var first=leases.tryAcquire("key",Duration.ofSeconds(1));assertTrue(first.isPresent());assertTrue(leases.tryAcquire("key",Duration.ofSeconds(1)).isEmpty());leases.release(new RefreshLeaseManager.Lease("key","wrong"));assertTrue(leases.tryAcquire("key",Duration.ofSeconds(1)).isEmpty());leases.release(first.orElseThrow());assertTrue(leases.tryAcquire("key",Duration.ofSeconds(1)).isPresent());
 }
 @Test void providerTimeoutDoesNotDestroyExistingReference(){
  InMemoryStore store=new InMemoryStore();IdentityLookupKey key=new IdentityLookupKey("ID-3",LocalDate.of(1992,1,1));IdentityReference existing=sampleReference(key);store.save(existing);
  IdentityProvider provider=new IdentityProvider(){public ProviderDescriptor descriptor(){return new ProviderDescriptor("slow-provider","Slow");}public ProviderLookupResult lookup(IdentityLookupKey k){try{Thread.sleep(250);}catch(InterruptedException ignored){}return ProviderLookupResult.found("slow-provider","record",ProviderAuthority.AUTHORITATIVE,new IdentityAttributes("A","B",null,k.birthDate(),"F",k.nationalId()),Instant.now());}};
  RefreshProperties p=new RefreshProperties();var pp=new RefreshProperties.Provider();pp.setTimeout(Duration.ofMillis(25));pp.setMaxAttempts(1);p.getProviders().put("slow-provider",pp);ObjectProvider<RedisL2Cache> l2=mock(ObjectProvider.class);when(l2.getIfAvailable()).thenReturn(null);
  var service=new IdentityReferenceRefreshService(store,new IdentityProviderRegistry(List.of(provider)),new CaffeineL1Cache(10),l2,new RefreshPolicyResolver(p),p,new InMemoryRefreshLeaseManager(),new RefreshQuotaGuard(),new IdentityReferenceMetrics(new SimpleMeterRegistry()),Runnable::run,new SimpleAsyncTaskExecutor());
  long start=System.nanoTime();RefreshResult result=service.forceRefresh(key,"slow-provider",Instant.now());assertEquals(RefreshResult.Status.FAILED,result.status());assertTrue(Duration.ofNanos(System.nanoTime()-start).toMillis()<200);assertEquals(existing.id(),store.find(key).orElseThrow().id());
 }
 private static IdentityReference sampleReference(IdentityLookupKey key){var f=new com.isc.identityreference.domain.freshness.Freshness(Instant.now().minusSeconds(60),Instant.now().plusSeconds(60),Instant.now().plusSeconds(600));var a=new IdentityAttributes("A","B",null,key.birthDate(),"F",key.nationalId());return new IdentityReference(IdentityReferenceId.newId(),key,a,List.of(),List.of(),com.isc.identityreference.domain.lifecycle.IdentityLifecycleState.ACTIVE,f,f.acquiredAt(),Instant.now());}
 private static final class InMemoryStore implements IdentityReferenceStore{final Map<String,IdentityReference> values=new HashMap<>();public Optional<IdentityReference> find(IdentityLookupKey k){return Optional.ofNullable(values.get(k.nationalId()+":"+k.birthDate()));}public IdentityReference save(IdentityReference r){values.put(r.lookupKey().nationalId()+":"+r.lookupKey().birthDate(),r);return r;}}
}