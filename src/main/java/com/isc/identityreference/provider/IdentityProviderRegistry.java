package com.isc.identityreference.provider;

import com.isc.identityreference.provider.spi.IdentityProvider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Runtime registry of identity providers keyed by their stable provider ID.
 */
public final class IdentityProviderRegistry {
    private final Map<String, IdentityProvider> providers;

    public IdentityProviderRegistry(Collection<? extends IdentityProvider> providers) {
        Objects.requireNonNull(providers, "providers");

        Map<String, IdentityProvider> indexed = new LinkedHashMap<>();
        for (IdentityProvider provider : providers) {
            Objects.requireNonNull(provider, "provider");
            String providerId = provider.descriptor().providerId();

            if (providerId == null || providerId.isBlank()) {
                throw new IllegalArgumentException("Provider ID must not be blank");
            }
            if (indexed.putIfAbsent(providerId, provider) != null) {
                throw new IllegalArgumentException("Duplicate providerId: " + providerId);
            }
        }
        this.providers = Map.copyOf(indexed);
    }

    public Optional<IdentityProvider> find(String providerId) {
        if (providerId == null || providerId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(providers.get(providerId));
    }

    public Collection<IdentityProvider> all() {
        return providers.values();
    }

    public static IdentityProviderRegistry single(IdentityProvider provider) {
        return new IdentityProviderRegistry(List.of(Objects.requireNonNull(provider, "provider")));
    }
}
