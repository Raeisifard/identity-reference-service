package com.isc.identityreference.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity-reference.operational")
public class ObservabilityProperties {
    private boolean requestLoggingEnabled = true;
    public boolean isRequestLoggingEnabled() { return requestLoggingEnabled; }
    public void setRequestLoggingEnabled(boolean requestLoggingEnabled) { this.requestLoggingEnabled = requestLoggingEnabled; }
}
