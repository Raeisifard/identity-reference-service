package com.isc.identityreference.refresh;
import java.time.Duration;
import java.time.LocalTime;
public record RefreshPolicy(String providerId,long version,Duration ttl,Duration staleGrace,Duration refreshBeforeExpiry,
                            long maxRequestsPerSecond,int maxConcurrentRequests,int maxAttempts,Duration initialBackoff,
                            Duration maxBackoff,Duration timeout,int maxRefreshFailures,Duration refreshFailureInitialBackoff,
                            Duration refreshFailureMaxBackoff,LocalTime windowStart,LocalTime windowEnd){
    public boolean inWindow(LocalTime time){
        if(windowStart.equals(windowEnd))return true;
        if(windowStart.isBefore(windowEnd))return !time.isBefore(windowStart)&&time.isBefore(windowEnd);
        return !time.isBefore(windowStart)||time.isBefore(windowEnd);
    }
}