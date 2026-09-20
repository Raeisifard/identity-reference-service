# Phase 16 — Failure handling, idempotency, locking and recovery

Use prompts/00-ai-agent-rules.md first.

## Objective
Harden the refresh and acquisition paths against provider outages, timeouts, duplicates, concurrent work, lease expiry, process crashes and partial failures.

## Required outcomes
- Use distributed leases consistently for automatic, scheduled and targeted refresh where concurrent execution is possible.
- Make refresh state transitions idempotent and safe to repeat.
- Enforce provider timeouts and bounded retries with policy-driven backoff.
- Prevent duplicate provider calls caused by concurrent refresh requests where practical.
- Handle bounded executor saturation without silently losing refresh work.
- Make expired leases and interrupted refreshes recoverable.
- Define failure states and retry scheduling without destroying the last known usable reference.
- Add tests for provider outage, timeout, duplicate request, concurrent refresh, lock contention/expiry, executor rejection and crash-recovery scenarios.
- Keep sensitive identity, biometric, provider and secret data out of logs.

## Handoff
Report changed files, tests run, known limitations and commit hash. Do not start the next milestone automatically.
