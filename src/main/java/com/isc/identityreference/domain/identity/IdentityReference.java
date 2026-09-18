package com.isc.identityreference.domain.identity;

import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.freshness.FreshnessState;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycle;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.domain.provider.ProviderRecord;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record IdentityReference(
        IdentityReferenceId id,
        IdentityLookupKey lookupKey,
        IdentityAttributes attributes,
        List<ProviderRecord> providerRecords,
        List<BiometricReference> biometricReferences,
        IdentityLifecycleState lifecycleState,
        Freshness freshness,
        Instant createdAt,
        Instant updatedAt
) {
    public IdentityReference {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(lookupKey, "lookupKey");
        Objects.requireNonNull(attributes, "attributes");
        providerRecords = List.copyOf(Objects.requireNonNull(providerRecords, "providerRecords"));
        biometricReferences = List.copyOf(Objects.requireNonNull(biometricReferences, "biometricReferences"));
        Objects.requireNonNull(lifecycleState, "lifecycleState");
        Objects.requireNonNull(freshness, "freshness");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not precede createdAt");
        }
    }

    public FreshnessState freshnessAt(Instant now) {
        return freshness.stateAt(now);
    }

    public IdentityReference transitionTo(IdentityLifecycleState target, Instant now) {
        Objects.requireNonNull(now, "now");
        IdentityLifecycleState next = IdentityLifecycle.transition(lifecycleState, target);
        return new IdentityReference(id, lookupKey, attributes, providerRecords, biometricReferences,
                next, freshness, createdAt, now);
    }

    public Optional<BiometricReference> activeBiometricFor(
            String modelId, String modelVersion, int dimension,
            com.isc.identityreference.domain.biometric.EmbeddingMetric metric,
            boolean normalized) {
        return biometricReferences.stream()
                .filter(reference -> reference.state() == com.isc.identityreference.domain.biometric.BiometricReferenceState.ACTIVE)
                .filter(reference -> reference.compatibleWith(modelId, modelVersion, dimension, metric, normalized))
                .findFirst();
    }
}
