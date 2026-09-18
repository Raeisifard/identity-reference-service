package com.isc.identityreference.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.cache.redis", name = "enabled", havingValue = "true")
public class RedisCacheConfiguration {
    @Bean
    RedisL2Cache redisL2Cache(StringRedisTemplate redis, ObjectMapper objectMapper) {
        return new RedisIdentityReferenceCache(redis, objectMapper, "identity-ref:v1:", Duration.ofHours(24));
    }
}
