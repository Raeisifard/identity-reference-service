package com.isc.identityreference.refresh;
import java.time.Duration;
import java.util.Optional;
public interface RefreshLeaseManager {
    Optional<Lease> tryAcquire(String key, Duration lease);
    void release(Lease lease);
    record Lease(String key, String token) {
        public Lease {
            if (key == null || key.isBlank()) throw new IllegalArgumentException("key must not be blank");
            if (token == null || token.isBlank()) throw new IllegalArgumentException("token must not be blank");
        }
    }
}