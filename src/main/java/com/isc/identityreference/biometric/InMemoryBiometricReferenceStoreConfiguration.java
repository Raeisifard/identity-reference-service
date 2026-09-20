package com.isc.identityreference.biometric;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.InMemoryBiometricReferenceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryBiometricReferenceStoreConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "identity-reference.persistence.oracle", name = "enabled", havingValue = "false", matchIfMissing = true)
    BiometricReferenceStore biometricReferenceStore() {
        return new InMemoryBiometricReferenceStore();
    }
}
