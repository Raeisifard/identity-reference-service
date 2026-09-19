package com.isc.identityreference.observability;

import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.IdentityProvider;
import com.isc.identityreference.provider.spi.ProviderDescriptor;
import com.isc.identityreference.provider.spi.ProviderLookupResult;
import com.isc.identityreference.provider.spi.ProviderLookupStatus;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderHealthIndicatorTest {
    @Test void reportsUpWhenProviderRegistryIsConfigured() {
        IdentityProvider provider = new TestIdentityProvider();
        var health = new ProviderHealthIndicator(IdentityProviderRegistry.single(provider)).health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("providerCount", 1);
    }

    private static final class TestIdentityProvider implements IdentityProvider {
        public IdentityProviderDescriptor descriptor() { return new ProviderDescriptor("test-provider", "Test Provider"); }
        public ProviderLookupResult lookup(IdentityLookupKey key) {
            return new ProviderLookupResult("test-provider", ProviderLookupStatus.NOT_FOUND);
        }
    }
}
