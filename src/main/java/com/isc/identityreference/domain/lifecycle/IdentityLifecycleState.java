package com.isc.identityreference.domain.lifecycle;

/** Lifecycle state of an identity reference. */
public enum IdentityLifecycleState {
    NEW,
    ACTIVE,
    REFRESHING,
    STALE,
    FAILED,
    RETIRED;

    public boolean isTerminal() {
        return this == RETIRED;
    }
}
