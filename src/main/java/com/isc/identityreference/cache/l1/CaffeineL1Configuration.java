package com.isc.identityreference.cache.l1;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "identity-reference.cache.l1", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CaffeineL1Configuration {
    @Bean
    CaffeineL1Cache caffeineL1Cache() {
        return new CaffeineL1Cache(10000);
    }

    @Bean
    Object caffeineL1Metrics(CaffeineL1Cache cache, MeterRegistry registry) {
        Gauge.builder("identity_reference_cache_l1_size", cache, CaffeineL1Cache::estimatedSize).register(registry);
        Gauge.builder("identity_reference_cache_l1_hit_rate", cache,
                c -> c.stats().hitRate()).register(registry);
        return new Object();
    }
}
