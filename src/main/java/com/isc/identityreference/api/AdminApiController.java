package com.isc.identityreference.api;

import com.isc.identityreference.observability.IdentityReferenceMetrics;
import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.refresh.IdentityReferenceRefreshService;
import com.isc.identityreference.refresh.RefreshResult;
import com.isc.identityreference.governance.DataGovernanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController @RequestMapping("/api/v1/admin") public class AdminApiController {
 private final com.isc.identityreference.provider.IdentityProviderRegistry providers; private final IdentityReferenceMetrics metrics; private final IdentityReferenceRefreshService refresh; private final DataGovernanceService governance;
 public AdminApiController(com.isc.identityreference.provider.IdentityProviderRegistry providers,IdentityReferenceMetrics metrics,IdentityReferenceRefreshService refresh,DataGovernanceService governance){this.providers=providers;this.metrics=metrics;this.refresh=refresh;this.governance=governance;}
 @PostMapping("/refresh") public ResponseEntity<AdminRefreshResponse> refresh(@Valid @RequestBody AdminRefreshRequest request){
  if(providers.find(request.providerId()).isEmpty()){metrics.refreshOutcome(request.providerId(),"admin","provider-unavailable");return ResponseEntity.accepted().body(new AdminRefreshResponse("REJECTED",null,"Provider is not available"));}
  metrics.refreshAccepted(request.providerId()); RefreshResult result=refresh.forceRefresh(new IdentityLookupKey(request.nationalId(),request.birthDate()),request.providerId(),Instant.now());
  return switch(result.status()){case REFRESHED->ResponseEntity.ok(new AdminRefreshResponse("REFRESHED",result.operationId(),result.message()));case NOT_FOUND->ResponseEntity.status(404).body(new AdminRefreshResponse("NOT_FOUND",result.operationId(),result.message()));case LOCKED->ResponseEntity.accepted().body(new AdminRefreshResponse("LOCKED",result.operationId(),result.message()));default->ResponseEntity.status(503).body(new AdminRefreshResponse("FAILED",result.operationId(),result.message()));};
 }
 @PostMapping("/retire") public ResponseEntity<AdminRetireResponse> retire(@Valid @RequestBody AdminRetireRequest request){java.util.UUID op=java.util.UUID.randomUUID();var result=governance.retire(new IdentityLookupKey(request.nationalId(),request.birthDate()),"ADMIN",request.reasonCode(),op,Instant.now());if(result.isEmpty())return ResponseEntity.status(404).body(new AdminRetireResponse("NOT_FOUND",op,"Identity reference not found"));return ResponseEntity.ok(new AdminRetireResponse("RETIRED",op,"Identity reference retired"));}\n @GetMapping("/status") public ResponseEntity<AdminStatusResponse> status(){return ResponseEntity.ok(new AdminStatusResponse("UP",providers.all().stream().map(p->p.descriptor().providerId()).sorted().toList()));}
 public record AdminStatusResponse(String status,java.util.List<String> providers){}
}