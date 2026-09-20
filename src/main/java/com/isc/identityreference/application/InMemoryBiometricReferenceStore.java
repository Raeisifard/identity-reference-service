package com.isc.identityreference.application;

import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.BiometricReferenceState;
import com.isc.identityreference.domain.biometric.EmbeddingMetric;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryBiometricReferenceStore implements BiometricReferenceStore {
    private final ConcurrentMap<String, BiometricReference> values = new ConcurrentHashMap<>();

    @Override
    public Optional<BiometricReference> find(String identityReferenceId, String modelId, String modelVersion,
                                             String sourcePhotoVersion) {
        return Optional.ofNullable(values.get(key(identityReferenceId, modelId, modelVersion, sourcePhotoVersion)));
    }

    @Override
    public Optional<BiometricReference> findActiveCompatible(String identityReferenceId, String modelId,
                                                             String modelVersion, int dimension,
                                                             EmbeddingMetric metric, boolean normalized) {
        return values.entrySet().stream()
                .filter(e -> e.getKey().startsWith(identityReferenceId + "|" + modelId + "|" + modelVersion + "|"))
                .map(java.util.Map.Entry::getValue)
                .filter(r -> r.state() == BiometricReferenceState.ACTIVE)
                .filter(r -> r.compatibleWith(modelId, modelVersion, dimension, metric, normalized))
                .max(Comparator.comparing(BiometricReference::createdAt));
    }

    @Override
    public BiometricReference save(String identityReferenceId, BiometricReference reference) {
        values.put(key(identityReferenceId, reference.modelId(), reference.modelVersion(),
                reference.sourcePhotoVersion()), reference);
        return reference;
    }

    private static String key(String identityReferenceId, String modelId, String modelVersion,
                               String sourcePhotoVersion) {
        return identityReferenceId + "|" + modelId + "|" + modelVersion + "|" + sourcePhotoVersion;
    }
}
