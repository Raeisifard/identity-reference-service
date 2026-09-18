# Architecture

Request path:
Client -> API -> Caffeine L1 -> Redis L2 -> Oracle -> provider acquisition when missing/stale -> normalize/validate -> optional embedding -> Oracle -> cache.

Refresh path:
Scheduler -> due records -> provider policy -> distributed lease -> provider adapter -> validation -> merge -> persist -> embedding if required -> cache -> audit/metrics.

Oracle is durable truth. Redis and Caffeine are acceleration layers and must be rebuildable.

Keep API, application, domain, provider, policy, cache, persistence, biometric, scheduler, security and observability boundaries separate.