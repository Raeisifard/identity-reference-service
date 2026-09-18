package com.isc.identityreference.cache.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.identityreference.domain.identity.IdentityReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.util.Optional;

public final class RedisIdentityReferenceCache implements RedisL2Cache {
    private static final int SCHEMA_VERSION = 1;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final String keyPrefix;
    private final Duration entryTtl;

    public RedisIdentityReferenceCache(StringRedisTemplate redis, ObjectMapper objectMapper,
                                       String keyPrefix, Duration entryTtl) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.keyPrefix = keyPrefix;
        this.entryTtl = entryTtl;
    }

    @Override
    public Optional<IdentityReference> get(String lookupKeyHash) {
        try {
            String value = redis.opsForValue().get(key(lookupKeyHash));
            if (value == null) return Optional.empty();
            CacheEnvelope envelope = objectMapper.readValue(value, CacheEnvelope.class);
            if (envelope.schemaVersion() != SCHEMA_VERSION) return Optional.empty();
            return Optional.of(envelope.reference());
        } catch (Exception ignored) {
            // Redis is an acceleration layer; a cache failure must behave as a miss.
            return Optional.empty();
        }
    }

    @Override
    public void put(String lookupKeyHash, IdentityReference reference) {
        try {
            String value = objectMapper.writeValueAsString(new CacheEnvelope(SCHEMA_VERSION, reference));
            redis.opsForValue().set(key(lookupKeyHash), value, entryTtl);
        } catch (JsonProcessingException | RuntimeException ignored) {
            // Cache write failure must not make the durable operation fail.
        }
    }

    @Override
    public void evict(String lookupKeyHash) {
        try {
            redis.delete(key(lookupKeyHash));
        } catch (RuntimeException ignored) {
            // Best-effort invalidation; Oracle remains authoritative.
        }
    }

    private String key(String lookupKeyHash) {
        if (lookupKeyHash == null || lookupKeyHash.isBlank()) throw new IllegalArgumentException("lookupKeyHash must not be blank");
        return keyPrefix + lookupKeyHash;
    }

    public record CacheEnvelope(int schemaVersion, IdentityReference reference) {}
}
