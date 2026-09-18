package com.isc.identityreference.scheduler;

import java.time.Instant;
import java.util.Objects;

public record RefreshWorkItem(String identityReferenceId, String providerId, Instant scheduledAt) {
    public RefreshWorkItem {
        requireNonBlank(identityReferenceId, "identityReferenceId");
        requireNonBlank(providerId, "providerId");
        Objects.requireNonNull(scheduledAt, "scheduledAt");
    }
    private static void requireNonBlank(String v, String n) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " must not be blank");
    }
}
