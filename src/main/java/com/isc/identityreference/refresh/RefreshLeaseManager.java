package com.isc.identityreference.refresh;
import java.time.Duration;
public interface RefreshLeaseManager { boolean tryAcquire(String key,Duration lease); }