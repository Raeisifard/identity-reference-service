package com.isc.identityreference.domain.lifecycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityLifecycleTest {

    @Test
    void allowsExpectedRefreshFlow() {
        assertTrue(IdentityLifecycle.canTransition(IdentityLifecycleState.ACTIVE, IdentityLifecycleState.REFRESHING));
        assertEquals(IdentityLifecycleState.ACTIVE,
                IdentityLifecycle.transition(IdentityLifecycleState.REFRESHING, IdentityLifecycleState.ACTIVE));
    }

    @Test
    void rejectsIllegalTransitions() {
        assertFalse(IdentityLifecycle.canTransition(IdentityLifecycleState.NEW, IdentityLifecycleState.STALE));
        assertThrows(IllegalStateException.class,
                () -> IdentityLifecycle.transition(IdentityLifecycleState.RETIRED, IdentityLifecycleState.ACTIVE));
    }
}
