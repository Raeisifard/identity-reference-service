package com.isc.identityreference.persistence.oracle.repository;

import com.isc.identityreference.persistence.oracle.entity.IdentityReferenceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IdentityReferenceRepository extends JpaRepository<IdentityReferenceEntity, String> {
    Optional<IdentityReferenceEntity> findByLookupKeyHash(String lookupKeyHash);
    List<IdentityReferenceEntity> findByNextRefreshAtLessThanEqualOrderByNextRefreshAtAsc(Instant now, Pageable pageable);
}
