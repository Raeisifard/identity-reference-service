package com.isc.identityreference.persistence.oracle.repository;
import com.isc.identityreference.persistence.oracle.entity.IdentityAuditEventEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.time.Instant; import java.util.List;
public interface IdentityAuditEventRepository extends JpaRepository<IdentityAuditEventEntity,String>{List<IdentityAuditEventEntity> findByOccurredAtBeforeOrderByOccurredAtAsc(Instant cutoff,org.springframework.data.domain.Pageable pageable); void deleteAllByIdInBatch(Iterable<String> ids);}
