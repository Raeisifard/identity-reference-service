package com.isc.identityreference.observability;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.IdentityProvider;
import com.isc.identityreference.provider.spi.ProviderDescriptor;
import com.isc.identityreference.provider.spi.ProviderLookupResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderHealthIndicatorTest {
    @Test void reportsUpWhenProviderRegistryIsConfigured() {
        IdentityProvider provider = new TestIdentityProvider();
        var health = new ProviderHealthIndicator(IdentityProviderRegistry.single(provider)).health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("providerCount", 1);
    }

    private static final class TestIdentityProvider implements IdentityProvider {

        @Override
        public ProviderDescriptor descriptor() {
            return new ProviderDescriptor("test-provider", "Test Provider");
        }

        @Override
        public ProviderLookupResult lookup(IdentityLookupKey key) {
            return ProviderLookupResult.notFound(
                    "test-provider",
                    "test-record",
                    ProviderAuthority.AUTHORITATIVE,
                    Instant.now()
            );
        }
    }
}
