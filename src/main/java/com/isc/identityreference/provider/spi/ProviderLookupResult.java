package com.isc.identityreference.provider.spi;

import com.isc.identityreference.domain.identity.IdentityAttributes;
import com.isc.identityreference.domain.provider.ProviderAuthority;

import java.time.Instant;
import java.util.Objects;

/** Normalized provider result; provider-specific payloads must not cross this boundary. */
public record ProviderLookupResult(
        String providerId,
        String providerRecordId,
        ProviderAuthority authority,
        ProviderLookupStatus status,
        IdentityAttributes attributes,
        Instant retrievedAt,
        ProviderError error) {

    public ProviderLookupResult {
        requireNonBlank(providerId, "providerId");
        requireNonBlank(providerRecordId, "providerRecordId");
        Objects.requireNonNull(authority, "authority");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(retrievedAt, "retrievedAt");

        if (status == ProviderLookupStatus.FOUND && attributes == null) {
            throw new IllegalArgumentException("attributes are required for FOUND result");
        }

        if (status != ProviderLookupStatus.FOUND && attributes != null) {
            throw new IllegalArgumentException("attributes are allowed only for FOUND result");
        }

        if (status == ProviderLookupStatus.ERROR && error == null) {
            throw new IllegalArgumentException("error is required for ERROR result");
        }

        if (status != ProviderLookupStatus.ERROR && error != null) {
            throw new IllegalArgumentException("error is allowed only for ERROR result");
        }
    }

    public static ProviderLookupResult found(
            String providerId,
            String recordId,
            ProviderAuthority authority,
            IdentityAttributes attributes,
            Instant retrievedAt) {

        return new ProviderLookupResult(
                providerId,
                recordId,
                authority,
                ProviderLookupStatus.FOUND,
                Objects.requireNonNull(attributes, "attributes"),
                retrievedAt,
                null);
    }

    public static ProviderLookupResult notFound(
            String providerId,
            String recordId,
            ProviderAuthority authority,
            Instant retrievedAt) {

        return new ProviderLookupResult(
                providerId,
                recordId,
                authority,
                ProviderLookupStatus.NOT_FOUND,
                null,
                retrievedAt,
                null);
    }

    public static ProviderLookupResult error(
            String providerId,
            String recordId,
            ProviderAuthority authority,
            ProviderError error,
            Instant retrievedAt) {

        return new ProviderLookupResult(
                providerId,
                recordId,
                authority,
                ProviderLookupStatus.ERROR,
                null,
                retrievedAt,
                Objects.requireNonNull(error, "error"));
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}