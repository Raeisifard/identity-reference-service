package com.isc.identityreference.persistence.oracle;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import com.isc.identityreference.biometric.MockEmbeddingService;
import com.isc.identityreference.application.BiometricIngestionService;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.persistence.oracle.entity.BiometricReferenceEntity;
import com.isc.identityreference.persistence.oracle.entity.IdentityReferenceEntity;
import com.isc.identityreference.persistence.oracle.repository.BiometricReferenceRepository;
import com.isc.identityreference.persistence.oracle.repository.IdentityReferenceRepository;
import com.isc.identityreference.policy.ProviderPolicy;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("oracle")
@EnabledIf(
        expression = "#{systemProperties['identity.oracle.smoke'] == 'true'}",
        loadContext = true
)
@SpringBootTest
@ActiveProfiles("oracle")
class OracleBiometricIngestionSmokeTest {

    private static final String MODEL_ID = "mock-arcface";
    private static final String MODEL_VERSION = "1";
    private static final int DIMENSION = 32;
    private static final String PHOTO_VERSION = "photo-v1";

    @Autowired
    private IdentityReferenceRepository identityReferenceRepository;

    @Autowired
    private BiometricReferenceRepository biometricReferenceRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void persistsAndReadsModelAwareEmbeddingThroughOracle() {
        String identityReferenceId = UUID.randomUUID().toString();
        String lookupKeyHash = "oracle-smoke-" + UUID.randomUUID();

        IdentityReferenceEntity identity = new IdentityReferenceEntity();
        identity.setId(identityReferenceId);
        identity.setLookupKeyHash(lookupKeyHash);
        identity.setGivenName("Oracle");
        identity.setFamilyName("Smoke");
        identity.setFatherName("Test");
        identity.setBirthDate(LocalDate.of(1990, 1, 1));
        identity.setGender("TEST");
        identity.setLifecycleState(IdentityLifecycleState.ACTIVE.name());

        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        identity.setCreatedAt(now);
        identity.setUpdatedAt(now);
        identityReferenceRepository.saveAndFlush(identity);

        var store = new OracleBiometricReferenceStore(biometricReferenceRepository);
        var embeddingService = new MockEmbeddingService(
                MODEL_ID, MODEL_VERSION, DIMENSION, EmbeddingMetric.COSINE, true);
        var ingestion = new BiometricIngestionService(embeddingService, store);
        var policy = new ProviderPolicy.EmbeddingPolicy(
                true, MODEL_ID, MODEL_VERSION, DIMENSION, EmbeddingMetric.COSINE, true);

        byte[] photo = new byte[]{11, 22, 33, 44, 55};

        assertEquals(
                BiometricIngestionService.Result.STORED,
                ingestion.ingest(identityReferenceId, photo, PHOTO_VERSION, policy, now)
        );

        biometricReferenceRepository.flush();
        entityManager.clear();

        BiometricReferenceEntity persisted = biometricReferenceRepository
                .findByIdentityReferenceIdAndModelIdAndModelVersionAndSourcePhotoVersion(
                        identityReferenceId, MODEL_ID, MODEL_VERSION, PHOTO_VERSION)
                .orElseThrow();

        assertEquals(identityReferenceId, persisted.getIdentityReferenceId());
        assertEquals(MODEL_ID, persisted.getModelId());
        assertEquals(MODEL_VERSION, persisted.getModelVersion());
        assertEquals(DIMENSION, persisted.getDimension());
        assertEquals(EmbeddingMetric.COSINE.name(), persisted.getMetric());
        assertTrue(persisted.isNormalized());
        assertEquals(PHOTO_VERSION, persisted.getSourcePhotoVersion());
        assertEquals(DIMENSION * Float.BYTES, persisted.getVector().length);

        var roundTripped = store.find(
                identityReferenceId, MODEL_ID, MODEL_VERSION, PHOTO_VERSION).orElseThrow();

        assertEquals(MODEL_ID, roundTripped.modelId());
        assertEquals(MODEL_VERSION, roundTripped.modelVersion());
        assertEquals(DIMENSION, roundTripped.dimension());
        assertEquals(EmbeddingMetric.COSINE, roundTripped.metric());
        assertTrue(roundTripped.normalized());
        assertEquals(PHOTO_VERSION, roundTripped.sourcePhotoVersion());
        assertEquals(DIMENSION, roundTripped.vector().length);

        double norm = 0.0;
        for (float value : roundTripped.vector()) {
            norm += (double) value * value;
        }
        assertEquals(1.0, Math.sqrt(norm), 0.00001);

        assertEquals(
                1,
                biometricReferenceRepository
                        .findAll()
                        .stream()
                        .filter(e -> identityReferenceId.equals(e.getIdentityReferenceId()))
                        .count()
        );
    }
}
