package com.isc.identityreference.cache.l1;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.isc.identityreference.domain.identity.IdentityReference;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class CaffeineL1Cache {
    private final Cache<String, IdentityReference> cache;

    public CaffeineL1Cache(long maximumSize) {
        if (maximumSize <= 0) throw new IllegalArgumentException("maximumSize must be positive");
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .recordStats()
                .build();
    }

    public Optional<IdentityReference> get(String lookupKeyHash) {
        return Optional.ofNullable(cache.getIfPresent(requireKey(lookupKeyHash)));
    }

    public void put(String lookupKeyHash, IdentityReference reference) {
        cache.put(requireKey(lookupKeyHash), reference);
    }

    public void evict(String lookupKeyHash) {
        cache.invalidate(requireKey(lookupKeyHash));
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public long estimatedSize() {
        return cache.estimatedSize();
    }

    public com.github.benmanes.caffeine.cache.stats.CacheStats stats() {
        return cache.stats();
    }

    private String requireKey(String key) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("lookupKeyHash must not be blank");
        return key;
    }
}
