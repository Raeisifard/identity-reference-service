package com.isc.identityreference.refresh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public final class RefreshQuotaGuard {
    private final Map<String,Semaphore> concurrency=new ConcurrentHashMap<>(); private final Map<String,Long> lastRequestNanos=new ConcurrentHashMap<>();
    public boolean tryEnter(RefreshPolicy policy){
        Semaphore semaphore=concurrency.computeIfAbsent(policy.providerId(),k->new Semaphore(policy.maxConcurrentRequests()));
        try{
            if(!semaphore.tryAcquire(1,policy.timeout().toMillis(),TimeUnit.MILLISECONDS))return false;
            long interval=1_000_000_000L/Math.max(1,policy.maxRequestsPerSecond()); long now=System.nanoTime();
            long previous=lastRequestNanos.getOrDefault(policy.providerId(),0L); long wait=interval-(now-previous);
            if(wait>0)TimeUnit.NANOSECONDS.sleep(wait); lastRequestNanos.put(policy.providerId(),System.nanoTime()); return true;
        }catch(InterruptedException e){Thread.currentThread().interrupt();return false;}
    }
    public void exit(String providerId){Semaphore s=concurrency.get(providerId);if(s!=null)s.release();}
}