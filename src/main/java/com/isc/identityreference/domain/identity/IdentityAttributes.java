package com.isc.identityreference.domain.identity;

import java.time.LocalDate;

public record IdentityAttributes(
        String givenName,
        String familyName,
        String fatherName,
        LocalDate birthDate,
        String gender,
        String nationalId
) {
    public IdentityAttributes {
        requireNonBlank(givenName, "givenName");
        requireNonBlank(familyName, "familyName");
        if (birthDate == null) {
            throw new IllegalArgumentException("birthDate must not be null");
        }
        requireNonBlank(nationalId, "nationalId");
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
