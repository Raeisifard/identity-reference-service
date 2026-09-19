package com.isc.identityreference.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity-reference.api.rate-limit")
public class ApiRateLimitProperties {
    private boolean enabled = true;
    private int requestsPerMinute = 60;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(int value) {
        if (value <= 0) throw new IllegalArgumentException("requests-per-minute must be positive");
        this.requestsPerMinute = value;
    }
}
