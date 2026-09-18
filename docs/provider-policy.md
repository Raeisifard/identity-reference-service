# Provider policy

A provider policy is versioned by provider ID and positive policy version. The policy is data, not hard-coded provider behavior.

The current model covers:
- enabled state and provider authority;
- supported lookup claims;
- field ownership;
- fresh TTL and stale grace;
- one or more refresh windows, including windows that cross midnight;
- maximum scheduling jitter;
- request rate and concurrency quotas;
- timeout and retry/backoff settings;
- overwrite behavior;
- photo retention requirements;
- optional embedding contract.

The policy engine derives freshness boundaries, checks refresh-window membership, and evaluates authority-aware overwrite rules.

Policy persistence and runtime loading are intentionally deferred to durable persistence work. Scheduling, quota enforcement and circuit-breaker execution are later milestones.

Policy version must be persisted with acquired data once persistence is introduced, so refresh decisions remain explainable.

Do not apply global newest-wins behavior. Lower-authority data must not replace higher-authority data unless the configured overwrite rule permits it.
