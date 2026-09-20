package com.isc.identityreference.application;

import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.EmbeddingMetric;

import java.util.Optional;

public interface BiometricReferenceStore {
    Optional<BiometricReference> find(String identityReferenceId, String modelId, String modelVersion, String sourcePhotoVersion);

    Optional<BiometricReference> findActiveCompatible(String identityReferenceId,
                                                       String modelId,
                                                       String modelVersion,
                                                       int dimension,
                                                       EmbeddingMetric metric,
                                                       boolean normalized);

    BiometricReference save(String identityReferenceId, BiometricReference reference);
}
