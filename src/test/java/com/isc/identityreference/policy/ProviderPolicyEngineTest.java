package com.isc.identityreference.policy;

import com.isc.identityreference.domain.provider.ProviderAuthority;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProviderPolicyEngineTest {

    @Test void buildsFreshnessFromPolicy() {
        ProviderPolicy p = policy(Duration.ofHours(24), Duration.ofHours(12));
        Instant acquired = Instant.parse("2026-01-01T00:00:00Z");
        var freshness = new ProviderPolicyEngine().freshnessFrom(p, acquired);
        assertEquals(acquired.plus(Duration.ofHours(24)), freshness.freshUntil());
        assertEquals(acquired.plus(Duration.ofHours(36)), freshness.staleUntil());
    }

    @Test void midnightWindowWrapsAcrossDayBoundary() {
        var window = new ProviderPolicy.RefreshWindow(LocalTime.of(23, 0), LocalTime.of(2, 0));
        assertTrue(window.contains(LocalTime.of(23, 30)));
        assertTrue(window.contains(LocalTime.of(1, 30)));
        assertFalse(window.contains(LocalTime.of(12, 0)));
    }

    @Test void overwriteNeverAllowsNoReplacement() {
        var p = policy(Duration.ofHours(1), Duration.ZERO, ProviderPolicy.OverwriteRule.NEVER);
        var engine = new ProviderPolicyEngine();
        assertFalse(engine.canOverwrite(p, ProviderAuthority.SECONDARY, ProviderAuthority.AUTHORITATIVE));
    }

    @Test void lowerAuthorityCannotOverwriteHigherAuthority() {
        var p = policy(Duration.ofHours(1), Duration.ZERO, ProviderPolicy.OverwriteRule.SAME_OR_HIGHER_AUTHORITY);
        var engine = new ProviderPolicyEngine();
        assertFalse(engine.canOverwrite(p, ProviderAuthority.AUTHORITATIVE, ProviderAuthority.SECONDARY));
        assertTrue(engine.canOverwrite(p, ProviderAuthority.SECONDARY, ProviderAuthority.VERIFIED));
    }

    @Test void policyValidatesEnabledEmbeddingContract() {
        assertThrows(IllegalArgumentException.class, () -> new ProviderPolicy(
                "mock", 1, true, ProviderAuthority.AUTHORITATIVE,
                Set.of(ProviderPolicy.ProviderClaim.NATIONAL_ID_AND_BIRTH_DATE),
                Map.of(), Duration.ofHours(1), Duration.ZERO, List.of(), Duration.ZERO,
                new ProviderPolicy.RequestQuota(1, 1),
                new ProviderPolicy.RetryPolicy(1, Duration.ofSeconds(1), Duration.ofSeconds(1)),
                Duration.ofSeconds(2), ProviderPolicy.OverwriteRule.EXPLICIT_POLICY,
                new ProviderPolicy.PhotoPolicy(false, false),
                new ProviderPolicy.EmbeddingPolicy(true, "", "1", 512)));
    }

    private static ProviderPolicy policy(Duration ttl, Duration stale) {
        return policy(ttl, stale, ProviderPolicy.OverwriteRule.SAME_OR_HIGHER_AUTHORITY);
    }

    private static ProviderPolicy policy(Duration ttl, Duration stale, ProviderPolicy.OverwriteRule rule) {
        return new ProviderPolicy(
                "mock", 1, true, ProviderAuthority.AUTHORITATIVE,
                Set.of(ProviderPolicy.ProviderClaim.NATIONAL_ID_AND_BIRTH_DATE),
                Map.of(ProviderPolicy.IdentityField.NATIONAL_ID, ProviderPolicy.FieldOwnership.OWNED),
                ttl, stale,
                List.of(new ProviderPolicy.RefreshWindow(LocalTime.MIDNIGHT, LocalTime.of(6, 0))),
                Duration.ofMinutes(5),
                new ProviderPolicy.RequestQuota(10, 2),
                new ProviderPolicy.RetryPolicy(3, Duration.ofSeconds(1), Duration.ofSeconds(8)),
                Duration.ofSeconds(5), rule,
                new ProviderPolicy.PhotoPolicy(false, false),
                new ProviderPolicy.EmbeddingPolicy(false, null, null, 0));
    }
}
