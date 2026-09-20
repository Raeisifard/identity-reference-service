package com.isc.identityreference.refresh;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.application.InMemoryIdentityReferenceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration @EnableConfigurationProperties(RefreshProperties.class) @org.springframework.scheduling.annotation.EnableScheduling
public class RefreshConfiguration {
 @Bean @ConditionalOnMissingBean(IdentityReferenceStore.class) IdentityReferenceStore inMemoryIdentityReferenceStore(){return new InMemoryIdentityReferenceStore();}
 @Bean RefreshQuotaGuard refreshQuotaGuard(){return new RefreshQuotaGuard();}
 @Bean @ConditionalOnMissingBean(RefreshLeaseManager.class) RefreshLeaseManager inMemoryRefreshLeaseManager(){return new InMemoryRefreshLeaseManager();}
 @Bean @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix="identity-reference.cache.redis",name="enabled",havingValue="true") @org.springframework.context.annotation.Primary
 RefreshLeaseManager redisRefreshLeaseManager(org.springframework.data.redis.core.StringRedisTemplate redis,@org.springframework.beans.factory.annotation.Value("${identity-reference.cache.redis.key-prefix:identity-ref:v1:}") String prefix){return new RedisRefreshLeaseManager(redis,prefix+"refresh-lock:");}
}