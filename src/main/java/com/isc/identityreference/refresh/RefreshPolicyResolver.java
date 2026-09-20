package com.isc.identityreference.refresh;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
@Component public class RefreshPolicyResolver{
    private final RefreshProperties properties;
    public RefreshPolicyResolver(RefreshProperties properties){this.properties=properties;}
    public RefreshPolicy resolve(String providerId){
        var p=properties.provider(providerId);
        return new RefreshPolicy(providerId,p.getVersion(),p.getTtl(),p.getStaleGrace(),p.getRefreshBeforeExpiry(),
                p.getMaxRequestsPerSecond(),p.getMaxConcurrentRequests(),p.getMaxAttempts(),p.getInitialBackoff(),p.getMaxBackoff(),
                p.getTimeout(),p.getMaxRefreshFailures(),p.getRefreshFailureInitialBackoff(),p.getRefreshFailureMaxBackoff(),
                LocalTime.parse(p.getRefreshWindowStart()),LocalTime.parse(p.getRefreshWindowEnd()));
    }
}