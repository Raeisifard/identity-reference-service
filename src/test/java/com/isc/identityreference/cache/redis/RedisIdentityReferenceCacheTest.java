package com.isc.identityreference.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisIdentityReferenceCacheTest {
    @Test void redisReadFailureIsTreatedAsMiss() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        when(redis.opsForValue()).thenThrow(new IllegalStateException("redis unavailable"));
        var cache = new RedisIdentityReferenceCache(redis, new ObjectMapper(), "identity-ref:v1:", Duration.ofMinutes(5));
        assertTrue(cache.get("hash").isEmpty());
    }

    @Test void blankLookupHashIsRejectedBeforeRedisAccess() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        var cache = new RedisIdentityReferenceCache(redis, new ObjectMapper(), "identity-ref:v1:", Duration.ofMinutes(5));
        assertThrows(IllegalArgumentException.class, () -> cache.get(" "));
        verifyNoInteractions(redis);
    }
}
