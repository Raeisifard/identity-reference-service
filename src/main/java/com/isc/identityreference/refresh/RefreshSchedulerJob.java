package com.isc.identityreference.refresh;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.domain.identity.IdentityReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component public class RefreshSchedulerJob {
 private final IdentityReferenceStore store; private final IdentityReferenceRefreshService refresh; private final RefreshProperties properties; private final RefreshPolicyResolver policies;
 public RefreshSchedulerJob(IdentityReferenceStore store,IdentityReferenceRefreshService refresh,RefreshProperties properties,RefreshPolicyResolver policies){this.store=store;this.refresh=refresh;this.properties=properties;this.policies=policies;}
 @Scheduled(fixedDelayString="${identity-reference.refresh.scheduler-delay:1m}") public void run(){if(!properties.isSchedulerEnabled())return;Instant now=Instant.now();for(IdentityReference reference:store.findDue(now.plus(properties.effectiveSchedulerLookAhead()),properties.getBatchSize())){if(reference.providerRecords().isEmpty())continue;String providerId=reference.providerRecords().getFirst().providerId();RefreshPolicy policy=policies.resolve(providerId);if(!policy.inWindow(now.atZone(properties.getZone()).toLocalTime()))continue;refresh.scheduledRefresh(reference.lookupKey(),providerId,now);}}
}