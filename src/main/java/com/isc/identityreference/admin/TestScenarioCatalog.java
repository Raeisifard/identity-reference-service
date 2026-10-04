package com.isc.identityreference.admin;

import java.util.List;

public final class TestScenarioCatalog {
    private TestScenarioCatalog() {}

    public record Scenario(String id, String label, String description, String expectedBehavior) {}

    private static final List<Scenario> SCENARIOS = List.of(
        new Scenario("VALID_IDENTITY", "Valid identity", "Normal active identity with current reference data.", "Lookup should resolve an ACTIVE identity."),
        new Scenario("EXPIRED_IDENTITY", "Expired identity", "Identity whose reference expiration has passed.", "Use to test expiry, stale-data and refresh decisions."),
        new Scenario("EXPIRING_IDENTITY", "Expiring identity", "Identity approaching its expiration boundary.", "Use to test refresh-before-expiry behavior."),
        new Scenario("NO_PHOTO", "No photo", "Identity reference deliberately created without a reference photo.", "Identity remains usable while biometric/photo workflows must report unavailable."),
        new Scenario("PHOTO_REPLACEMENT", "Photo replacement", "Fixture with a photo that will be replaced during the test.", "Photo version must advance and biometric processing must become pending/rebuilt."),
        new Scenario("BIOMETRIC_REBUILD", "Biometric rebuild", "Fixture intended to exercise explicit biometric reconstruction.", "Rebuild uses the configured development biometric path."),
        new Scenario("BIOMETRIC_FAILED", "Biometric failure", "Fixture intended to exercise a biometric processing failure path.", "The fixture must expose FAILED rather than claiming successful processing."),
        new Scenario("FACE_MATCH", "Face match", "Reference identity intended for a positive face comparison.", "Use the newest compatible biometric reference for a positive match."),
        new Scenario("FACE_MISMATCH", "Face mismatch", "Reference identity intended for a negative face comparison.", "Comparison should not match an unrelated probe."),
        new Scenario("MULTI_EMBEDDING", "Multiple embeddings", "Identity intended for model/version-aware multiple biometric references.", "Test selection of compatible biometric references."),
        new Scenario("PROVIDER_NOT_FOUND", "Provider not found", "Lookup/refresh scenario where the requested provider has no matching identity.", "Provider lookup should return the defined NOT_FOUND outcome."),
        new Scenario("PROVIDER_TIMEOUT", "Provider timeout", "Provider response exceeds its configured timeout.", "Refresh should classify the provider failure as timeout and apply retry/backoff policy."),
        new Scenario("PROVIDER_FAILURE", "Provider failure", "Provider returns an unavailable or failed result.", "Refresh should record a safe failure and follow retry policy."),
        new Scenario("PROVIDER_UNAVAILABLE", "Provider unavailable", "Requested provider is not registered/available in the running service.", "Administrative refresh must reject the request safely."),
        new Scenario("REFRESH_REQUIRED", "Refresh required", "Reference is stale or due for provider synchronization.", "Refresh should acquire the normal lease/quota controls."),
        new Scenario("REFRESH_LOCKED", "Refresh locked", "A refresh is already in progress or quota prevents immediate execution.", "The operation should report LOCKED rather than duplicate work."),
        new Scenario("REFRESH_RETRY_EXHAUSTED", "Refresh retry exhausted", "Repeated provider failures have reached the configured retry limit.", "Reference should expose the retry-exhausted state and next-refresh behavior."),
        new Scenario("STALE_WHILE_REFRESH", "Stale while refresh", "A stale reference is served while background refresh is attempted.", "Client receives stale data while synchronization proceeds asynchronously."),
        new Scenario("RETIRED_IDENTITY", "Retired identity", "Identity intentionally moved to the RETIRED lifecycle state.", "Normal use should follow the existing lifecycle/governance rules."),
        new Scenario("DUPLICATE_IDENTITY", "Duplicate identity", "Creation attempt uses an identity key that already exists.", "Creation must be rejected rather than overwrite an existing identity."),
        new Scenario("RATE_LIMITED", "Rate limited", "Request volume exceeds the configured API rate limit.", "The API should return the defined rate-limit response.")
    );

    public static List<Scenario> all() {
        return SCENARIOS;
    }

    public static boolean isKnown(String id) {
        return id != null && SCENARIOS.stream().anyMatch(s -> s.id().equals(id));
    }
}
