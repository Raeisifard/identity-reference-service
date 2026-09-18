package com.isc.identityreference.scheduler;

import java.time.Duration;
import java.time.Instant;

public interface RefreshLeaseManager {
    boolean tryAcquire(String identityReferenceId, String providerId, Instant now, Duration leaseDuration);
}
