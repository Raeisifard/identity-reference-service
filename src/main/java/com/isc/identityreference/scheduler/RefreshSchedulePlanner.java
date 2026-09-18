package com.isc.identityreference.scheduler;

import com.isc.identityreference.policy.ProviderPolicy;
import java.time.*;
import java.util.Objects;

public final class RefreshSchedulePlanner {

    public RefreshDecision plan(Instant now, ZoneId zone, Instant dueAt,
                                ProviderPolicy policy, int requestedCount) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(dueAt, "dueAt");
        Objects.requireNonNull(policy, "policy");
        if (requestedCount < 0) throw new IllegalArgumentException("requestedCount must not be negative");

        ZonedDateTime local = now.atZone(zone);
        boolean inWindow = policy.refreshWindows().isEmpty()
                || policy.refreshWindows().stream().anyMatch(w -> w.contains(local.toLocalTime()));

        if (!inWindow) {
            return new RefreshDecision(false, nextWindowStart(local, policy), 0, "outside-refresh-window");
        }

        int quotaBound = (int) Math.min(Integer.MAX_VALUE,
                Math.min(policy.quota().maxConcurrentRequests(), policy.quota().maxRequestsPerSecond()));
        int selected = Math.min(requestedCount, quotaBound);
        return new RefreshDecision(true, dueAt, selected,
                selected < requestedCount ? "provider-quota-bound" : "ready");
    }

    private Instant nextWindowStart(ZonedDateTime now, ProviderPolicy policy) {
        if (policy.refreshWindows().isEmpty()) return now.toInstant();
        ZonedDateTime best = null;
        for (ProviderPolicy.RefreshWindow window : policy.refreshWindows()) {
            ZonedDateTime candidate = now.withHour(window.start().getHour())
                    .withMinute(window.start().getMinute()).withSecond(0).withNano(0);
            if (!candidate.isAfter(now)) candidate = candidate.plusDays(1);
            if (best == null || candidate.isBefore(best)) best = candidate;
        }
        return best.toInstant();
    }

    public record RefreshDecision(boolean ready, Instant scheduledAt, int selectedCount, String reason) {}
}
