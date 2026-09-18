package com.isc.identityreference.domain.freshness;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreshnessTest {

    @Test
    void classifiesFreshStaleAndExpiredUsingExplicitClock() {
        Instant acquired = Instant.parse("2026-01-01T00:00:00Z");
        Freshness freshness = new Freshness(acquired,
                acquired.plusSeconds(60), acquired.plusSeconds(120));

        assertEquals(FreshnessState.FRESH, freshness.stateAt(acquired.plusSeconds(59)));
        assertEquals(FreshnessState.STALE, freshness.stateAt(acquired.plusSeconds(60)));
        assertEquals(FreshnessState.EXPIRED, freshness.stateAt(acquired.plusSeconds(120)));
    }
}
