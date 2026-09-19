package com.isc.identityreference.application;

import com.isc.identityreference.domain.biometric.BiometricReference;
import java.util.Optional;

public interface BiometricReferenceStore {
    Optional<BiometricReference> find(String identityReferenceId, String modelId, String modelVersion, String sourcePhotoVersion);
    BiometricReference save(String identityReferenceId, BiometricReference reference);
}
