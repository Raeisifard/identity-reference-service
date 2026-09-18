package com.isc.identityreference.scheduler;

import com.isc.identityreference.policy.ProviderPolicy;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RefreshSchedulePlannerTest {
    @Test void defersOutsideQuietWindow() {
        var policy = policy(List.of(new ProviderPolicy.RefreshWindow(LocalTime.of(0,0), LocalTime.of(6,0))));
        var planner = new RefreshSchedulePlanner();
        var now = Instant.parse("2026-09-18T12:00:00Z");
        var decision = planner.plan(now, ZoneOffset.UTC, now, policy, 1);
        assertFalse(decision.ready());
        assertEquals("outside-refresh-window", decision.reason());
        assertEquals(Instant.parse("2026-09-19T00:00:00Z"), decision.scheduledAt());
    }

    @Test void limitsDispatchToProviderQuota() {
        var policy = policy(List.of());
        var planner = new RefreshSchedulePlanner();
        var decision = planner.plan(Instant.parse("2026-09-18T00:00:00Z"), ZoneOffset.UTC,
                Instant.parse("2026-09-18T00:00:00Z"), policy, 100);
        assertTrue(decision.ready());
        assertEquals(2, decision.selectedCount());
        assertEquals("provider-quota-bound", decision.reason());
    }

    @Test void dryRunDoesNotRequireLease() {
        var lease = (RefreshLeaseManager) (id,p,now,d) -> { throw new AssertionError("lease must not be called"); };
        var scheduler = new RefreshScheduler(new RefreshSchedulePlanner(), lease);
        assertTrue(scheduler.shouldDispatch("id","provider",Instant.parse("2026-09-18T01:00:00Z"),
                ZoneOffset.UTC, policy(List.of()), Instant.parse("2026-09-18T01:00:00Z"), true));
    }

    private static ProviderPolicy policy(List<ProviderPolicy.RefreshWindow> windows) {
        return new ProviderPolicy("mock-provider",1,true,com.isc.identityreference.domain.provider.ProviderAuthority.AUTHORITATIVE,
                Set.of(ProviderPolicy.ProviderClaim.NATIONAL_ID_AND_BIRTH_DATE),Map.of(),
                Duration.ofHours(24),Duration.ofHours(12),windows,Duration.ZERO,
                new ProviderPolicy.RequestQuota(10,2),
                new ProviderPolicy.RetryPolicy(2,Duration.ofSeconds(1),Duration.ofSeconds(4)),
                Duration.ofSeconds(5),ProviderPolicy.OverwriteRule.SAME_OR_HIGHER_AUTHORITY,
                new ProviderPolicy.PhotoPolicy(false,false),new ProviderPolicy.EmbeddingPolicy(false,null,null,0));
    }
}
