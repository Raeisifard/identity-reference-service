package com.isc.identityreference.persistence.oracle.repository;

import com.isc.identityreference.persistence.oracle.entity.BiometricReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BiometricReferenceRepository extends JpaRepository<BiometricReferenceEntity, String> {
    Optional<BiometricReferenceEntity> findByIdentityReferenceIdAndModelIdAndModelVersionAndSourcePhotoVersion(
            String identityReferenceId, String modelId, String modelVersion, String sourcePhotoVersion);

    List<BiometricReferenceEntity> findByIdentityReferenceIdAndModelIdAndModelVersionAndStateOrderByCreatedAtDesc(
            String identityReferenceId, String modelId, String modelVersion, String state);
}
