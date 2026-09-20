package com.isc.identityreference.persistence.oracle;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.persistence.oracle.repository.BiometricReferenceRepository;
import com.isc.identityreference.persistence.oracle.repository.IdentityReferenceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.persistence.oracle", name = "enabled", havingValue = "true")
public class OracleIdentityReferenceConfiguration {

    @Bean
    IdentityReferenceStore identityReferenceStore(IdentityReferenceRepository repository) {
        return new OracleIdentityReferenceStore(repository);
    }

    @Bean
    BiometricReferenceStore biometricReferenceStore(BiometricReferenceRepository repository) {
        return new OracleBiometricReferenceStore(repository);
    }
}
