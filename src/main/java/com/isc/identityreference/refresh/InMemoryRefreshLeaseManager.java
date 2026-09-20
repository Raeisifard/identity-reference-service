package com.isc.identityreference.refresh;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryRefreshLeaseManager implements RefreshLeaseManager {
    private final ConcurrentMap<String,Instant> leases=new ConcurrentHashMap<>();
    public boolean tryAcquire(String key,Duration lease){
        Instant now=Instant.now(),until=now.plus(lease); final boolean[] acquired={false};
        leases.compute(key,(k,v)->{if(v==null||!v.isAfter(now)){acquired[0]=true;return until;}return v;});
        return acquired[0];
    }
}