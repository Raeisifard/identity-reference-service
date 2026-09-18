# Cache strategy

Use bounded Caffeine L1, shared Redis L2 and Oracle durable storage.

Business freshness is represented by explicit metadata such as nextRefreshAt; Redis TTL alone is not the business freshness decision.

Recommended Redis key: identity-ref:v1:{identityKey}. Use explicit versioned serialization and avoid Java native object serialization.

For 8 million records, benchmark serialized record size and Redis overhead. A 512-dimensional float32 embedding is 2048 bytes before storage overhead; actual capacity must be measured.

Persist first, then update/invalidate caches. Cache loss must be recoverable from Oracle.