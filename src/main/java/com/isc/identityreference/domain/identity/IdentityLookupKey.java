package com.isc.identityreference.domain.identity;

import java.time.LocalDate;
import java.util.Objects;

/** Protected lookup material. Do not expose or log this value. */
public record IdentityLookupKey(String nationalId, LocalDate birthDate) {

    public IdentityLookupKey {
        requireNonBlank(nationalId, "nationalId");
        Objects.requireNonNull(birthDate, "birthDate");
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
