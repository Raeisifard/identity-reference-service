package com.isc.identityreference.provider.mock;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.provider.spi.ProviderErrorCode;
import com.isc.identityreference.provider.spi.ProviderLookupStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MockNationalAgencyIdentityProviderTest {
    private final MockNationalAgencyIdentityProvider provider =
            new MockNationalAgencyIdentityProvider("mock-national-agency", "Mock National Agency", true);

    @Test
    void returnsFoundForSyntheticFixture() {
        var result = provider.lookup(new IdentityLookupKey("FIXTURE-FOUND-001", LocalDate.of(1990, 1, 1)));

        assertEquals(ProviderLookupStatus.FOUND, result.status());
        assertEquals("FIXTURE-RECORD-001", result.providerRecordId());
        assertEquals("Synthetic", result.attributes().givenName());
        assertEquals("mock-national-agency", result.providerId());
    }

    @Test
    void returnsNotFoundForUnknownFixture() {
        var result = provider.lookup(new IdentityLookupKey("FIXTURE-NOT-FOUND", LocalDate.of(1990, 1, 1)));

        assertEquals(ProviderLookupStatus.NOT_FOUND, result.status());
        assertNull(result.attributes());
    }

    @Test
    void returnsRetryableErrorForSyntheticOutage() {
        var result = provider.lookup(new IdentityLookupKey("FIXTURE-ERROR", LocalDate.of(1990, 1, 1)));

        assertEquals(ProviderLookupStatus.ERROR, result.status());
        assertTrue(result.error().retryable());
        assertEquals(ProviderErrorCode.UNAVAILABLE, result.error().code());
    }

    @Test
    void errorMessageDoesNotEchoLookupMaterial() {
        var disabled = new MockNationalAgencyIdentityProvider("mock-national-agency", "Mock National Agency", false);
        var result = disabled.lookup(new IdentityLookupKey("FIXTURE-ERROR", LocalDate.of(1990, 1, 1)));

        assertEquals(ProviderLookupStatus.ERROR, result.status());
        assertFalse(result.error().safeMessage().contains("FIXTURE-ERROR"));
    }
}
