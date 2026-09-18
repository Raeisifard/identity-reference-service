package com.isc.identityreference.application;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class LookupKeyFingerprint {
    private LookupKeyFingerprint() {}

    static String of(IdentityLookupKey key) {
        try {
            String canonical = key.nationalId() + "|" + key.birthDate();
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(digest.length * 2);
            for (byte value : digest) result.append(String.format("%02x", value));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
