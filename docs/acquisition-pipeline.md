# Acquisition and refresh pipeline

Milestone 08 introduces the application pipeline boundary for provider acquisition.

The pipeline:
1. checks provider/policy eligibility;
2. checks L1 then L2;
3. calls the normalized provider SPI on a miss;
4. handles FOUND, NOT_FOUND and ERROR without leaking provider payloads;
5. creates freshness from the versioned provider policy;
6. applies authority-aware overwrite rules;
7. persists before updating caches;
8. updates L1 and L2 after persistence;
9. keeps a request idempotency map to suppress duplicate work.

The current idempotency store is process-local and therefore not sufficient for a multi-instance deployment. Distributed idempotency/locking is explicitly deferred to milestone 15.

The cache lookup key in this milestone is an internal deterministic composite for orchestration tests. A keyed lookup digest should replace it at the security boundary before production deployment.
