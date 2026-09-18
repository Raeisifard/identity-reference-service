package com.isc.identityreference.provider.spi;

import com.isc.identityreference.domain.identity.IdentityAttributes;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class IdentityProviderContractTest {
    private static final Instant TIME = Instant.parse("2026-09-18T00:00:00Z");

    @Test void mockProviderProducesNormalizedFoundResult() {
        IdentityProvider provider = new IdentityProvider() {
            private final ProviderDescriptor descriptor = new ProviderDescriptor("mock-provider", "Mock Provider");
            public ProviderDescriptor descriptor() { return descriptor; }
            public ProviderLookupResult lookup(IdentityLookupKey key) {
                IdentityAttributes a = new IdentityAttributes("Ada", "Lovelace", "Byron", key.birthDate(), "F", key.nationalId());
                return ProviderLookupResult.found(descriptor.providerId(), "mock-record-001", ProviderAuthority.AUTHORITATIVE, a, TIME);
            }
        };
        ProviderLookupResult result = provider.lookup(new IdentityLookupKey("TEST-NATIONAL-ID", LocalDate.of(1815, 12, 10)));
        assertEquals("mock-provider", result.providerId());
        assertEquals(ProviderLookupStatus.FOUND, result.status());
        assertEquals("mock-record-001", result.providerRecordId());
        assertEquals("Ada", result.attributes().orElseThrow().givenName());
        assertEquals(ProviderAuthority.AUTHORITATIVE, result.authority());
        assertTrue(result.error().isEmpty());
    }

    @Test void notFoundResultDoesNotCarryAttributes() {
        ProviderLookupResult result = ProviderLookupResult.notFound("mock-provider", "not-found", ProviderAuthority.UNKNOWN, TIME);
        assertEquals(ProviderLookupStatus.NOT_FOUND, result.status());
        assertTrue(result.attributes().isEmpty());
        assertTrue(result.error().isEmpty());
    }

    @Test void retryableErrorIsExplicit() {
        ProviderError error = new ProviderError(ProviderErrorCode.RATE_LIMITED, true, "Provider request was rate limited.");
        ProviderLookupResult result = ProviderLookupResult.error("mock-provider", "unavailable", ProviderAuthority.UNKNOWN, error, TIME);
        assertEquals(ProviderLookupStatus.ERROR, result.status());
        assertTrue(result.error().orElseThrow().retryable());
        assertEquals(ProviderErrorCode.RATE_LIMITED, result.error().orElseThrow().code());
        assertNull(result.attributes());
    }
}
