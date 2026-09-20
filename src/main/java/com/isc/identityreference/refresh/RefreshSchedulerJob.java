package com.isc.identityreference.refresh;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.domain.identity.IdentityReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.RejectedExecutionException;

@Component public class RefreshSchedulerJob {
 private final IdentityReferenceStore store; private final IdentityReferenceRefreshService refresh; private final RefreshProperties properties; private final RefreshPolicyResolver policies; private final TaskExecutor executor;
 public RefreshSchedulerJob(IdentityReferenceStore store,IdentityReferenceRefreshService refresh,RefreshProperties properties,RefreshPolicyResolver policies,@Qualifier("identityRefreshExecutor") TaskExecutor executor){this.store=store;this.refresh=refresh;this.properties=properties;this.policies=policies;this.executor=executor;}
 @Scheduled(fixedDelayString="${identity-reference.refresh.scheduler-delay:10s}")
 public void run(){
  if(!properties.isSchedulerEnabled())return;
  Instant now=Instant.now();
  for(IdentityReference reference:store.findDue(now.plus(properties.effectiveSchedulerLookAhead()),properties.getBatchSize())){
   if(reference.providerRecords().isEmpty())continue;
   String providerId=reference.providerRecords().getFirst().providerId();
   RefreshPolicy policy=policies.resolve(providerId);
   if(!policy.inWindow(now.atZone(properties.getZone()).toLocalTime()))continue;
   try{executor.execute(()->refresh.scheduledRefresh(reference.lookupKey(),providerId,Instant.now()));}
   catch(RejectedExecutionException ignored){}
  }
 }
}
