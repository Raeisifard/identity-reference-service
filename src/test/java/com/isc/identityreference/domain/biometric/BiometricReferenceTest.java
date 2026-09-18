package com.isc.identityreference.domain.biometric;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BiometricReferenceTest {

    @Test
    void requiresExactModelCompatibility() {
        float[] vector = new float[512];
        BiometricReference reference = new BiometricReference(
                "arcface-r50", "1", 512, EmbeddingMetric.COSINE, true,
                "photo-v1", BiometricReferenceState.ACTIVE, vector, Instant.parse("2026-01-01T00:00:00Z"));

        assertTrue(reference.compatibleWith("arcface-r50", "1", 512, EmbeddingMetric.COSINE, true));
        assertFalse(reference.compatibleWith("another-model", "1", 512, EmbeddingMetric.COSINE, true));
        assertFalse(reference.compatibleWith("arcface-r50", "1", 256, EmbeddingMetric.COSINE, true));
    }

    @Test
    void defensivelyCopiesVector() {
        float[] vector = new float[]{1, 2};
        BiometricReference reference = new BiometricReference(
                "model", "1", 2, EmbeddingMetric.COSINE, true,
                "photo-v1", BiometricReferenceState.ACTIVE, vector, Instant.now());

        vector[0] = 99;
        assertEquals(1, reference.vector()[0], 0.0001);
    }
}
