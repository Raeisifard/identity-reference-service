package com.isc.identityreference.policy;

import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

public final class ProviderPolicyEngine {

    public Freshness freshnessFrom(ProviderPolicy policy, Instant acquiredAt) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(acquiredAt, "acquiredAt");
        return new Freshness(acquiredAt, acquiredAt.plus(policy.ttl()), acquiredAt.plus(policy.ttl()).plus(policy.staleGrace()));
    }

    public boolean canOverwrite(ProviderPolicy policy, ProviderAuthority existing, ProviderAuthority incoming) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(existing, "existing");
        Objects.requireNonNull(incoming, "incoming");
        return switch (policy.overwriteRule()) {
            case NEVER -> false;
            case SAME_OR_HIGHER_AUTHORITY -> authorityRank(incoming) >= authorityRank(existing);
            case EXPLICIT_POLICY -> policy.enabled() && policy.authority() == incoming;
        };
    }

    public boolean isWithinRefreshWindow(ProviderPolicy policy, LocalTime time) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(time, "time");
        return policy.refreshWindows().isEmpty()
                || policy.refreshWindows().stream().anyMatch(window -> window.contains(time));
    }

    private int authorityRank(ProviderAuthority authority) {
        return switch (authority) {
            case UNKNOWN -> 0;
            case SECONDARY -> 1;
            case VERIFIED -> 2;
            case AUTHORITATIVE -> 3;
        };
    }
}
