# Phase 15 — 8-million-record scale and performance engineering

**Status: DONE**

The first production target is approximately 8 million identity references for the first year, while the architecture remains horizontally scalable toward 80 million and beyond.

The service must not load the population into JVM memory. Oracle remains durable source of truth, Redis is an acceleration layer, and Caffeine remains a bounded per-instance L1 cache.

## Refresh capacity

With a 24-hour freshness TTL:
- 8M records / 24h = 92.6 provider lookups/second sustained.
- 80M records / 24h = 925.9 provider lookups/second sustained.

The scheduler uses bounded batches and a dedicated bounded executor. Provider quotas remain configuration and real deployment capacity must follow contractual provider limits and measured Oracle/Redis/provider latency.

## Refresh executor
- core threads: 8
- maximum threads: 64
- queue capacity: 2000

The bounded queue prevents an unbounded backlog from becoming a memory problem.

## Scheduler lookahead
The scheduler lookahead is at least the configured global value and expands to the largest provider refresh-before-expiry value.

## Persistence
`next_refresh_at` remains indexed in Oracle and is persisted using the provider-specific refresh policy.

## Horizontal scaling
Multiple service instances can share the Redis refresh lease. Provider rate/concurrency settings are currently instance-local; deployment capacity must therefore divide contractual provider quota across instances until a distributed provider-wide limiter is introduced.

## Handoff
Phase 16 addresses stronger failure/idempotency/locking/recovery behavior needed for sustained multi-instance operation.
