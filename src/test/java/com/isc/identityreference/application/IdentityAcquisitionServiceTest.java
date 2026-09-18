package com.isc.identityreference.application;

import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.cache.redis.RedisL2Cache;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.policy.ProviderPolicy;
import com.isc.identityreference.policy.ProviderPolicyEngine;
import com.isc.identityreference.provider.spi.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IdentityAcquisitionServiceTest {
    @Test void acquiresNormalizesPersistsAndCaches() {
        var store = new InMemoryStore();
        var provider = provider();
        var l1 = new CaffeineL1Cache(10);
        RedisL2Cache l2 = new RedisL2Cache() {
            private final Map<String, IdentityReference> values = new HashMap<>();
            public Optional<IdentityReference> get(String k){return Optional.ofNullable(values.get(k));}
            public void put(String k, IdentityReference v){values.put(k,v);}
            public void evict(String k){values.remove(k);}
        };
        var service = new IdentityAcquisitionService(store, provider, policy(), new ProviderPolicyEngine(), l1, l2);
        var key = new IdentityLookupKey("ID-1", LocalDate.of(1815,12,10));
        var result = service.acquire(new IdentityAcquisitionRequest(key, "mock-provider", "request-1"),
                Instant.parse("2026-09-18T00:00:00Z"));
        assertEquals(IdentityAcquisitionResult.Status.ACQUIRED, result.status());
        assertEquals(IdentityLifecycleState.ACTIVE, result.reference().lifecycleState());
        assertEquals(1, store.values.size());
        assertTrue(l1.get(LookupKeyFingerprint.of(key)).isPresent());
    }

    @Test void idempotencyReturnsReplay() {
        var store = new InMemoryStore();
        var service = new IdentityAcquisitionService(store, provider(), policy(), new ProviderPolicyEngine(),
                new CaffeineL1Cache(10), new NoopL2());
        var key = new IdentityLookupKey("ID-1", LocalDate.of(1815,12,10));
        var request = new IdentityAcquisitionRequest(key, "mock-provider", "same-request");
        var first = service.acquire(request, Instant.parse("2026-09-18T00:00:00Z"));
        var second = service.acquire(request, Instant.parse("2026-09-18T00:01:00Z"));
        assertEquals(IdentityAcquisitionResult.Status.ACQUIRED, first.status());
        assertEquals(IdentityAcquisitionResult.Status.IDEMPOTENT_REPLAY, second.status());
        assertSame(first.reference(), second.reference());
    }

    private static IdentityProvider provider() {
        return new IdentityProvider() {
            public ProviderDescriptor descriptor(){return new ProviderDescriptor("mock-provider","Mock");}
            public ProviderLookupResult lookup(IdentityLookupKey k) {
                var a = new IdentityAttributes("Ada","Lovelace","Byron",k.birthDate(),"F",k.nationalId());
                return ProviderLookupResult.found("mock-provider","record-1",ProviderAuthority.AUTHORITATIVE,a,Instant.parse("2026-09-18T00:00:00Z"));
            }
        };
    }
    private static ProviderPolicy policy() {
        return new ProviderPolicy("mock-provider",1,true,ProviderAuthority.AUTHORITATIVE,
                Set.of(ProviderPolicy.ProviderClaim.NATIONAL_ID_AND_BIRTH_DATE),Map.of(),
                Duration.ofHours(24),Duration.ofHours(12),List.of(),Duration.ZERO,
                new ProviderPolicy.RequestQuota(10,2),
                new ProviderPolicy.RetryPolicy(2,Duration.ofSeconds(1),Duration.ofSeconds(4)),
                Duration.ofSeconds(5),ProviderPolicy.OverwriteRule.SAME_OR_HIGHER_AUTHORITY,
                new ProviderPolicy.PhotoPolicy(false,false),new ProviderPolicy.EmbeddingPolicy(false,null,null,0));
    }
    private static final class InMemoryStore implements IdentityReferenceStore {
        final Map<String,IdentityReference> values=new HashMap<>();
        public Optional<IdentityReference> find(IdentityLookupKey k){return Optional.ofNullable(values.get(k.nationalId()+":"+k.birthDate()));}
        public IdentityReference save(IdentityReference r){values.put(r.lookupKey().nationalId()+":"+r.lookupKey().birthDate(),r);return r;}
    }
    private static final class NoopL2 implements RedisL2Cache {
        public Optional<IdentityReference> get(String k){return Optional.empty();}
        public void put(String k,IdentityReference v){}
        public void evict(String k){}
    }
}
