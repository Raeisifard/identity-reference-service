package com.isc.identityreference.refresh;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
@ConfigurationProperties(prefix="identity-reference.refresh") public class RefreshProperties{
    private boolean schedulerEnabled=true; private Duration schedulerDelay=Duration.ofMinutes(1); private int batchSize=100; private Duration leaseDuration=Duration.ofSeconds(30);
    private Duration schedulerLookAhead=Duration.ofHours(6); private ZoneId zone=ZoneId.of("UTC"); private boolean staleWhileRefresh=true;
    private int refreshExecutorCorePoolSize=4,refreshExecutorMaxPoolSize=16,refreshExecutorQueueCapacity=1000; private String refreshExecutorThreadNamePrefix="identity-refresh-";
    private int providerExecutorCorePoolSize=8,providerExecutorMaxPoolSize=64; private String providerExecutorThreadNamePrefix="identity-provider-";
    private Map<String,Provider> providers=new LinkedHashMap<>();
    public boolean isSchedulerEnabled(){return schedulerEnabled;} public void setSchedulerEnabled(boolean v){schedulerEnabled=v;}
    public Duration getSchedulerDelay(){return schedulerDelay;} public void setSchedulerDelay(Duration v){schedulerDelay=v;}
    public int getBatchSize(){return batchSize;} public void setBatchSize(int v){batchSize=v;}
    public Duration getLeaseDuration(){return leaseDuration;} public void setLeaseDuration(Duration v){leaseDuration=v;}
    public Duration getSchedulerLookAhead(){return schedulerLookAhead;} public void setSchedulerLookAhead(Duration v){schedulerLookAhead=v;}
    public ZoneId getZone(){return zone;} public void setZone(ZoneId v){zone=v;}
    public boolean isStaleWhileRefresh(){return staleWhileRefresh;} public void setStaleWhileRefresh(boolean v){staleWhileRefresh=v;}
    public int getRefreshExecutorCorePoolSize(){return refreshExecutorCorePoolSize;} public void setRefreshExecutorCorePoolSize(int v){refreshExecutorCorePoolSize=v;}
    public int getRefreshExecutorMaxPoolSize(){return refreshExecutorMaxPoolSize;} public void setRefreshExecutorMaxPoolSize(int v){refreshExecutorMaxPoolSize=v;}
    public int getRefreshExecutorQueueCapacity(){return refreshExecutorQueueCapacity;} public void setRefreshExecutorQueueCapacity(int v){refreshExecutorQueueCapacity=v;}
    public String getRefreshExecutorThreadNamePrefix(){return refreshExecutorThreadNamePrefix;} public void setRefreshExecutorThreadNamePrefix(String v){refreshExecutorThreadNamePrefix=v;}
    public int getProviderExecutorCorePoolSize(){return providerExecutorCorePoolSize;} public void setProviderExecutorCorePoolSize(int v){providerExecutorCorePoolSize=v;}
    public int getProviderExecutorMaxPoolSize(){return providerExecutorMaxPoolSize;} public void setProviderExecutorMaxPoolSize(int v){providerExecutorMaxPoolSize=v;}
    public String getProviderExecutorThreadNamePrefix(){return providerExecutorThreadNamePrefix;} public void setProviderExecutorThreadNamePrefix(String v){providerExecutorThreadNamePrefix=v;}
    public Map<String,Provider> getProviders(){return providers;} public void setProviders(Map<String,Provider> v){providers=new LinkedHashMap<>(v);}
    public Provider provider(String id){return providers.getOrDefault(id,new Provider());}
    public Duration effectiveSchedulerLookAhead(){return providers.values().stream().map(Provider::getRefreshBeforeExpiry).max(Duration::compareTo).map(max->max.compareTo(schedulerLookAhead)>0?max:schedulerLookAhead).orElse(schedulerLookAhead);}
    public static class Provider{
        private long version=1; private Duration ttl=Duration.ofHours(24),staleGrace=Duration.ofHours(12),refreshBeforeExpiry=Duration.ofHours(6);
        private long maxRequestsPerSecond=10; private int maxConcurrentRequests=2,maxAttempts=2; private Duration initialBackoff=Duration.ofSeconds(1),maxBackoff=Duration.ofSeconds(4),timeout=Duration.ofSeconds(5);
        private int maxRefreshFailures=5; private Duration refreshFailureInitialBackoff=Duration.ofMinutes(1),refreshFailureMaxBackoff=Duration.ofMinutes(30);
        private String refreshWindowStart="00:00",refreshWindowEnd="06:00";
        public long getVersion(){return version;} public void setVersion(long v){version=v;} public Duration getTtl(){return ttl;} public void setTtl(Duration v){ttl=v;}
        public Duration getStaleGrace(){return staleGrace;} public void setStaleGrace(Duration v){staleGrace=v;} public Duration getRefreshBeforeExpiry(){return refreshBeforeExpiry;} public void setRefreshBeforeExpiry(Duration v){refreshBeforeExpiry=v;}
        public long getMaxRequestsPerSecond(){return maxRequestsPerSecond;} public void setMaxRequestsPerSecond(long v){maxRequestsPerSecond=v;} public int getMaxConcurrentRequests(){return maxConcurrentRequests;} public void setMaxConcurrentRequests(int v){maxConcurrentRequests=v;}
        public int getMaxAttempts(){return maxAttempts;} public void setMaxAttempts(int v){maxAttempts=v;} public Duration getInitialBackoff(){return initialBackoff;} public void setInitialBackoff(Duration v){initialBackoff=v;}
        public Duration getMaxBackoff(){return maxBackoff;} public void setMaxBackoff(Duration v){maxBackoff=v;} public Duration getTimeout(){return timeout;} public void setTimeout(Duration v){timeout=v;}
        public int getMaxRefreshFailures(){return maxRefreshFailures;} public void setMaxRefreshFailures(int v){maxRefreshFailures=v;} public Duration getRefreshFailureInitialBackoff(){return refreshFailureInitialBackoff;} public void setRefreshFailureInitialBackoff(Duration v){refreshFailureInitialBackoff=v;}
        public Duration getRefreshFailureMaxBackoff(){return refreshFailureMaxBackoff;} public void setRefreshFailureMaxBackoff(Duration v){refreshFailureMaxBackoff=v;}
        public String getRefreshWindowStart(){return refreshWindowStart;} public void setRefreshWindowStart(String v){refreshWindowStart=v;} public String getRefreshWindowEnd(){return refreshWindowEnd;} public void setRefreshWindowEnd(String v){refreshWindowEnd=v;}
    }
}