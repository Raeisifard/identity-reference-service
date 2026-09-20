package com.isc.identityreference.refresh;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.cache.redis.RedisL2Cache;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.observability.IdentityReferenceMetrics;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdentityReferenceRefreshServiceTest {
 @Test void forceRefreshRetrievesPersistsAndCaches(){
  InMemoryStore store=new InMemoryStore(); IdentityLookupKey key=new IdentityLookupKey("ID-1",LocalDate.of(1990,1,1));
  IdentityProvider provider=new IdentityProvider(){public ProviderDescriptor descriptor(){return new ProviderDescriptor("mock-provider","Mock");}public ProviderLookupResult lookup(IdentityLookupKey k){return ProviderLookupResult.found("mock-provider","record-1",ProviderAuthority.AUTHORITATIVE,new IdentityAttributes("Ada","Lovelace","Byron",k.birthDate(),"F",k.nationalId()),Instant.parse("2026-09-20T08:00:00Z"));}};
  RefreshProperties properties=new RefreshProperties(); properties.getProviders().put("mock-provider",new RefreshProperties.Provider());
  @SuppressWarnings("unchecked") ObjectProvider<RedisL2Cache> l2=mock(ObjectProvider.class); when(l2.getIfAvailable()).thenReturn(null);
  var service=new IdentityReferenceRefreshService(store,new IdentityProviderRegistry(List.of(provider)),new CaffeineL1Cache(10),l2,new RefreshPolicyResolver(properties),properties,new InMemoryRefreshLeaseManager(),new RefreshQuotaGuard(),new IdentityReferenceMetrics(new SimpleMeterRegistry()));
  RefreshResult result=service.forceRefresh(key,"mock-provider",Instant.parse("2026-09-20T08:00:00Z"));
  assertEquals(RefreshResult.Status.REFRESHED,result.status()); assertNotNull(result.reference()); assertTrue(store.find(key).isPresent()); assertTrue(result.reference().freshness().freshUntil().isAfter(result.reference().freshness().acquiredAt()));
 }
 private static final class InMemoryStore implements IdentityReferenceStore {final Map<String,IdentityReference> values=new HashMap<>();public Optional<IdentityReference> find(IdentityLookupKey k){return Optional.ofNullable(values.get(k.nationalId()+":"+k.birthDate()));}public IdentityReference save(IdentityReference r){values.put(r.lookupKey().nationalId()+":"+r.lookupKey().birthDate(),r);return r;}}
}