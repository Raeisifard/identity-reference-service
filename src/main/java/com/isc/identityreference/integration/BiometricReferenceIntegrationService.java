package com.isc.identityreference.integration;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;

import java.util.Optional;
import java.util.UUID;

public final class BiometricReferenceIntegrationService {
    private final IdentityReferenceStore identityStore;
    private final BiometricReferenceStore biometricStore;

    public BiometricReferenceIntegrationService(IdentityReferenceStore identityStore,
                                                BiometricReferenceStore biometricStore) {
        this.identityStore = identityStore;
        this.biometricStore = biometricStore;
    }

    public Optional<BiometricReference> find(UUID identityReferenceId, String modelId, String modelVersion,
                                              int dimension, EmbeddingMetric metric, boolean normalized) {
        return identityStore.findById(identityReferenceId)
                .filter(identity -> identity.lifecycleState() != IdentityLifecycleState.RETIRED)
                .flatMap(identity -> biometricStore.findActiveCompatible(identityReferenceId.toString(), modelId,
                        modelVersion, dimension, metric, normalized));
    }
}
