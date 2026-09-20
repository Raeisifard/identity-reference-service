package com.isc.identityreference.integration;

import com.isc.identityreference.application.BiometricReferenceStore;
import com.isc.identityreference.application.IdentityReferenceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiometricReferenceIntegrationConfiguration {
    @Bean
    BiometricReferenceIntegrationService biometricReferenceIntegrationService(IdentityReferenceStore identityStore,
                                                                                BiometricReferenceStore biometricStore) {
        return new BiometricReferenceIntegrationService(identityStore, biometricStore);
    }
}
