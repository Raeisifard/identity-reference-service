package com.isc.identityreference.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.cache.redis", name = "enabled", havingValue = "true")
public class RedisCacheConfiguration {

    @Bean
    RedisL2Cache redisL2Cache(
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            @Value("${identity-reference.cache.redis.key-prefix:identity-ref:v1:}") String keyPrefix,
            @Value("${identity-reference.cache.redis.entry-ttl:24h}") Duration entryTtl) {
        return new RedisIdentityReferenceCache(redis, objectMapper, keyPrefix, entryTtl);
    }
}
