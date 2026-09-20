package com.isc.identityreference.api;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.observability.IdentityReferenceMetrics;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import com.isc.identityreference.refresh.IdentityReferenceRefreshService;
import com.isc.identityreference.refresh.RefreshResult;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Objects;

@Service
public class IdentityLookupService {
 private final IdentityProviderRegistry providers; private final IdentityReferenceMetrics metrics; private final IdentityReferenceRefreshService refresh;
 public IdentityLookupService(IdentityProviderRegistry providers,IdentityReferenceMetrics metrics,IdentityReferenceRefreshService refresh){this.providers=providers;this.metrics=metrics;this.refresh=refresh;}
 public IdentityLookupResponse lookup(IdentityLookupRequest request){
  String providerId=request.providerId(); Timer.Sample sample=Timer.start();
  if(providers.find(providerId).isEmpty()){metrics.lookup(providerId,"UNAVAILABLE");sample.stop(metrics.lookupTimer(providerId));return new IdentityLookupResponse("UNAVAILABLE",providerId,"Identity lookup is temporarily unavailable");}
  RefreshResult result=refresh.getOrRefresh(new IdentityLookupKey(request.nationalId(),request.birthDate()),providerId,Instant.now());
  String outcome=switch(result.status()){case REFRESHED,SKIPPED,STALE_SERVED->"FOUND";case NOT_FOUND->"NOT_FOUND";default->"UNAVAILABLE";};
  metrics.lookup(providerId,outcome); sample.stop(metrics.lookupTimer(providerId));
  String message=switch(outcome){case "FOUND"->"Identity match found";case "NOT_FOUND"->"No matching identity was found";default->"Identity lookup is temporarily unavailable";};
  return new IdentityLookupResponse(outcome,providerId,message);
 }
}