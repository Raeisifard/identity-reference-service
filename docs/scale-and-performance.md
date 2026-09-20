# Phase 15 — Scale and performance engineering

## Capacity target

The first production target is approximately 8 million identity references for the first year. The design must remain horizontally scalable toward 80 million and beyond without making population size a fixed architectural limit.

The service must not load the population into JVM memory. Oracle remains the durable source of truth, Redis is an acceleration layer, and Caffeine remains a bounded per-instance L1 cache.

## Refresh capacity

With a 24-hour freshness TTL, a full population refresh requires approximately:

- 8M records / 24h = 92.6 provider lookups/second sustained.
- 80M records / 24h = 925.9 provider lookups/second sustained.

Therefore provider quotas are configuration, not hard-coded capacity assumptions. The default mock profile is tuned to 100 requests/second so the first 8M target can be exercised realistically. A real provider's contractual quota must determine the deployed rate, instance count, and refresh cycle.

The scheduler now uses a 10-second cadence and 1000-record batches by default. Scheduled work is dispatched through a bounded executor rather than running serially on the scheduler thread.

## Refresh executor

Stale-while-refresh and scheduled refresh use a dedicated bounded executor:

- core threads: 8
- maximum threads: 64
- queue capacity: 2000

These values are configuration and must be load-tested against provider latency, Oracle capacity, Redis capacity, and deployment instance count.

A bounded queue is intentional: it prevents an unbounded backlog from becoming a memory problem and allows saturation to be observed rather than hidden.

## Scheduler lookahead

The scheduler lookahead is at least the configured global value and automatically expands to the largest provider refresh-before-expiry value. This prevents a provider with a longer refresh lead time from being missed by the scheduler.

## Persistence

next_refresh_at remains indexed in Oracle and is persisted using the provider-specific refresh policy rather than a hard-coded six-hour offset.

The due query remains bounded with a page size. Population-scale operation must continue to process small windows instead of materializing millions of records.

## Cache sizing

The Caffeine L1 cache remains bounded. Redis TTL is independent of business freshness. Neither cache is treated as the authoritative copy of the population.

For 80M+ growth, Redis capacity must be planned from actual working-set/hit-rate measurements rather than assuming that all durable records belong in Redis.

## Horizontal scaling

Multiple service instances can share the Redis refresh lease. Provider rate/concurrency settings are currently instance-local; deployment capacity must therefore divide the provider's contractual quota across instances until a distributed provider-wide rate limiter is introduced.

The database remains the coordination source for due-record discovery, while the Redis lease prevents duplicate refresh execution for the same identity across instances.

## Phase 15 acceptance criteria

1. 8M is treated as a deployment capacity target, not a hard-coded population limit.
2. The refresh path has no unbounded background task queue.
3. Scheduled refresh is bounded by batch size and provider quota/concurrency.
4. Oracle due-record selection uses indexed next_refresh_at.
5. Provider-specific refresh lead times are honored.
6. L1 remains bounded and Redis remains an acceleration layer.
7. Capacity calculations for 8M and 80M are documented.
8. The next phase addresses stronger failure/idempotency/locking behavior needed for sustained multi-instance operation.
