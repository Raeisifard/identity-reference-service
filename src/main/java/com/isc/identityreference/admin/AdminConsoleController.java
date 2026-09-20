package com.isc.identityreference.admin;

import com.isc.identityreference.provider.IdentityProviderRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/console")
@ConditionalOnProperty(prefix = "identity-reference.admin-console", name = "enabled", havingValue = "true")
public class AdminConsoleController {
    private final AdminConsoleProperties properties;
    private final IdentityProviderRegistry providers;
    private final HealthEndpoint health;

    public AdminConsoleController(AdminConsoleProperties properties, IdentityProviderRegistry providers, HealthEndpoint health) {
        this.properties = properties;
        this.providers = providers;
        this.health = health;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        var result = new LinkedHashMap<String, Object>();
        result.put("title", properties.getTitle());
        result.put("health", health.health().getStatus().getCode());
        result.put("checkedAt", Instant.now());
        result.put("providerCount", providers.all().size());
        result.put("providers", providers.all().stream().map(p -> p.descriptor().providerId()).sorted().toList());
        result.put("capabilities", capabilities());
        return result;
    }

    @GetMapping("/capabilities")
    public Map<String, Boolean> capabilities() {
        return Map.of("enabled", properties.isEnabled(), "development", properties.isDevelopmentEnabled(),
                "integrationTesting", properties.isIntegrationTestingEnabled());
    }

    @GetMapping("/domains")
    public Map<String, Object> domains() {
        return Map.of("domains", java.util.List.of(
                Map.of("id", "overview", "label", "Overview", "status", "available"),
                Map.of("id", "providers", "label", "Providers", "status", "available"),
                Map.of("id", "pipeline", "label", "Identity pipeline", "status", "available"),
                Map.of("id", "cache", "label", "Cache", "status", "available"),
                Map.of("id", "refresh", "label", "Refresh & scheduler", "status", "available"),
                Map.of("id", "persistence", "label", "Persistence", "status", "available"),
                Map.of("id", "metrics", "label", "Metrics", "status", "available"),
                Map.of("id", "testing", "label", "Integration testing", "status", properties.isIntegrationTestingEnabled() ? "available" : "disabled")
        ));
    }
}
