package com.isc.identityreference.persistence.oracle;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.domain.biometric.*;
import com.isc.identityreference.persistence.oracle.entity.BiometricReferenceEntity;
import com.isc.identityreference.persistence.oracle.repository.BiometricReferenceRepository;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.UUID;

public final class OracleBiometricReferenceStore implements BiometricReferenceStore {
    private final BiometricReferenceRepository repository;

    public OracleBiometricReferenceStore(BiometricReferenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BiometricReference> find(String identityReferenceId, String modelId, String modelVersion,
                                             String sourcePhotoVersion) {
        return repository.findByIdentityReferenceIdAndModelIdAndModelVersionAndSourcePhotoVersion(
                        identityReferenceId, modelId, modelVersion, sourcePhotoVersion)
                .map(OracleBiometricReferenceStore::toDomain);
    }

    @Override
    public Optional<BiometricReference> findActiveCompatible(String identityReferenceId, String modelId,
                                                             String modelVersion, int dimension,
                                                             EmbeddingMetric metric, boolean normalized) {
        return repository.findByIdentityReferenceIdAndModelIdAndModelVersionAndStateOrderByCreatedAtDesc(
                        identityReferenceId, modelId, modelVersion, BiometricReferenceState.ACTIVE.name())
                .stream()
                .map(OracleBiometricReferenceStore::toDomain)
                .filter(r -> r.compatibleWith(modelId, modelVersion, dimension, metric, normalized))
                .findFirst();
    }

    @Override
    public BiometricReference save(String identityReferenceId, BiometricReference reference) {
        BiometricReferenceEntity entity = repository
                .findByIdentityReferenceIdAndModelIdAndModelVersionAndSourcePhotoVersion(
                        identityReferenceId, reference.modelId(), reference.modelVersion(),
                        reference.sourcePhotoVersion())
                .orElseGet(BiometricReferenceEntity::new);

        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        entity.setIdentityReferenceId(identityReferenceId);
        entity.setModelId(reference.modelId());
        entity.setModelVersion(reference.modelVersion());
        entity.setDimension(reference.dimension());
        entity.setMetric(reference.metric().name());
        entity.setNormalized(reference.normalized());
        entity.setSourcePhotoVersion(reference.sourcePhotoVersion());
        entity.setState(reference.state().name());
        entity.setVector(toBytes(reference.vector()));
        entity.setCreatedAt(reference.createdAt());
        repository.save(entity);
        return reference;
    }

    public long deleteByIdentityReferenceId(String identityReferenceId) {
        return repository.deleteByIdentityReferenceId(identityReferenceId);
    }

    private static BiometricReference toDomain(BiometricReferenceEntity e) {
        return new BiometricReference(e.getModelId(), e.getModelVersion(), e.getDimension(),
                EmbeddingMetric.valueOf(e.getMetric()), e.isNormalized(), e.getSourcePhotoVersion(),
                BiometricReferenceState.valueOf(e.getState()), fromBytes(e.getVector(), e.getDimension()),
                e.getCreatedAt());
    }

    private static byte[] toBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Float.BYTES).order(ByteOrder.BIG_ENDIAN);
        for (float value : vector) buffer.putFloat(value);
        return buffer.array();
    }

    private static float[] fromBytes(byte[] bytes, int dimension) {
        if (bytes == null || bytes.length != dimension * Float.BYTES)
            throw new IllegalStateException("stored biometric vector has invalid size");
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        float[] vector = new float[dimension];
        for (int i = 0; i < dimension; i++) vector[i] = buffer.getFloat();
        return vector;
    }
}
