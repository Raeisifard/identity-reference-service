package com.isc.identityreference.biometric;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.InMemoryBiometricReferenceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryBiometricReferenceStoreConfiguration {
    @Bean
    @ConditionalOnExpression("!${identity-reference.persistence.h2.enabled:false} && !${identity-reference.persistence.oracle.enabled:false}")
    BiometricReferenceStore biometricReferenceStore() {
        return new InMemoryBiometricReferenceStore();
    }
}
