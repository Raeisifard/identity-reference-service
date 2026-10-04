package com.isc.identityreference.admin;

import com.isc.identityreference.governance.AuditEvent;
import com.isc.identityreference.governance.AuditEventStore;
import com.isc.identityreference.governance.GovernanceProperties;
import com.isc.identityreference.provider.IdentityProviderRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/console")
@ConditionalOnProperty(prefix = "identity-reference.admin-console", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AdminConsoleOperationsController {
    private final IdentityProviderRegistry providers;
    private final AuditEventStore audit;
    private final GovernanceProperties governance;
    private final AdminConsoleProperties console;

    public AdminConsoleOperationsController(
            IdentityProviderRegistry providers,
            AuditEventStore audit,
            GovernanceProperties governance,
            AdminConsoleProperties console) {
        this.providers = providers;
        this.audit = audit;
        this.governance = governance;
        this.console = console;
    }

    @GetMapping("/scenarios")
    public List<TestScenarioCatalog.Scenario> scenarios() {
        return TestScenarioCatalog.all();
    }

    @GetMapping("/providers")
    public List<Map<String, Object>> providers() {
        return providers.all().stream()
                .map(p -> Map.<String, Object>of(
                        "providerId", p.descriptor().providerId(),
                        "displayName", p.descriptor().displayName(),
                        "available", true))
                .sorted((a, b) -> a.get("providerId").toString().compareTo(b.get("providerId").toString()))
                .toList();
    }

    @GetMapping("/audit")
    public List<AuditEvent> audit(@RequestParam(defaultValue = "25") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return audit.recent(safeLimit);
    }

    @GetMapping("/governance")
    public Map<String, Object> governance() {
        return Map.of(
                "enabled", governance.isEnabled(),
                "identityRetention", governance.getIdentityRetention().toString(),
                "retiredRetention", governance.getRetiredRetention().toString(),
                "auditRetention", governance.getAuditRetention().toString(),
                "purgeDelay", governance.getPurgeDelay().toString(),
                "purgeBatchSize", governance.getPurgeBatchSize());
    }

    @GetMapping("/capabilities")
    public Map<String, Object> capabilities() {
        return Map.of(
                "snapshotAt", Instant.now().toString(),
                "developmentMode", console.isDevelopmentMode(),
                "authenticationEnabled", console.getAuthentication().isEnabled(),
                "redisIsOptional", true,
                "localInMemoryCache", false,
                "durableStore", console.isDevelopmentMode() ? "H2 file-backed (profile-dependent)" : "Oracle (profile-dependent)",
                "testDataEnabled", console.getSections().isTestData(),
                "apiTestingEnabled", console.getSections().isApiTesting(),
                "biometricConsoleEnabled", console.getSections().isBiometric(),
                "governanceEnabled", governance.isEnabled());
    }
}
