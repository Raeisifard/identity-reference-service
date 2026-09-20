# Phase 08 — Acquisition and refresh pipeline

**Status: DONE**

The Phase 08 application pipeline boundary is implemented.

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

The current idempotency store is process-local and is not sufficient for a multi-instance deployment. Strong distributed idempotency, lease recovery and failure handling are Phase 16 work.

The lookup key used by this early milestone is an internal deterministic composite for orchestration tests. Final protected lookup material remains part of the Phase 21 data-protection decision.
