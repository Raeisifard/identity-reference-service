package com.isc.identityreference.policy;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record ProviderPolicy(
        String providerId, long version, boolean enabled, ProviderAuthority authority,
        Set<ProviderClaim> supportedClaims, Map<IdentityField, FieldOwnership> fieldOwnership,
        Duration ttl, Duration staleGrace, List<RefreshWindow> refreshWindows, Duration maxJitter,
        RequestQuota quota, RetryPolicy retryPolicy, Duration timeout, OverwriteRule overwriteRule,
        PhotoPolicy photoPolicy, EmbeddingPolicy embeddingPolicy) {

    public ProviderPolicy {
        requireNonBlank(providerId, "providerId");
        if (version <= 0) throw new IllegalArgumentException("version must be positive");
        Objects.requireNonNull(authority, "authority");
        supportedClaims = Set.copyOf(Objects.requireNonNull(supportedClaims, "supportedClaims"));
        fieldOwnership = Map.copyOf(Objects.requireNonNull(fieldOwnership, "fieldOwnership"));
        requirePositive(ttl, "ttl"); requireNonNegative(staleGrace, "staleGrace");
        refreshWindows = List.copyOf(Objects.requireNonNull(refreshWindows, "refreshWindows"));
        requireNonNegative(maxJitter, "maxJitter"); Objects.requireNonNull(quota, "quota");
        Objects.requireNonNull(retryPolicy, "retryPolicy"); requirePositive(timeout, "timeout");
        Objects.requireNonNull(overwriteRule, "overwriteRule");
        Objects.requireNonNull(photoPolicy, "photoPolicy"); Objects.requireNonNull(embeddingPolicy, "embeddingPolicy");
    }

    public boolean supports(ProviderClaim claim) { return supportedClaims.contains(Objects.requireNonNull(claim, "claim")); }
    public FieldOwnership ownershipOf(IdentityField field) { return fieldOwnership.getOrDefault(Objects.requireNonNull(field, "field"), FieldOwnership.UNSPECIFIED); }

    private static void requireNonNegative(Duration value, String name) { Objects.requireNonNull(value, name); if (value.isNegative()) throw new IllegalArgumentException(name + " must not be negative"); }
    private static void requirePositive(Duration value, String name) { requireNonNegative(value, name); if (value.isZero()) throw new IllegalArgumentException(name + " must be positive"); }
    private static void requireNonBlank(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank"); }

    public enum ProviderClaim { NATIONAL_ID_AND_BIRTH_DATE }
    public enum IdentityField { GIVEN_NAME, FAMILY_NAME, FATHER_NAME, BIRTH_DATE, GENDER, NATIONAL_ID }
    public enum FieldOwnership { OWNED, SHARED, UNSPECIFIED }
    public enum OverwriteRule { NEVER, SAME_OR_HIGHER_AUTHORITY, EXPLICIT_POLICY }

    public record RefreshWindow(LocalTime start, LocalTime end) {
        public RefreshWindow { Objects.requireNonNull(start, "start"); Objects.requireNonNull(end, "end"); }
        public boolean contains(LocalTime time) {
            Objects.requireNonNull(time, "time");
            if (start.equals(end)) return true;
            if (start.isBefore(end)) return !time.isBefore(start) && time.isBefore(end);
            return !time.isBefore(start) || time.isBefore(end);
        }
    }

    public record RequestQuota(long maxRequestsPerSecond, int maxConcurrentRequests) {
        public RequestQuota { if (maxRequestsPerSecond <= 0) throw new IllegalArgumentException("maxRequestsPerSecond must be positive"); if (maxConcurrentRequests <= 0) throw new IllegalArgumentException("maxConcurrentRequests must be positive"); }
    }

    public record RetryPolicy(int maxAttempts, Duration initialBackoff, Duration maxBackoff) {
        public RetryPolicy {
            if (maxAttempts <= 0) throw new IllegalArgumentException("maxAttempts must be positive");
            requirePositive(initialBackoff, "initialBackoff"); requirePositive(maxBackoff, "maxBackoff");
            if (maxBackoff.compareTo(initialBackoff) < 0) throw new IllegalArgumentException("maxBackoff must not precede initialBackoff");
        }
        private static void requirePositive(Duration value, String name) { Objects.requireNonNull(value, name); if (value.isZero() || value.isNegative()) throw new IllegalArgumentException(name + " must be positive"); }
    }

    public record PhotoPolicy(boolean required, boolean retainRawPhoto) {}

    public record EmbeddingPolicy(
            boolean enabled, String modelId, String modelVersion, int dimension,
            EmbeddingMetric metric, boolean normalized) {
        public EmbeddingPolicy(boolean enabled, String modelId, String modelVersion, int dimension) {
            this(enabled, modelId, modelVersion, dimension, EmbeddingMetric.COSINE, true);
        }
        public EmbeddingPolicy {
            Objects.requireNonNull(metric, "metric");
            if (enabled) {
                requireNonBlank(modelId, "modelId"); requireNonBlank(modelVersion, "modelVersion");
                if (dimension <= 0) throw new IllegalArgumentException("dimension must be positive");
            } else if (dimension < 0) {
                throw new IllegalArgumentException("dimension must not be negative");
            }
        }
        private static void requireNonBlank(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank"); }
    }
}
