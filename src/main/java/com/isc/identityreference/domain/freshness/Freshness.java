package com.isc.identityreference.domain.freshness;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record Freshness(Instant acquiredAt, Instant freshUntil, Instant staleUntil) {

    public Freshness {
        Objects.requireNonNull(acquiredAt, "acquiredAt");
        Objects.requireNonNull(freshUntil, "freshUntil");
        Objects.requireNonNull(staleUntil, "staleUntil");
        if (freshUntil.isBefore(acquiredAt)) {
            throw new IllegalArgumentException("freshUntil must not precede acquiredAt");
        }
        if (staleUntil.isBefore(freshUntil)) {
            throw new IllegalArgumentException("staleUntil must not precede freshUntil");
        }
    }

    public FreshnessState stateAt(Instant now) {
        Objects.requireNonNull(now, "now");
        if (now.isBefore(freshUntil)) {
            return FreshnessState.FRESH;
        }
        if (now.isBefore(staleUntil)) {
            return FreshnessState.STALE;
        }
        return FreshnessState.EXPIRED;
    }

    public Duration ageAt(Instant now) {
        Objects.requireNonNull(now, "now");
        return Duration.between(acquiredAt, now);
    }
}
