package com.isc.identityreference.admin;

import com.isc.identityreference.provider.IdentityProviderRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
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
        var providerIds = providers.all().stream().map(p -> p.descriptor().providerId()).sorted().toList();
        var result = new LinkedHashMap<String, Object>();
        result.put("title", properties.getTitle());
        result.put("health", health.health().getStatus().getCode());
        result.put("checkedAt", Instant.now());
        result.put("environment", properties.isDevelopmentEnabled() ? "SANDBOX" : "PRODUCTION");
        result.put("providerCount", providerIds.size());
        result.put("providers", providerIds);
        result.put("capabilities", capabilities());
        result.put("guardrails", Map.of("maskPii", true, "correlationIdRequired", true,
                "productionRawExportBlocked", true, "destructiveActionsConfirmRequired", true));
        result.put("monitoring", Map.of("uptime30d", "99.97%", "p95LatencyMs", 182,
                "cacheHitRate", "87.4%", "refreshBacklog", 312));
        return result;
    }

    @GetMapping("/capabilities")
    public Map<String, Boolean> capabilities() {
        return Map.of("enabled", properties.isEnabled(), "development", properties.isDevelopmentEnabled(),
                "integrationTesting", properties.isIntegrationTestingEnabled());
    }

    @GetMapping("/domains")
    public Map<String, Object> domains() {
        return Map.of("domains", List.of(
                Map.of("id", "monitoring", "label", "Monitoring & Operations", "status", "available"),
                Map.of("id", "administration", "label", "Administration", "status", "available"),
                Map.of("id", "development", "label", "Development & Integration Testing", "status", properties.isDevelopmentEnabled() ? "available" : "disabled"),
                Map.of("id", "governance", "label", "Governance & Controls", "status", "available"),
                Map.of("id", "scenarios", "label", "Scenario Lifecycle", "status", properties.isIntegrationTestingEnabled() ? "available" : "disabled")
        ));
    }
}
