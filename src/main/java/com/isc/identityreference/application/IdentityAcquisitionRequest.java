package com.isc.identityreference.application;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import java.util.Objects;

public record IdentityAcquisitionRequest(
        IdentityLookupKey lookupKey,
        String providerId,
        String idempotencyKey) {
    public IdentityAcquisitionRequest {
        Objects.requireNonNull(lookupKey, "lookupKey");
        requireNonBlank(providerId, "providerId");
        requireNonBlank(idempotencyKey, "idempotencyKey");
    }
    private static void requireNonBlank(String v, String n) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " must not be blank");
    }
}
