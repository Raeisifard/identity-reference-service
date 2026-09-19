package com.isc.identityreference.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class ApiRateLimitGuardTest {
    @Test
    void rejectsAfterConfiguredWindowLimit() {
        ApiRateLimitProperties properties = new ApiRateLimitProperties();
        properties.setRequestsPerMinute(2);
        ApiRateLimitGuard guard = new ApiRateLimitGuard(properties,
                Clock.fixed(Instant.parse("2026-09-19T10:00:00Z"), ZoneOffset.UTC));

        assertTrue(guard.allow("lookup-user"));
        assertTrue(guard.allow("lookup-user"));
        assertFalse(guard.allow("lookup-user"));
    }
}
