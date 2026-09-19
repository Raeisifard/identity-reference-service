package com.isc.identityreference.observability;

import com.isc.identityreference.provider.IdentityProviderRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("identityProviders")
public class ProviderHealthIndicator implements HealthIndicator {
    private final IdentityProviderRegistry providers;
    public ProviderHealthIndicator(IdentityProviderRegistry providers) { this.providers = providers; }

    @Override
    public Health health() {
        var ids = providers.all().stream().map(p -> p.descriptor().providerId()).sorted().toList();
        if (ids.isEmpty()) return Health.down().withDetail("providerCount", 0).build();
        return Health.up().withDetail("providerCount", ids.size()).withDetail("providers", ids).build();
    }
}
