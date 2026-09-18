# Provider policy

A provider policy defines enabled state, authority level, supported claim inputs, field ownership, validation, TTL, stale grace, refresh windows, jitter, request rate/concurrency quotas, timeout, retry/circuit breaker rules, overwrite/conflict behavior, photo requirements, embedding behavior and retention.

Persist the policy version with acquired data so refresh decisions remain explainable.

Do not apply newest-wins globally; lower-authority sources must not overwrite authoritative fields unless policy permits it.