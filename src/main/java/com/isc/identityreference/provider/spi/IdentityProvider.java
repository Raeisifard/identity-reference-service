package com.isc.identityreference.provider.spi;

import com.isc.identityreference.domain.identity.IdentityLookupKey;

/** Provider-facing SPI. Implementations normalize provider-specific protocols. */
public interface IdentityProvider {
    ProviderDescriptor descriptor();
    ProviderLookupResult lookup(IdentityLookupKey lookupKey);
}
