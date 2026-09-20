package com.isc.identityreference.persistence.oracle;

import com.isc.identityreference.application.*;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.domain.provider.*;
import com.isc.identityreference.persistence.oracle.entity.IdentityReferenceEntity;
import com.isc.identityreference.persistence.oracle.repository.BiometricReferenceRepository;
import com.isc.identityreference.persistence.oracle.repository.IdentityReferenceRepository;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class OracleIdentityReferenceStore implements IdentityReferenceStore {
    private final IdentityReferenceRepository repository;
    private final BiometricReferenceRepository biometricRepository;

    public OracleIdentityReferenceStore(IdentityReferenceRepository repository) {
        this(repository, null);
    }

    public OracleIdentityReferenceStore(IdentityReferenceRepository repository,
                                       BiometricReferenceRepository biometricRepository) {
        this.repository = repository;
        this.biometricRepository = biometricRepository;
    }

    @Override public Optional<IdentityReference> find(IdentityLookupKey k) {
        return repository.findByLookupKeyHash(LookupKeyFingerprint.of(k)).map(OracleIdentityReferenceStore::toDomain);
    }

    @Override public Optional<IdentityReference> findById(UUID identityReferenceId) {
        return repository.findById(identityReferenceId.toString()).map(OracleIdentityReferenceStore::toDomain);
    }

    @Override public List<IdentityReference> findDue(Instant n, int limit) {
        return repository.findByNextRefreshAtLessThanEqualOrderByNextRefreshAtAsc(n,
                org.springframework.data.domain.PageRequest.of(0, limit)).stream()
                .map(OracleIdentityReferenceStore::toDomain).toList();
    }

    @Override public IdentityReference save(IdentityReference r) {
        return save(r, r.freshness().freshUntil().minus(Duration.ofHours(6)), 1L);
    }

    @Override public IdentityReference save(IdentityReference r, Instant next, long version) {
        var e = repository.findByLookupKeyHash(LookupKeyFingerprint.of(r.lookupKey()))
                .orElseGet(IdentityReferenceEntity::newEntity);
        e.setId(r.id().value().toString());
        e.setLookupKeyHash(LookupKeyFingerprint.of(r.lookupKey()));
        e.setGivenName(r.attributes().givenName());
        e.setFamilyName(r.attributes().familyName());
        e.setFatherName(r.attributes().fatherName());
        e.setBirthDate(r.attributes().birthDate());
        e.setGender(r.attributes().gender());
        e.setNationalIdCiphertext(r.attributes().nationalId().getBytes(StandardCharsets.UTF_8));
        e.setLifecycleState(r.lifecycleState().name());
        e.setAcquiredAt(r.freshness().acquiredAt());
        e.setFreshUntil(r.freshness().freshUntil());
        e.setStaleUntil(r.freshness().staleUntil());
        e.setNextRefreshAt(next);
        if (!r.providerRecords().isEmpty()) {
            var p = r.providerRecords().getFirst();
            e.setProviderId(p.providerId());
            e.setProviderRecordId(p.providerRecordId());
            e.setProviderAuthority(p.authority().name());
        }
        e.setPolicyVersion(version);
        e.setRefreshStatus("SUCCESS");
        e.setRefreshError(null);
        e.setRefreshRetryCount(0);
        e.setCreatedAt(r.createdAt());
        e.setUpdatedAt(r.updatedAt());
        repository.save(e);
        return r;
    }

    @Override public int currentRefreshRetryCount(IdentityLookupKey k) {
        return repository.findByLookupKeyHash(LookupKeyFingerprint.of(k))
                .map(e -> e.getRefreshRetryCount() == null ? 0 : e.getRefreshRetryCount()).orElse(0);
    }

    @Override public void recordRefreshFailure(IdentityLookupKey k, String providerId, String status, String error,
                                                int count, Instant next, Instant updated) {
        repository.findByLookupKeyHash(LookupKeyFingerprint.of(k)).ifPresent(e -> {
            e.setProviderId(providerId);
            e.setRefreshStatus(status);
            e.setRefreshError(error == null ? null : error.substring(0, Math.min(256, error.length())));
            e.setRefreshRetryCount(count);
            e.setNextRefreshAt(next);
            e.setUpdatedAt(updated);
            repository.save(e);
        });
    }

    @Override public List<IdentityReference> findRetiredBefore(Instant cutoff, int limit) {
        return repository.findByLifecycleStateAndUpdatedAtBeforeOrderByUpdatedAtAsc(
                        IdentityLifecycleState.RETIRED.name(), cutoff,
                        org.springframework.data.domain.PageRequest.of(0, limit))
                .stream().map(OracleIdentityReferenceStore::toDomain).toList();
    }

    @Override public boolean delete(IdentityLookupKey k) {
        return repository.findByLookupKeyHash(LookupKeyFingerprint.of(k)).map(e -> {
            if (biometricRepository != null) biometricRepository.deleteByIdentityReferenceId(e.getId());
            repository.delete(e);
            return true;
        }).orElse(false);
    }

    @Override public Long currentPolicyVersion(IdentityLookupKey k) {
        return repository.findByLookupKeyHash(LookupKeyFingerprint.of(k))
                .map(IdentityReferenceEntity::getPolicyVersion).orElse(null);
    }

    private static IdentityReference toDomain(IdentityReferenceEntity e) {
        String nid = e.getNationalIdCiphertext() == null ? "REDACTED"
                : new String(e.getNationalIdCiphertext(), StandardCharsets.UTF_8);
        IdentityAttributes a = new IdentityAttributes(e.getGivenName(), e.getFamilyName(), e.getFatherName(),
                e.getBirthDate(), e.getGender(), nid);
        Freshness f = new Freshness(e.getAcquiredAt(), e.getFreshUntil(), e.getStaleUntil());
        List<ProviderRecord> records = e.getProviderId() == null ? List.of()
                : List.of(new ProviderRecord(e.getProviderId(), e.getProviderRecordId(),
                ProviderAuthority.valueOf(e.getProviderAuthority()), ProviderRecordState.CURRENT, a, f,
                e.getAcquiredAt()));
        return new IdentityReference(IdentityReferenceId.of(UUID.fromString(e.getId())),
                new IdentityLookupKey(nid, e.getBirthDate()), a, records, List.of(),
                IdentityLifecycleState.valueOf(e.getLifecycleState()), f, e.getCreatedAt(), e.getUpdatedAt());
    }
}
