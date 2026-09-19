package com.isc.identityreference.provider.spi;

import com.isc.identityreference.domain.identity.IdentityAttributes;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IdentityProviderContractTest {

    private static final Instant TIME =
            Instant.parse("2026-09-18T00:00:00Z");

    @Test
    void mockProviderProducesNormalizedFoundResult() {
        IdentityProvider provider = new IdentityProvider() {
            private final ProviderDescriptor descriptor =
                    new ProviderDescriptor("mock-provider", "Mock Provider");

            public ProviderDescriptor descriptor() {
                return descriptor;
            }

            public ProviderLookupResult lookup(IdentityLookupKey key) {
                IdentityAttributes attributes =
                        new IdentityAttributes(
                                "Ada",
                                "Lovelace",
                                "Byron",
                                key.birthDate(),
                                "F",
                                key.nationalId());

                return ProviderLookupResult.found(
                        descriptor.providerId(),
                        "mock-record-001",
                        ProviderAuthority.AUTHORITATIVE,
                        attributes,
                        TIME);
            }
        };

        ProviderLookupResult result =
                provider.lookup(
                        new IdentityLookupKey(
                                "TEST-NATIONAL-ID",
                                LocalDate.of(1815, 12, 10)));

        assertEquals("mock-provider", result.providerId());
        assertEquals(ProviderLookupStatus.FOUND, result.status());
        assertEquals("mock-record-001", result.providerRecordId());
        assertNotNull(result.attributes());
        assertEquals("Ada", result.attributes().givenName());
        assertEquals(
                ProviderAuthority.AUTHORITATIVE,
                result.authority());
        assertNull(result.error());
    }

    @Test
    void notFoundResultDoesNotCarryAttributes() {
        ProviderLookupResult result =
                ProviderLookupResult.notFound(
                        "mock-provider",
                        "not-found",
                        ProviderAuthority.UNKNOWN,
                        TIME);

        assertEquals(ProviderLookupStatus.NOT_FOUND, result.status());
        assertNull(result.attributes());
        assertNull(result.error());
    }

    @Test
    void retryableErrorIsExplicit() {
        ProviderError error =
                new ProviderError(
                        ProviderErrorCode.RATE_LIMITED,
                        true,
                        "Provider request was rate limited.");

        ProviderLookupResult result =
                ProviderLookupResult.error(
                        "mock-provider",
                        "unavailable",
                        ProviderAuthority.UNKNOWN,
                        error,
                        TIME);

        assertEquals(ProviderLookupStatus.ERROR, result.status());
        assertNotNull(result.error());
        assertTrue(result.error().retryable());
        assertEquals(
                ProviderErrorCode.RATE_LIMITED,
                result.error().code());
        assertNull(result.attributes());
    }
}