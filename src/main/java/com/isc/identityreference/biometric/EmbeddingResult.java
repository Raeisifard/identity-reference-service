package com.isc.identityreference.biometric;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import java.util.Objects;

public record EmbeddingResult(String modelId, String modelVersion, int dimension, EmbeddingMetric metric, boolean normalized, float[] vector) {
    public EmbeddingResult {
        requireNonBlank(modelId, "modelId");
        requireNonBlank(modelVersion, "modelVersion");
        if (dimension <= 0) throw new IllegalArgumentException("dimension must be positive");
        Objects.requireNonNull(metric, "metric"); Objects.requireNonNull(vector, "vector");
        if (vector.length != dimension) throw new IllegalArgumentException("vector length must equal dimension");
        for (float value : vector) if (!Float.isFinite(value)) throw new IllegalArgumentException("vector must contain only finite values");
        vector = vector.clone();
    }
    @Override public float[] vector() { return vector.clone(); }
    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
    }
}
