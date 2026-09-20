# Refresh and synchronization engine

Phase 14 introduces one shared `IdentityReferenceRefreshService` used by targeted admin refresh, automatic lookup refresh, and scheduled refresh.

## Freshness versus cache TTL
- Provider/business freshness is derived from the provider refresh policy: `ttl` and `stale-grace`.
- `nextRefreshAt` is persisted separately in Oracle and is used by the scheduler.
- Redis `entry-ttl` remains a cache eviction setting. It is intentionally independent from provider freshness.
- With `stale-while-refresh=true`, a stale record can be served while one refresh attempt is protected by a lease.

## Targeted admin refresh
`POST /api/v1/admin/refresh` now accepts `providerId`, `nationalId`, and `birthDate`.

The endpoint performs a targeted provider retrieval through the shared refresh service. It does not perform a population-wide synchronization.

## Automatic refresh
Identity lookup checks L1, L2, and durable storage. A fresh record is returned without a provider call. An expired record is synchronously refreshed. A stale record can be served while a background refresh is attempted when `stale-while-refresh` is enabled.

## Scheduled refresh
The scheduler reads due records in bounded batches, checks the configured provider refresh window, applies provider quotas, and acquires a Redis lease when Redis is enabled. The in-memory lease is retained for single-instance/test operation.

## Policy
Provider-specific settings are under `identity-reference.refresh.providers.<providerId>` and include freshness TTL, stale grace, refresh-before-expiry, rate/concurrency limits, retry policy, timeout, and refresh window.

## Refresh state
Oracle stores provider ID/record ID, policy version, refresh status/error/retry count, acquired/fresh/stale boundaries, and `next_refresh_at`.

## Operations and metrics
Micrometer records refresh count by provider/trigger/outcome and refresh duration. Triggers are `automatic`, `scheduled`, and `admin`. Sensitive lookup material is not emitted by the metrics or logs.