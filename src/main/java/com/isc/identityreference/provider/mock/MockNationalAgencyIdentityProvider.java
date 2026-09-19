package com.isc.identityreference.provider.mock;

import com.isc.identityreference.domain.identity.IdentityAttributes;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.provider.spi.IdentityProvider;
import com.isc.identityreference.provider.spi.ProviderDescriptor;
import com.isc.identityreference.provider.spi.ProviderError;
import com.isc.identityreference.provider.spi.ProviderErrorCode;
import com.isc.identityreference.provider.spi.ProviderLookupResult;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Deterministic in-process adapter for local development and tests.
 * It uses only synthetic fixtures and has no external endpoint or credentials.
 */
public final class MockNationalAgencyIdentityProvider implements IdentityProvider {
    private final ProviderDescriptor descriptor;
    private final boolean enabled;

    public MockNationalAgencyIdentityProvider(String providerId, String displayName, boolean enabled) {
        descriptor = new ProviderDescriptor(providerId, displayName);
        this.enabled = enabled;
    }

    public boolean enabled() { return enabled; }

    @Override
    public ProviderDescriptor descriptor() { return descriptor; }

    @Override
    public ProviderLookupResult lookup(IdentityLookupKey lookupKey) {
        Objects.requireNonNull(lookupKey, "lookupKey");
        Instant retrievedAt = Instant.now();

        if (!enabled) {
            return ProviderLookupResult.error(
                    descriptor.providerId(), "disabled", ProviderAuthority.UNKNOWN,
                    new ProviderError(ProviderErrorCode.AUTHORIZATION_FAILED, false, "Provider adapter is disabled"),
                    retrievedAt);
        }

        return switch (lookupKey.nationalId()) {
            case "FIXTURE-FOUND-001" -> found(lookupKey, retrievedAt);
            case "FIXTURE-NOT-FOUND" -> ProviderLookupResult.notFound(
                    descriptor.providerId(), "none", ProviderAuthority.AUTHORITATIVE, retrievedAt);
            case "FIXTURE-ERROR" -> ProviderLookupResult.error(
                    descriptor.providerId(), "synthetic-error", ProviderAuthority.UNKNOWN,
                    new ProviderError(ProviderErrorCode.UNAVAILABLE, true, "Synthetic provider outage"),
                    retrievedAt);
            default -> ProviderLookupResult.notFound(
                    descriptor.providerId(), "none", ProviderAuthority.AUTHORITATIVE, retrievedAt);
        };
    }

    private ProviderLookupResult found(IdentityLookupKey key, Instant retrievedAt) {
        if (!LocalDate.of(1990, 1, 1).equals(key.birthDate())) {
            return ProviderLookupResult.notFound(
                    descriptor.providerId(), "none", ProviderAuthority.AUTHORITATIVE, retrievedAt);
        }

        IdentityAttributes attributes = new IdentityAttributes(
                "Synthetic", "Citizen", "Example", key.birthDate(), "U", key.nationalId());

        return ProviderLookupResult.found(
                descriptor.providerId(), "FIXTURE-RECORD-001",
                ProviderAuthority.AUTHORITATIVE, attributes, retrievedAt);
    }
}
