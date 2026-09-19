package com.isc.identityreference.biometric;

import java.util.Objects;

public record EmbeddingRequest(String identityReferenceId, String sourcePhotoVersion, byte[] photoBytes) {
    public EmbeddingRequest {
        requireNonBlank(identityReferenceId, "identityReferenceId");
        requireNonBlank(sourcePhotoVersion, "sourcePhotoVersion");
        Objects.requireNonNull(photoBytes, "photoBytes");
        if (photoBytes.length == 0) throw new IllegalArgumentException("photoBytes must not be empty");
        photoBytes = photoBytes.clone();
    }
    @Override public byte[] photoBytes() { return photoBytes.clone(); }
    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
    }
}
