package com.isc.identityreference.cache.redis;

import com.isc.identityreference.domain.identity.IdentityReference;
import java.util.Optional;

public interface RedisL2Cache {
    Optional<IdentityReference> get(String lookupKeyHash);
    void put(String lookupKeyHash, IdentityReference reference);
    void evict(String lookupKeyHash);
}
