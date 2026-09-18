package com.isc.identityreference.application;

import com.isc.identityreference.domain.identity.IdentityReference;
import java.util.Objects;

public record IdentityAcquisitionResult(Status status, IdentityReference reference) {
    public IdentityAcquisitionResult {
        Objects.requireNonNull(status, "status");
        if (status == Status.ACQUIRED && reference == null) throw new IllegalArgumentException("reference required");
    }
    public enum Status { ACQUIRED, NOT_FOUND, FAILED, IDEMPOTENT_REPLAY }
}
