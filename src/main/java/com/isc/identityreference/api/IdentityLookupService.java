package com.isc.identityreference.api;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.IdentityProvider;
import com.isc.identityreference.provider.spi.ProviderLookupResult;
import com.isc.identityreference.provider.spi.ProviderLookupStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class IdentityLookupService {
    private final IdentityProviderRegistry providers;

    public IdentityLookupService(IdentityProviderRegistry providers) {
        this.providers = Objects.requireNonNull(providers, "providers");
    }

    public IdentityLookupResponse lookup(IdentityLookupRequest request) {
        IdentityProvider provider = providers.find(request.providerId()).orElse(null);
        if (provider == null) {
            return new IdentityLookupResponse("UNAVAILABLE", request.providerId(),
                    "Identity lookup is temporarily unavailable");
        }

        ProviderLookupResult result = provider.lookup(
                new IdentityLookupKey(request.nationalId(), request.birthDate()));

        if (result.status() == ProviderLookupStatus.FOUND) {
            return new IdentityLookupResponse("FOUND", result.providerId(), "Identity match found");
        }
        if (result.status() == ProviderLookupStatus.NOT_FOUND) {
            return new IdentityLookupResponse("NOT_FOUND", result.providerId(), "No matching identity was found");
        }
        return new IdentityLookupResponse("UNAVAILABLE", result.providerId(),
                "Identity lookup is temporarily unavailable");
    }
}
