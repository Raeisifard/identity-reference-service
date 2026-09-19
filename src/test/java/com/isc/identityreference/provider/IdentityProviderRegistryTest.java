package com.isc.identityreference.provider;

import com.isc.identityreference.provider.mock.MockNationalAgencyIdentityProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentityProviderRegistryTest {
    @Test
    void indexesProvidersByStableId() {
        var provider = new MockNationalAgencyIdentityProvider("mock-national-agency", "Mock National Agency", true);
        var registry = new IdentityProviderRegistry(List.of(provider));

        assertSame(provider, registry.find("mock-national-agency").orElseThrow());
        assertEquals(1, registry.all().size());
    }

    @Test
    void rejectsDuplicateProviderIds() {
        var first = new MockNationalAgencyIdentityProvider("duplicate", "First", true);
        var second = new MockNationalAgencyIdentityProvider("duplicate", "Second", true);

        assertThrows(IllegalArgumentException.class, () -> new IdentityProviderRegistry(List.of(first, second)));
    }
}
