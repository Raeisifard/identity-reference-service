package com.isc.identityreference.api;

import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.EmbeddingMetric;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BiometricReferenceIntegrationResponse(
        UUID identityReferenceId,
        String modelId,
        String modelVersion,
        int dimension,
        EmbeddingMetric metric,
        boolean normalized,
        String sourcePhotoVersion,
        String state,
        Instant createdAt,
        List<Float> vector) {

    public static BiometricReferenceIntegrationResponse from(UUID identityReferenceId, BiometricReference reference) {
        float[] values = reference.vector();
        List<Float> vector = new java.util.ArrayList<>(values.length);
        for (float value : values) {
            vector.add(value);
        }
        return new BiometricReferenceIntegrationResponse(identityReferenceId, reference.modelId(),
                reference.modelVersion(), reference.dimension(), reference.metric(), reference.normalized(),
                reference.sourcePhotoVersion(), reference.state().name(), reference.createdAt(), vector);
    }
}
