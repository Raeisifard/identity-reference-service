package com.isc.identityreference.domain.identity;

import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentityReferenceTest {

    @Test
    void transitionsWithoutMutationAndPreservesIdentity() {
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        IdentityAttributes attributes = new IdentityAttributes("Ada", "Lovelace", null,
                LocalDate.of(1815, 12, 10), null, "NATIONAL-ID");
        Freshness freshness = new Freshness(created, created.plusSeconds(60), created.plusSeconds(120));
        IdentityReference original = new IdentityReference(
                IdentityReferenceId.newId(),
                new IdentityLookupKey("NATIONAL-ID", LocalDate.of(1815, 12, 10)),
                attributes, List.of(), List.of(), IdentityLifecycleState.NEW,
                freshness, created, created);

        IdentityReference active = original.transitionTo(
                IdentityLifecycleState.ACTIVE, created.plusSeconds(1));

        assertEquals(original.id(), active.id());
        assertEquals(IdentityLifecycleState.NEW, original.lifecycleState());
        assertEquals(IdentityLifecycleState.ACTIVE, active.lifecycleState());
        assertEquals(created.plusSeconds(1), active.updatedAt());
    }
}
