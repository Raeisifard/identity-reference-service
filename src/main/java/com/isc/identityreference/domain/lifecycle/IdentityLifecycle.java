package com.isc.identityreference.domain.lifecycle;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Explicit, side-effect-free lifecycle transition rules. */
public final class IdentityLifecycle {

    private static final Map<IdentityLifecycleState, Set<IdentityLifecycleState>> ALLOWED = Map.of(
            IdentityLifecycleState.NEW, EnumSet.of(IdentityLifecycleState.ACTIVE, IdentityLifecycleState.FAILED, IdentityLifecycleState.RETIRED),
            IdentityLifecycleState.ACTIVE, EnumSet.of(IdentityLifecycleState.REFRESHING, IdentityLifecycleState.STALE, IdentityLifecycleState.RETIRED),
            IdentityLifecycleState.REFRESHING, EnumSet.of(IdentityLifecycleState.ACTIVE, IdentityLifecycleState.STALE, IdentityLifecycleState.FAILED),
            IdentityLifecycleState.STALE, EnumSet.of(IdentityLifecycleState.REFRESHING, IdentityLifecycleState.ACTIVE, IdentityLifecycleState.FAILED, IdentityLifecycleState.RETIRED),
            IdentityLifecycleState.FAILED, EnumSet.of(IdentityLifecycleState.REFRESHING, IdentityLifecycleState.ACTIVE, IdentityLifecycleState.RETIRED),
            IdentityLifecycleState.RETIRED, EnumSet.noneOf(IdentityLifecycleState.class)
    );

    private IdentityLifecycle() {
    }

    public static boolean canTransition(IdentityLifecycleState from, IdentityLifecycleState to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        return ALLOWED.get(from).contains(to);
    }

    public static IdentityLifecycleState transition(IdentityLifecycleState from, IdentityLifecycleState to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("Illegal identity lifecycle transition");
        }
        return to;
    }
}
