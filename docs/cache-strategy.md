# Phases 06–07 — Redis L2 and optional Redis cache strategy

**Status: DONE**

Redis is an optional acceleration layer. The selected durable database remains the source of truth: H2 in development and Oracle in production.

Business freshness is represented by explicit metadata such as freshUntil, staleUntil and nextRefreshAt; cache eviction is not the business freshness decision.

Redis L2 uses a versioned JSON envelope and versioned key prefix. Its entry TTL is an acceleration/eviction setting and is intentionally independent from provider identity-data validity.

When Redis is disabled, lookup and lifecycle operations use the durable database directly. There is no Caffeine or substitute local cache fallback.

Refresh persistence is performed before cache population. Redis cache failures remain non-fatal because the durable database remains authoritative. Redis-backed refresh leases are a separate coordination concern and remain conditional on Redis being enabled.
