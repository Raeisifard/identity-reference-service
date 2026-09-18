package com.isc.identityreference.persistence.oracle.repository;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdentityReferenceRepositoryContractTest {
    @Test void exposesLookupAndDueRefreshOperations() throws Exception {
        assertNotNull(IdentityReferenceRepository.class.getMethod("findByLookupKeyHash", String.class));
        assertNotNull(IdentityReferenceRepository.class.getMethod(
                "findByNextRefreshAtLessThanEqualOrderByNextRefreshAtAsc",
                java.time.Instant.class, org.springframework.data.domain.Pageable.class));
    }
}
