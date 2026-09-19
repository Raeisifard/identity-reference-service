package com.isc.identityreference.application;

import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.cache.redis.RedisL2Cache;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.domain.provider.*;
import com.isc.identityreference.policy.ProviderPolicy;
import com.isc.identityreference.policy.ProviderPolicyEngine;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.provider.spi.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IdentityAcquisitionService {
    private final IdentityReferenceStore store;
    private final IdentityProviderRegistry providers;
    private final ProviderPolicy policy;
    private final ProviderPolicyEngine policyEngine;
    private final CaffeineL1Cache l1;
    private final RedisL2Cache l2;
    private final ConcurrentMap<String, IdentityAcquisitionResult> idempotency = new ConcurrentHashMap<>();

    public IdentityAcquisitionService(IdentityReferenceStore store, IdentityProvider provider,
                                      ProviderPolicy policy, ProviderPolicyEngine policyEngine,
                                      CaffeineL1Cache l1, RedisL2Cache l2) {
        this(store, IdentityProviderRegistry.single(provider), policy, policyEngine, l1, l2);
    }

    public IdentityAcquisitionService(IdentityReferenceStore store, IdentityProviderRegistry providers,
                                      ProviderPolicy policy, ProviderPolicyEngine policyEngine,
                                      CaffeineL1Cache l1, RedisL2Cache l2) {
        this.store = Objects.requireNonNull(store, "store");
        this.providers = Objects.requireNonNull(providers, "providers");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.policyEngine = Objects.requireNonNull(policyEngine, "policyEngine");
        this.l1 = Objects.requireNonNull(l1, "l1");
        this.l2 = Objects.requireNonNull(l2, "l2");
    }

    public IdentityAcquisitionResult acquire(IdentityAcquisitionRequest request, Instant now) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(now, "now");

        Optional<IdentityProvider> selectedProvider = providers.find(request.providerId());
        if (!policy.enabled() || !policy.providerId().equals(request.providerId()) || selectedProvider.isEmpty()
                || !selectedProvider.get().descriptor().providerId().equals(request.providerId())) {
            return new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.FAILED, null);
        }
        IdentityProvider provider = selectedProvider.get();

        IdentityAcquisitionResult replay = idempotency.get(request.idempotencyKey());
        if (replay != null) {
            return new IdentityAcquisitionResult(
                    IdentityAcquisitionResult.Status.IDEMPOTENT_REPLAY, replay.reference());
        }

        String cacheKey = LookupKeyFingerprint.of(request.lookupKey());
        Optional<IdentityReference> cached = l1.get(cacheKey);
        if (cached.isEmpty()) cached = l2.get(cacheKey);
        if (cached.isPresent()) {
            IdentityAcquisitionResult result =
                    new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.ACQUIRED, cached.get());
            idempotency.putIfAbsent(request.idempotencyKey(), result);
            return result;
        }

        ProviderLookupResult result = provider.lookup(request.lookupKey());
        if (result.status() == ProviderLookupStatus.NOT_FOUND) {
            IdentityAcquisitionResult notFound =
                    new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.NOT_FOUND, null);
            idempotency.putIfAbsent(request.idempotencyKey(), notFound);
            return notFound;
        }
        if (result.status() != ProviderLookupStatus.FOUND) {
            IdentityAcquisitionResult failed =
                    new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.FAILED, null);
            idempotency.putIfAbsent(request.idempotencyKey(), failed);
            return failed;
        }

        IdentityReference existing = store.find(request.lookupKey()).orElse(null);
        if (existing != null && !policyEngine.canOverwrite(policy, existing.providerRecords().isEmpty()
                ? ProviderAuthority.UNKNOWN : existing.providerRecords().getFirst().authority(), result.authority())) {
            IdentityAcquisitionResult current =
                    new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.ACQUIRED, existing);
            idempotency.putIfAbsent(request.idempotencyKey(), current);
            return current;
        }

        Freshness freshness = policyEngine.freshnessFrom(policy, result.retrievedAt());
        ProviderRecord record = new ProviderRecord(
                result.providerId(), result.providerRecordId(), result.authority(),
                ProviderRecordState.CURRENT, result.attributes(), freshness, result.retrievedAt());

        IdentityReference reference = new IdentityReference(
                existing == null ? IdentityReferenceId.newId() : existing.id(),
                request.lookupKey(), result.attributes(), List.of(record), List.of(),
                IdentityLifecycleState.ACTIVE, freshness,
                existing == null ? now : existing.createdAt(), now);

        IdentityReference saved = store.save(reference);
        l1.put(cacheKey, saved);
        l2.put(cacheKey, saved);

        IdentityAcquisitionResult acquired =
                new IdentityAcquisitionResult(IdentityAcquisitionResult.Status.ACQUIRED, saved);
        idempotency.putIfAbsent(request.idempotencyKey(), acquired);
        return acquired;
    }
}
