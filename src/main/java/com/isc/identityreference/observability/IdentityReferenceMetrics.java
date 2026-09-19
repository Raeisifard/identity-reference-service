package com.isc.identityreference.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class IdentityReferenceMetrics {
    private final MeterRegistry registry;
    public IdentityReferenceMetrics(MeterRegistry registry) { this.registry = registry; }

    public Timer lookupTimer(String providerId) {
        return Timer.builder("identity_reference_lookup_duration")
                .description("Identity lookup duration").tag("provider", safe(providerId)).register(registry);
    }

    public void lookup(String providerId, String outcome) {
        Counter.builder("identity_reference_lookup_total").description("Identity lookup requests")
                .tag("provider", safe(providerId)).tag("outcome", safe(outcome)).register(registry).increment();
    }

    public void rateLimited() {
        Counter.builder("identity_reference_api_rate_limited_total")
                .description("API requests rejected by the rate limiter").register(registry).increment();
    }

    public void refreshAccepted(String providerId) {
        Counter.builder("identity_reference_refresh_total").description("Administrative refresh requests")
                .tag("provider", safe(providerId)).tag("outcome", "accepted").register(registry).increment();
    }

    private static String safe(String value) { return value == null || value.isBlank() ? "unknown" : value; }
}
