package com.isc.identityreference.refresh;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.util.UUID;

public final class RedisRefreshLeaseManager implements RefreshLeaseManager {
    private final StringRedisTemplate redis; private final String prefix;
    public RedisRefreshLeaseManager(StringRedisTemplate redis,String prefix){this.redis=redis;this.prefix=prefix;}
    public boolean tryAcquire(String key,Duration lease){try{return Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(prefix+key,UUID.randomUUID().toString(),lease));}catch(RuntimeException ex){return false;}}
}