# Cache strategy

Use bounded Caffeine L1, shared Redis L2 and Oracle durable storage.

Business freshness is represented by explicit metadata such as nextRefreshAt; cache eviction is not the business freshness decision.

Caffeine L1 is bounded by maximum entry count and records hit statistics. The current default bound is 10,000 entries and is configuration-driven in application settings.

Redis L2 uses a versioned JSON envelope and versioned key prefix. Redis read/write/invalidation failures are non-fatal because Oracle is the durable source of truth.

Cache TTLs are acceleration bounds, not identity freshness policy. Cache loss must be recoverable from Oracle.

For 8 million records, benchmark serialized record size and cache overhead. A 512-dimensional float32 embedding is 2048 bytes before storage overhead; actual capacity must be measured.

Sensitive cache contents require encryption/ACL controls in production. The current milestones do not invent a key-management mechanism.

Persist first, then update/invalidate caches.
