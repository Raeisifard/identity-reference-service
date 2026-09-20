package com.isc.identityreference.integration;

import com.isc.identityreference.application.InMemoryBiometricReferenceStore;
import com.isc.identityreference.application.InMemoryIdentityReferenceStore;
import com.isc.identityreference.domain.biometric.*;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BiometricReferenceIntegrationServiceTest {
    @Test
    void returnsNewestActiveReferenceWhenModelContractMatches() {
        InMemoryIdentityReferenceStore identityStore = new InMemoryIdentityReferenceStore();
        InMemoryBiometricReferenceStore biometricStore = new InMemoryBiometricReferenceStore();
        BiometricReferenceIntegrationService service =
                new BiometricReferenceIntegrationService(identityStore, biometricStore);
        UUID id = UUID.randomUUID();
        saveIdentity(identityStore, id, IdentityLifecycleState.ACTIVE);

        biometricStore.save(id.toString(), reference("photo-1",
                Instant.parse("2026-09-19T00:00:00Z"), BiometricReferenceState.RETIRED));
        biometricStore.save(id.toString(), reference("photo-2",
                Instant.parse("2026-09-20T00:00:00Z"), BiometricReferenceState.ACTIVE));

        var result = service.find(id, "arcface-512", "w600k-r50", 512,
                EmbeddingMetric.COSINE, true);

        assertThat(result).isPresent();
        assertThat(result.get().sourcePhotoVersion()).isEqualTo("photo-2");
    }

    @Test
    void doesNotReturnReferenceFromAnotherEmbeddingSpace() {
        InMemoryIdentityReferenceStore identityStore = new InMemoryIdentityReferenceStore();
        InMemoryBiometricReferenceStore biometricStore = new InMemoryBiometricReferenceStore();
        BiometricReferenceIntegrationService service =
                new BiometricReferenceIntegrationService(identityStore, biometricStore);
        UUID id = UUID.randomUUID();
        saveIdentity(identityStore, id, IdentityLifecycleState.ACTIVE);

        biometricStore.save(id.toString(), reference("photo-1", Instant.now(), BiometricReferenceState.ACTIVE));

        assertThat(service.find(id, "mobilefacenet-512", "v1", 512,
                EmbeddingMetric.COSINE, true)).isEmpty();
    }

    @Test
    void doesNotServeReferenceForRetiredIdentity() {
        InMemoryIdentityReferenceStore identityStore = new InMemoryIdentityReferenceStore();
        InMemoryBiometricReferenceStore biometricStore = new InMemoryBiometricReferenceStore();
        BiometricReferenceIntegrationService service =
                new BiometricReferenceIntegrationService(identityStore, biometricStore);
        UUID id = UUID.randomUUID();
        saveIdentity(identityStore, id, IdentityLifecycleState.RETIRED);

        biometricStore.save(id.toString(), reference("photo-1", Instant.now(), BiometricReferenceState.ACTIVE));

        assertThat(service.find(id, "arcface-512", "w600k-r50", 512,
                EmbeddingMetric.COSINE, true)).isEmpty();
    }

    private static void saveIdentity(InMemoryIdentityReferenceStore store, UUID id, IdentityLifecycleState state) {
        Instant now = Instant.parse("2026-09-20T00:00:00Z");
        IdentityAttributes attributes = new IdentityAttributes(
                "Test", "Person", null, LocalDate.of(1990, 1, 1), "U", "0000000000");
        Freshness freshness = new Freshness(now.minusSeconds(60), now.plusSeconds(3600), now.plusSeconds(7200));
        store.save(new IdentityReference(
                IdentityReferenceId.of(id),
                new IdentityLookupKey("0000000000", attributes.birthDate()),
                attributes, List.of(), List.of(), state, freshness, now, now));
    }

    private static BiometricReference reference(String photo, Instant createdAt, BiometricReferenceState state) {
        float[] vector = new float[512];
        vector[0] = 1.0f;
        return new BiometricReference("arcface-512", "w600k-r50", 512, EmbeddingMetric.COSINE,
                true, photo, state, vector, createdAt);
    }
}
