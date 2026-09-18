package com.isc.identityreference.provider.spi;

public record ProviderDescriptor(String providerId, String displayName) {
    public ProviderDescriptor {
        requireNonBlank(providerId, "providerId");
        requireNonBlank(displayName, "displayName");
    }
    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
    }
}
