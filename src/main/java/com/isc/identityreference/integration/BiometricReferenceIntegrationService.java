package com.isc.identityreference.integration;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.EmbeddingMetric;

import java.util.Optional;
import java.util.UUID;

public final class BiometricReferenceIntegrationService {
    private final BiometricReferenceStore store;

    public BiometricReferenceIntegrationService(BiometricReferenceStore store) {
        this.store = store;
    }

    public Optional<BiometricReference> find(UUID identityReferenceId, String modelId, String modelVersion,
                                              int dimension, EmbeddingMetric metric, boolean normalized) {
        return store.findActiveCompatible(identityReferenceId.toString(), modelId, modelVersion,
                dimension, metric, normalized);
    }
}
