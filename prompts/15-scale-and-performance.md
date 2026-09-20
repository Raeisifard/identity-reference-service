# Phase 15 — 8-million-record scale and performance engineering

## Objective

Tune identity-reference-service for an initial population of approximately 8 million people while keeping the architecture scalable toward 80 million and beyond.

## Required outcomes

- Keep Oracle as durable source of truth.
- Keep Caffeine L1 bounded and Redis L2 as an acceleration layer.
- Process scheduled refreshes in bounded batches.
- Use a dedicated bounded executor for stale-while-refresh and scheduled work.
- Respect provider-specific rate and concurrency limits.
- Avoid unbounded background queues and synchronized refresh storms.
- Persist provider-specific next_refresh_at.
- Ensure scheduler lookahead covers the largest configured provider refresh-before-expiry.
- Preserve targeted admin refresh semantics.
- Do not introduce an admin endpoint for population-wide refresh.

## Capacity model

For a 24-hour freshness cycle:

- 8M requires about 92.6 refreshes/second sustained.
- 80M requires about 925.9 refreshes/second sustained.

These are planning figures, not promises about any external provider. Deployment capacity must be derived from provider contractual quotas and measured Oracle/Redis/provider latency.

## Engineering work

1. Benchmark Oracle due-record queries and lookup queries with realistic indexes.
2. Test bounded scheduled batches.
3. Test refresh executor saturation and rejection behavior.
4. Measure provider throughput, Oracle write throughput, Redis hit rate, and refresh latency.
5. Exercise multiple application instances with Redis leases.
6. Test cache behavior with a working set much smaller than the durable population.
7. Produce a repeatable synthetic/load-test procedure for 8M records without requiring 8M Java objects in the application heap.
8. Document scaling from 8M to 80M+ by adding instances, partitioning, or database capacity rather than redesigning the domain API.

## Out of scope

- National-ID cryptographic protection; that remains the dedicated data-governance/security work.
- Provider-wide refresh through REST.
- Replacing Oracle with a cache.
