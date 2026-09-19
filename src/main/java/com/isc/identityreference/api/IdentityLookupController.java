package com.isc.identityreference.api;

import com.isc.identityreference.security.ApiRateLimitGuard;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityLookupController {
    private final IdentityLookupService service;
    private final ApiRateLimitGuard rateLimitGuard;

    public IdentityLookupController(IdentityLookupService service, ApiRateLimitGuard rateLimitGuard) {
        this.service = service;
        this.rateLimitGuard = rateLimitGuard;
    }

    @PostMapping("/lookup")
    public ResponseEntity<IdentityLookupResponse> lookup(
            @Valid @RequestBody IdentityLookupRequest request,
            Authentication authentication) {
        String principal = authentication == null ? "anonymous" : authentication.getName();
        if (!rateLimitGuard.allow(principal)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new IdentityLookupResponse("RATE_LIMITED", request.providerId(),
                            "Too many lookup requests"));
        }
        return ResponseEntity.ok(service.lookup(request));
    }
}
