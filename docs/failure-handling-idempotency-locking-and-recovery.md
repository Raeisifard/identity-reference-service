# Phase 16 — Failure handling, idempotency, locking and recovery

**Status: DONE**

Implemented:
- token-owned per-reference leases for automatic, scheduled and administrative refresh;
- Redis SET NX TTL leases with compare-and-delete release and in-memory equivalent;
- bounded provider executor with policy-enforced timeouts;
- bounded provider retries with policy-driven backoff;
- durable refresh failure status, retry count, safe error text and next_refresh_at;
- retry scheduling with a configurable failure bound and cooldown after exhaustion;
- preservation of the last usable identity reference on provider failure;
- bounded refresh executor saturation; due work remains discoverable by the scheduler;
- sensitive identity/provider payloads are not logged.

Recovery model:
1. The lease prevents concurrent duplicate work.
2. Lease TTL provides crash recovery if the owner disappears.
3. A successful refresh resets refresh failure metadata through the normal save path.
4. A failed refresh leaves identity data untouched and schedules the next attempt.
5. Exhausted refresh failures are deferred by the provider TTL to avoid a hot retry loop.

Known limitation: leases are TTL based and are not renewed. The configured lease must exceed the complete provider timeout/retry/backoff budget. Distributed provider-wide quota coordination remains outside Phase 16.

Validation added for lease ownership/release and provider timeout preservation.