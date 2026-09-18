package com.isc.identityreference.scheduler;

import com.isc.identityreference.policy.ProviderPolicy;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

public final class RefreshScheduler {
    private final RefreshSchedulePlanner planner;
    private final RefreshLeaseManager leaseManager;

    public RefreshScheduler(RefreshSchedulePlanner planner, RefreshLeaseManager leaseManager) {
        this.planner = Objects.requireNonNull(planner, "planner");
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager");
    }

    public boolean shouldDispatch(String identityReferenceId, String providerId,
                                  Instant now, ZoneId zone, ProviderPolicy policy,
                                  Instant dueAt, boolean dryRun) {
        var decision = planner.plan(now, zone, dueAt, policy, 1);
        if (!decision.ready() || dryRun) return decision.ready();
        return leaseManager.tryAcquire(identityReferenceId, providerId, now,
                policy.timeout().plus(Duration.ofSeconds(5)));
    }
}
