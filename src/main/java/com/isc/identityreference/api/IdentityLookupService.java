package com.isc.identityreference.api;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.IdentityProvider;
import com.isc.identityreference.provider.spi.ProviderLookupResult;
import com.isc.identityreference.provider.spi.ProviderLookupStatus;
import com.isc.identityreference.observability.IdentityReferenceMetrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class IdentityLookupService {
    private final IdentityProviderRegistry providers;
    private final IdentityReferenceMetrics metrics;

    public IdentityLookupService(IdentityProviderRegistry providers, IdentityReferenceMetrics metrics) {
        this.providers = Objects.requireNonNull(providers, "providers");
        this.metrics = Objects.requireNonNull(metrics, "metrics");
    }

    public IdentityLookupResponse lookup(IdentityLookupRequest request) {
        String requestedProvider = request.providerId();
        Timer.Sample sample = Timer.start();
        IdentityProvider provider = providers.find(request.providerId()).orElse(null);
        if (provider == null) {
            metrics.lookup(requestedProvider, "UNAVAILABLE");
            sample.stop(metrics.lookupTimer(requestedProvider));
            return new IdentityLookupResponse("UNAVAILABLE", request.providerId(),
                    "Identity lookup is temporarily unavailable");
        }

        ProviderLookupResult result = provider.lookup(
                new IdentityLookupKey(request.nationalId(), request.birthDate()));

        if (result.status() == ProviderLookupStatus.FOUND) {
            metrics.lookup(result.providerId(), "FOUND");
            sample.stop(metrics.lookupTimer(result.providerId()));
            return new IdentityLookupResponse("FOUND", result.providerId(), "Identity match found");
        }
        if (result.status() == ProviderLookupStatus.NOT_FOUND) {
            metrics.lookup(result.providerId(), "NOT_FOUND");
            sample.stop(metrics.lookupTimer(result.providerId()));
            return new IdentityLookupResponse("NOT_FOUND", result.providerId(), "No matching identity was found");
        }
        metrics.lookup(result.providerId(), "UNAVAILABLE");
        sample.stop(metrics.lookupTimer(result.providerId()));
        return new IdentityLookupResponse("UNAVAILABLE", result.providerId(),
                "Identity lookup is temporarily unavailable");
    }
}
