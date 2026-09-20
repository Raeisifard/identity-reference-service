# Phase 04 — Provider policy engine

**Status: DONE**

A provider policy is versioned by provider ID and positive policy version. The policy is data, not hard-coded provider behavior.

The current model covers enabled state and provider authority, supported lookup claims, field ownership, fresh TTL and stale grace, refresh windows including midnight-crossing windows, scheduling jitter, request rate/concurrency quotas, timeout and retry/backoff settings, overwrite behavior, photo retention requirements and optional embedding contract.

The policy engine derives freshness boundaries, checks refresh-window membership and evaluates authority-aware overwrite rules.

Durable policy state and runtime loading were completed in later persistence work. Scheduling, quota enforcement and failure/recovery behavior are represented in later phases.

Final protected lookup material remains a Phase 21 decision.
