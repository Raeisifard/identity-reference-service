# 14-refresh-and-synchronization-engine.md

Use prompts/00-ai-agent-rules.md first.

## Objective
Implement one shared refresh service for targeted admin refresh, automatic refresh on cache miss/expiry, stale-while-refresh, and scheduled synchronization.

## Acceptance criteria
- Inspect the current persistence, cache, provider, scheduler and admin-refresh code before editing.
- `POST /api/v1/admin/refresh` targets exactly one identity using providerId, nationalId and birthDate.
- Automatic lookup refreshes expired data and avoids provider calls for fresh data.
- Scheduled refresh is bounded by batch size, provider quota, refresh windows and distributed leases.
- Redis TTL remains independent from business freshness.
- Persist retrievedAt/freshUntil/staleUntil/nextRefreshAt plus provider and refresh status metadata.
- Add metrics for automatic, scheduled and admin refresh, provider failures, skips/deferred work and duration.
- Add/update automated tests and documentation.
- Do not implement population-wide provider synchronization through the admin REST endpoint.
- Never log national IDs, birth dates, photographs, embeddings, provider payloads or credentials.

## Handoff
Report changed files, tests run, known limitations and commit hash. Do not start the next milestone automatically.