package com.isc.identityreference.integration;

import com.isc.identityreference.application.InMemoryBiometricReferenceStore;
import com.isc.identityreference.domain.biometric.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BiometricReferenceIntegrationServiceTest {
    @Test
    void returnsNewestActiveReferenceWhenModelContractMatches() {
        InMemoryBiometricReferenceStore store = new InMemoryBiometricReferenceStore();
        BiometricReferenceIntegrationService service = new BiometricReferenceIntegrationService(store);
        String id = UUID.randomUUID().toString();

        store.save(id, reference("photo-1", Instant.parse("2026-09-19T00:00:00Z"), BiometricReferenceState.RETIRED));
        store.save(id, reference("photo-2", Instant.parse("2026-09-20T00:00:00Z"), BiometricReferenceState.ACTIVE));

        var result = service.find(UUID.fromString(id), "arcface-512", "w600k-r50", 512,
                EmbeddingMetric.COSINE, true);

        assertThat(result).isPresent();
        assertThat(result.get().sourcePhotoVersion()).isEqualTo("photo-2");
    }

    @Test
    void doesNotReturnReferenceFromAnotherEmbeddingSpace() {
        InMemoryBiometricReferenceStore store = new InMemoryBiometricReferenceStore();
        BiometricReferenceIntegrationService service = new BiometricReferenceIntegrationService(store);
        String id = UUID.randomUUID().toString();

        store.save(id, reference("photo-1", Instant.now(), BiometricReferenceState.ACTIVE));

        assertThat(service.find(UUID.fromString(id), "mobilefacenet-512", "v1", 512,
                EmbeddingMetric.COSINE, true)).isEmpty();
    }

    private static BiometricReference reference(String photo, Instant createdAt, BiometricReferenceState state) {
        float[] vector = new float[512];
        vector[0] = 1.0f;
        return new BiometricReference("arcface-512", "w600k-r50", 512, EmbeddingMetric.COSINE,
                true, photo, state, vector, createdAt);
    }
}
