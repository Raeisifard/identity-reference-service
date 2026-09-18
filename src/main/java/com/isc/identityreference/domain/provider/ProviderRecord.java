package com.isc.identityreference.domain.provider;

import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.IdentityAttributes;

import java.time.Instant;
import java.util.Objects;

public record ProviderRecord(
        String providerId,
        String providerRecordId,
        ProviderAuthority authority,
        ProviderRecordState state,
        IdentityAttributes attributes,
        Freshness freshness,
        Instant retrievedAt
) {
    public ProviderRecord {
        requireNonBlank(providerId, "providerId");
        requireNonBlank(providerRecordId, "providerRecordId");
        Objects.requireNonNull(authority, "authority");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(attributes, "attributes");
        Objects.requireNonNull(freshness, "freshness");
        Objects.requireNonNull(retrievedAt, "retrievedAt");
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
