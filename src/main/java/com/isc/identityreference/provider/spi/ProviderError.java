package com.isc.identityreference.provider.spi;

import java.util.Objects;

public record ProviderError(ProviderErrorCode code, boolean retryable, String safeMessage) {
    public ProviderError {
        Objects.requireNonNull(code, "code");
        if (safeMessage == null || safeMessage.isBlank()) throw new IllegalArgumentException("safeMessage must not be blank");
    }
}
