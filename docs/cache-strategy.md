# Cache strategy

Use bounded Caffeine L1, shared Redis L2 and Oracle durable storage.

Business freshness is represented by explicit metadata such as nextRefreshAt; Redis TTL alone is not the business freshness decision.

The Redis L2 implementation uses a versioned JSON envelope and a versioned key prefix. It uses cache-aside semantics and treats Redis read/write/invalidation failures as non-fatal because Oracle is the durable source of truth.

The cache entry TTL is an acceleration bound, not the identity freshness policy. A later cache layer may evict entries earlier or later, but it must never use Redis TTL as the authority for identity freshness.

For 8 million records, benchmark serialized record size and Redis overhead. A 512-dimensional float32 embedding is 2048 bytes before storage overhead; actual capacity must be measured.

Sensitive cache contents require encryption/ACL controls in production. The current milestone deliberately does not invent a key-management mechanism; that belongs to the security/Vault hardening work.

Persist first, then update/invalidate caches.
