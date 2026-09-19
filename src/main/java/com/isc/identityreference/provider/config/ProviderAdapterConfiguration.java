package com.isc.identityreference.provider.config;

import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.mock.MockNationalAgencyIdentityProvider;
import com.isc.identityreference.provider.spi.IdentityProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ProviderAdapterProperties.class)
public class ProviderAdapterConfiguration {

    @Bean
    MockNationalAgencyIdentityProvider mockNationalAgencyIdentityProvider(ProviderAdapterProperties properties) {
        ProviderAdapterProperties.Adapter adapter = properties.getAdapters().stream()
                .filter(candidate -> "mock-national-agency".equalsIgnoreCase(candidate.getType()))
                .findFirst()
                .orElse(null);

        boolean enabled = adapter != null && adapter.isEnabled();
        String providerId = adapter == null || adapter.getProviderId() == null || adapter.getProviderId().isBlank()
                ? "mock-national-agency" : adapter.getProviderId();
        String displayName = adapter == null || adapter.getDisplayName() == null || adapter.getDisplayName().isBlank()
                ? "Mock National Agency" : adapter.getDisplayName();

        return new MockNationalAgencyIdentityProvider(providerId, displayName, enabled);
    }

    @Bean
    IdentityProviderRegistry identityProviderRegistry(
            ProviderAdapterProperties properties,
            MockNationalAgencyIdentityProvider mockProvider) {
        List<IdentityProvider> providers = new ArrayList<>();
        if (!properties.isRealIntegrationsEnabled() && mockProvider.enabled()) {
            providers.add(mockProvider);
        }
        return new IdentityProviderRegistry(providers);
    }
}
