package com.isc.identityreference.api;

import com.isc.identityreference.observability.IdentityReferenceMetrics;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminApiController {
    private final com.isc.identityreference.provider.IdentityProviderRegistry providers;
    private final IdentityReferenceMetrics metrics;

    public AdminApiController(com.isc.identityreference.provider.IdentityProviderRegistry providers, IdentityReferenceMetrics metrics) {
        this.providers = providers;
        this.metrics = metrics;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AdminRefreshResponse> refresh(@Valid @RequestBody AdminRefreshRequest request) {
        if (providers.find(request.providerId()).isEmpty()) {
            return ResponseEntity.accepted().body(new AdminRefreshResponse(
                    "REJECTED", null, "Provider is not available"));
        }
        metrics.refreshAccepted(request.providerId());
        return ResponseEntity.accepted().body(new AdminRefreshResponse(
                "QUEUED", UUID.randomUUID().toString(), "Refresh request accepted for the scheduler pipeline"));
    }

    @GetMapping("/status")
    public ResponseEntity<AdminStatusResponse> status() {
        return ResponseEntity.ok(new AdminStatusResponse(
                "UP", providers.all().stream().map(p -> p.descriptor().providerId()).sorted().toList()));
    }

    public record AdminStatusResponse(String status, java.util.List<String> providers) {}
}
