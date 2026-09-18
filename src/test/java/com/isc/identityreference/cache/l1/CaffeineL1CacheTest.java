package com.isc.identityreference.cache.l1;

import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.IdentityAttributes;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.identity.IdentityReference;
import com.isc.identityreference.domain.identity.IdentityReferenceId;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CaffeineL1CacheTest {
    private static final Instant NOW = Instant.parse("2026-09-18T00:00:00Z");

    @Test void cachesAndInvalidatesReference() {
        var cache = new CaffeineL1Cache(2);
        var ref = reference();
        cache.put("hash", ref);
        assertSame(ref, cache.get("hash").orElseThrow());
        cache.evict("hash");
        assertTrue(cache.get("hash").isEmpty());
    }

    @Test void rejectsBlankKey() {
        var cache = new CaffeineL1Cache(2);
        assertThrows(IllegalArgumentException.class, () -> cache.get(" "));
    }

    private IdentityReference reference() {
        var attrs = new IdentityAttributes("Ada", "Lovelace", null, LocalDate.of(1815,12,10), "F", "NATIONAL-ID");
        var freshness = new Freshness(NOW, NOW.plusSeconds(60), NOW.plusSeconds(120));
        return new IdentityReference(IdentityReferenceId.newId(),
                new IdentityLookupKey("NATIONAL-ID", LocalDate.of(1815,12,10)),
                attrs, List.of(), List.of(), IdentityLifecycleState.ACTIVE, freshness, NOW, NOW);
    }
}
