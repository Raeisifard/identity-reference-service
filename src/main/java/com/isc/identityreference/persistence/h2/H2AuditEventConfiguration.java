package com.isc.identityreference.persistence.h2;

import com.isc.identityreference.governance.AuditEventStore;
import com.isc.identityreference.persistence.oracle.OracleAuditEventStore;
import com.isc.identityreference.persistence.oracle.repository.IdentityAuditEventRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.persistence.h2", name = "enabled", havingValue = "true")
public class H2AuditEventConfiguration {
    @Bean
    AuditEventStore h2AuditEventStore(IdentityAuditEventRepository repository) {
        return new OracleAuditEventStore(repository);
    }
}
