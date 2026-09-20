package com.isc.identityreference.biometric;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.InMemoryBiometricReferenceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryBiometricReferenceStoreConfiguration {
    @Bean
    @ConditionalOnMissingBean(BiometricReferenceStore.class)
    BiometricReferenceStore biometricReferenceStore() {
        return new InMemoryBiometricReferenceStore();
    }
}
