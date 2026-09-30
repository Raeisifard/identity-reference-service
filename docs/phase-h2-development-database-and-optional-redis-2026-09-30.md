# H2 Development Database and Optional Redis — Implementation Report

**Date:** 2026-09-30  
**Branch:** `feature/h2-file-db-optional-redis`  
**Prompt:** `prompts/H2-DEVELOPMENT-DATABASE-AND-OPTIONAL-REDIS-PLAN.md`

## Implemented

- Added a file-backed H2 `dev` profile for local development.
- H2 database files survive application restarts; no `create-drop` or automatic schema recreation is used.
- Added H2-specific Flyway migrations equivalent to the current Oracle schema.
- JPA schema validation is enabled for local H2 and tests.
- Kept the existing Oracle `local`/`oracle` connection settings and Oracle migrations intact.
- Redis is optional through `IDENTITY_REDIS_ENABLED`.
- When Redis is disabled, the application reads and writes the active database directly.
- Removed the Caffeine dependency and local L1 cache implementation entirely.
- Redis remains an acceleration layer and is not required for durable correctness.
- Updated documentation for H2 persistence, Redis behavior, and the no-local-cache policy.

## Verification

Maven verification was **not run in this environment** because Maven is not installed in the execution environment. The branch is intentionally provided for your local verification before merging into `main`.

Recommended checks:

```text
mvn clean verify
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

For H2 with Redis disabled:

```text
IDENTITY_REDIS_ENABLED=false
```

For H2 with Redis enabled:

```text
IDENTITY_REDIS_ENABLED=true
```

The first development startup should create the H2 database files under `./data/`; subsequent startups reuse the same database. To reset development data, stop the service and remove the H2 database files under that directory.
