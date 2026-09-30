# Phase 01–15 — Current architecture baseline

**Status: IMPLEMENTED BASELINE**

Request path:
Client -> API -> optional Redis -> durable database (H2 in dev, Oracle in production) -> provider acquisition when missing/stale -> normalize/validate -> optional embedding -> cache.

Refresh path:
Scheduler -> due records -> provider policy -> distributed lease when Redis is enabled -> provider adapter -> validation -> merge -> persist -> optional cache -> metrics/audit as the later milestones are completed.

The selected database is durable truth. Redis is an optional acceleration/coordination layer. When Redis is disabled, requests go directly to the selected database with no local cache fallback.

API, application, domain, provider, policy, cache, persistence, biometric, scheduler, security and observability boundaries remain separate.

This is an architectural baseline, not a claim that later phases 16–22 are complete.
