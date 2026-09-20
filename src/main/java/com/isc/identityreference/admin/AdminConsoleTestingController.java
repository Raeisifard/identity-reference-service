package com.isc.identityreference.admin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/console/testing")
@ConditionalOnProperty(prefix = "identity-reference.admin-console", name = "integration-testing-enabled", havingValue = "true")
public class AdminConsoleTestingController {
    @PostMapping("/smoke")
    public ResponseEntity<Map<String, Object>> smoke() {
        return ResponseEntity.accepted().body(Map.of("status", "QUEUED", "queuedAt", Instant.now(),
                "checks", List.of("provider-registry", "health", "admin-api-boundary", "cache-configuration"),
                "message", "Synthetic smoke test accepted; no identity payloads are used."));
    }
}
