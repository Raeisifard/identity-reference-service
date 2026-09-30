package com.isc.identityreference.persistence.h2;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.persistence.oracle.OracleBiometricReferenceStore;
import com.isc.identityreference.persistence.oracle.OracleIdentityReferenceStore;
import com.isc.identityreference.persistence.oracle.repository.BiometricReferenceRepository;
import com.isc.identityreference.persistence.oracle.repository.IdentityReferenceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.persistence.h2", name = "enabled", havingValue = "true")
public class H2PersistenceConfiguration {
    @Bean
    IdentityReferenceStore h2IdentityReferenceStore(
            IdentityReferenceRepository repository,
            BiometricReferenceRepository biometricRepository) {
        return new OracleIdentityReferenceStore(repository, biometricRepository);
    }

    @Bean
    BiometricReferenceStore h2BiometricReferenceStore(BiometricReferenceRepository repository) {
        return new OracleBiometricReferenceStore(repository);
    }
}
