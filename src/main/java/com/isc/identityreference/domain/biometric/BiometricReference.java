package com.isc.identityreference.domain.biometric;

import java.time.Instant;
import java.util.Objects;

public record BiometricReference(
        String modelId,
        String modelVersion,
        int dimension,
        EmbeddingMetric metric,
        boolean normalized,
        String sourcePhotoVersion,
        BiometricReferenceState state,
        float[] vector,
        Instant createdAt
) {
    public BiometricReference {
        requireNonBlank(modelId, "modelId");
        requireNonBlank(modelVersion, "modelVersion");
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension must be positive");
        }
        Objects.requireNonNull(metric, "metric");
        requireNonBlank(sourcePhotoVersion, "sourcePhotoVersion");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(vector, "vector");
        Objects.requireNonNull(createdAt, "createdAt");
        if (vector.length != dimension) {
            throw new IllegalArgumentException("vector length must equal dimension");
        }
        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("vector must contain only finite values");
            }
        }
        vector = vector.clone();
    }

    @Override
    public float[] vector() {
        return vector.clone();
    }

    public boolean compatibleWith(String requiredModelId, String requiredModelVersion,
                                  int requiredDimension, EmbeddingMetric requiredMetric,
                                  boolean requiredNormalized) {
        return modelId.equals(requiredModelId)
                && modelVersion.equals(requiredModelVersion)
                && dimension == requiredDimension
                && metric == requiredMetric
                && normalized == requiredNormalized;
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
