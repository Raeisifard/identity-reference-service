package com.isc.identityreference.application;

import com.isc.identityreference.domain.identity.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityReferenceStore {
    Optional<IdentityReference> find(IdentityLookupKey lookupKey);
    default Optional<IdentityReference> findById(UUID identityReferenceId) { return Optional.empty(); }
    IdentityReference save(IdentityReference reference);
    default IdentityReference save(IdentityReference reference, Instant nextRefreshAt, long policyVersion) { return save(reference); }
    default List<IdentityReference> findDue(Instant now, int limit) { return List.of(); }
    default int currentRefreshRetryCount(IdentityLookupKey lookupKey) { return 0; }
    default void recordRefreshFailure(IdentityLookupKey lookupKey, String providerId, String status, String safeError,
                                      int retryCount, Instant nextRefreshAt, Instant updatedAt) {}
    default List<IdentityReference> findRetiredBefore(Instant cutoff, int limit) { return List.of(); }
    default boolean delete(IdentityLookupKey lookupKey) { return false; }
    default Long currentPolicyVersion(IdentityLookupKey lookupKey) { return null; }
}
