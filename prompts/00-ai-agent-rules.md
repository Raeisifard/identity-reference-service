# AI coding-agent rules

Use this file together with exactly one numbered milestone prompt.

1. Inspect the current repository before editing.
2. Read README.md and relevant docs.
3. Preserve sound existing conventions.
4. Never invent provider endpoints or credentials; use mocks until real API documentation exists.
5. Never log raw national IDs, birth dates, photos, embeddings, provider payloads or secrets.
6. Do not make JVM heap the primary database for 8 million identities.
7. Treat Oracle as durable storage and Redis/Caffeine as acceleration layers.
8. A 512-D vector is not automatically compatible with another 512-D vector.
9. Refresh scheduling must be quota-aware and jittered.
10. Use explicit clocks in freshness/scheduler tests.
11. Prefer idempotent operations and concurrency-safe state transitions.
12. Run tests after meaningful changes and fix regressions.
13. Keep milestones independently reviewable.
14. Commit each completed milestone with a focused commit.
15. Do not silently implement the next milestone.