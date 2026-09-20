# Phase 01–15 — Current architecture baseline

**Status: IMPLEMENTED BASELINE**

Request path:
Client -> API -> Caffeine L1 -> Redis L2 -> Oracle -> provider acquisition when missing/stale -> normalize/validate -> optional embedding -> Oracle -> cache.

Refresh path:
Scheduler -> due records -> provider policy -> distributed lease -> provider adapter -> validation -> merge -> persist -> embedding if required -> cache -> metrics/audit as the later milestones are completed.

Oracle is durable truth. Redis and Caffeine are acceleration layers and must be rebuildable.

API, application, domain, provider, policy, cache, persistence, biometric, scheduler, security and observability boundaries remain separate.

This is an architectural baseline, not a claim that later phases 16–22 are complete.
