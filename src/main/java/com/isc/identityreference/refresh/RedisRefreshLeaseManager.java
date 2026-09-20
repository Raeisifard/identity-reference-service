package com.isc.identityreference.refresh;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public final class RedisRefreshLeaseManager implements RefreshLeaseManager {
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>(
        "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end", Long.class);
    private final StringRedisTemplate redis; private final String prefix;
    public RedisRefreshLeaseManager(StringRedisTemplate redis, String prefix) { this.redis=redis; this.prefix=prefix; }
    public Optional<Lease> tryAcquire(String key, Duration lease) {
        String token=UUID.randomUUID().toString();
        try { boolean acquired=Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(prefix+key,token,lease)); return acquired?Optional.of(new Lease(key,token)):Optional.empty(); }
        catch(RuntimeException ex){ return Optional.empty(); }
    }
    public void release(Lease lease) {
        try { redis.execute(RELEASE_SCRIPT,List.of(prefix+lease.key()),lease.token()); } catch(RuntimeException ignored) {}
    }
}