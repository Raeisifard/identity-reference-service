package com.isc.identityreference.integration;

import com.isc.identityreference.application.BiometricReferenceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiometricReferenceIntegrationConfiguration {
    @Bean
    BiometricReferenceIntegrationService biometricReferenceIntegrationService(BiometricReferenceStore store) {
        return new BiometricReferenceIntegrationService(store);
    }
}
