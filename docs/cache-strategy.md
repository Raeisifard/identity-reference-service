# Cache strategy

Use bounded Caffeine L1, shared Redis L2 and Oracle durable storage.

Business freshness is represented by explicit metadata such as freshUntil, staleUntil and nextRefreshAt; cache eviction is not the business freshness decision.

Redis L2 uses a versioned JSON envelope and versioned key prefix. Its entry TTL is an acceleration/eviction setting and is intentionally independent from provider identity-data validity.

Refresh persistence is performed before cache population. Redis failures remain non-fatal because Oracle is the durable source of truth.

For scheduled refresh, Oracle next_refresh_at is indexed and queried in bounded batches. Redis leases prevent concurrent instances from refreshing the same identity simultaneously.