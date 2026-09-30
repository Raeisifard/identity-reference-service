package com.isc.identityreference.admin;
import com.isc.identityreference.application.*;import com.isc.identityreference.biometric.*;import com.isc.identityreference.domain.biometric.EmbeddingMetric;import org.springframework.boot.autoconfigure.condition.*;import org.springframework.context.annotation.*;
@Configuration(proxyBeanMethods=false) @ConditionalOnProperty(prefix="identity-reference.admin-console.test-data",name="biometric-enabled",havingValue="true")
class TestIdentityBiometricConfiguration{
 @Bean @ConditionalOnMissingBean(EmbeddingService.class) EmbeddingService testEmbeddingService(AdminConsoleProperties p){var x=p.getTestData();return new MockEmbeddingService(x.getBiometricModelId(),x.getBiometricModelVersion(),x.getBiometricDimension(),EmbeddingMetric.COSINE,true);}
 @Bean BiometricIngestionService testBiometricIngestionService(EmbeddingService e,BiometricReferenceStore s){return new BiometricIngestionService(e,s);}
}
