package com.isc.identityreference.refresh;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
public final class InMemoryRefreshLeaseManager implements RefreshLeaseManager {
    private record Entry(String token, Instant until) {}
    private final ConcurrentMap<String, Entry> leases=new ConcurrentHashMap<>();
    public Optional<Lease> tryAcquire(String key, Duration lease){
        if(lease==null||lease.isZero()||lease.isNegative())throw new IllegalArgumentException("lease must be positive");
        Instant now=Instant.now(); String token=UUID.randomUUID().toString(); final boolean[] acquired={false};
        leases.compute(key,(k,current)->{
            if(current==null||!current.until().isAfter(now)){acquired[0]=true;return new Entry(token,now.plus(lease));}
            return current;
        });
        return acquired[0]?Optional.of(new Lease(key,token)):Optional.empty();
    }
    public void release(Lease lease){leases.computeIfPresent(lease.key(),(k,current)->current.token().equals(lease.token())?null:current);}
}