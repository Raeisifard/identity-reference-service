# Phase 15 — 8-million-record scale and performance engineering

**Status: DONE**

The first production target is approximately 8 million identity references for the first year, while the architecture remains horizontally scalable toward 80 million and beyond.

The service must not load the population into JVM memory. The durable database is the source of truth, Redis is an optional acceleration layer, and no local in-memory cache is required when Redis is disabled.

## Refresh capacity

With a 24-hour freshness TTL:
- 8M records / 24h = 92.6 provider lookups/second sustained.
- 80M records / 24h = 925.9 provider lookups/second sustained.

The scheduler uses bounded batches and a dedicated bounded executor. Provider quotas remain configuration and real deployment capacity must follow contractual provider limits and measured database/Redis/provider latency.

## Persistence
`next_refresh_at` remains indexed in Oracle and is persisted using the provider-specific refresh policy. H2 development uses the equivalent logical schema and indexes.

## Horizontal scaling
Multiple service instances can share the Redis refresh lease when Redis is enabled. Provider rate/concurrency settings are currently instance-local; deployment capacity must therefore divide contractual provider quota across instances until a distributed provider-wide limiter is introduced.
