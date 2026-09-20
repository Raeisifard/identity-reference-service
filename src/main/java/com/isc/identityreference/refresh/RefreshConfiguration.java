package com.isc.identityreference.refresh;
import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.application.InMemoryIdentityReferenceStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Configuration @EnableConfigurationProperties(RefreshProperties.class) @org.springframework.scheduling.annotation.EnableScheduling
public class RefreshConfiguration{
 @Bean @ConditionalOnMissingBean(IdentityReferenceStore.class) IdentityReferenceStore inMemoryIdentityReferenceStore(){return new InMemoryIdentityReferenceStore();}
 @Bean RefreshQuotaGuard refreshQuotaGuard(){return new RefreshQuotaGuard();}
 @Bean @ConditionalOnMissingBean(RefreshLeaseManager.class) RefreshLeaseManager inMemoryRefreshLeaseManager(){return new InMemoryRefreshLeaseManager();}
 @Bean @Qualifier("identityRefreshExecutor") TaskExecutor identityRefreshExecutor(RefreshProperties p){
  ThreadPoolTaskExecutor e=new ThreadPoolTaskExecutor();e.setCorePoolSize(p.getRefreshExecutorCorePoolSize());e.setMaxPoolSize(p.getRefreshExecutorMaxPoolSize());e.setQueueCapacity(p.getRefreshExecutorQueueCapacity());e.setThreadNamePrefix(p.getRefreshExecutorThreadNamePrefix());e.setWaitForTasksToCompleteOnShutdown(true);e.setAwaitTerminationSeconds(30);e.initialize();return e;
 }
 @Bean @Qualifier("identityProviderExecutor") AsyncTaskExecutor identityProviderExecutor(RefreshProperties p){
  ThreadPoolTaskExecutor e=new ThreadPoolTaskExecutor();e.setCorePoolSize(p.getProviderExecutorCorePoolSize());e.setMaxPoolSize(p.getProviderExecutorMaxPoolSize());e.setQueueCapacity(0);e.setThreadNamePrefix(p.getProviderExecutorThreadNamePrefix());e.setWaitForTasksToCompleteOnShutdown(false);e.initialize();return e;
 }
 @Bean @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix="identity-reference.cache.redis",name="enabled",havingValue="true") @org.springframework.context.annotation.Primary
 RefreshLeaseManager redisRefreshLeaseManager(org.springframework.data.redis.core.StringRedisTemplate redis,@org.springframework.beans.factory.annotation.Value("\${identity-reference.cache.redis.key-prefix:identity-ref:v1:}") String prefix){return new RedisRefreshLeaseManager(redis,prefix+"refresh-lock:");}
}