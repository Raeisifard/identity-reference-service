# Development H2 Database and Environment-Aware Optional Redis Plan

Use `prompts/00-ai-agent-rules.md` first.

## Purpose

Upgrade `identity-reference-service` so it can run in a development environment using H2 instead of requiring an Oracle instance, while retaining Oracle as the production database and allowing Redis to be enabled or disabled through configuration according to the infrastructure available in each environment.

This is a planning/implementation prompt only. Do not implement it unless explicitly requested by the user.

The intended deployment modes are:

| Environment/mode | Durable database | Redis cache |
|---|---|---|
| Development | H2 | Optional; enabled only when configured and available |
| Production | Oracle | Optional; enabled only when configured and available |
| Automated tests | Isolated H2 database unless a test explicitly requires Oracle | Disabled by default; enable only in dedicated Redis integration tests |

The database remains the source of truth. Redis is only an optimization. When Redis is disabled, every cache-backed lookup must reach the active database directly (H2 in development or Oracle in production), with no hidden in-memory cache.

## Important compatibility clarification

H2 is not Oracle and cannot completely reproduce every Oracle behavior. H2's Oracle compatibility mode is a useful aid, not proof of full compatibility.

The implementation must:
- identify Oracle-specific SQL, data types, functions, constraints, indexes, sequences, LOB handling, and Flyway migration syntax;
- make the application's required development behavior work on H2 without weakening or silently changing production Oracle behavior;
- use portable SQL/schema definitions where practical;
- use separate database-specific Flyway migration locations only where genuinely necessary, keeping the logical schema and migration versions aligned;
- document any behavior that H2 cannot faithfully reproduce;
- retain Oracle smoke/integration tests for Oracle-specific behavior.

Do not claim that H2 fully emulates Oracle. Do not replace Oracle migrations with H2-only migrations or modify the production schema merely to make H2 start.

## 1. Inspect before editing

Before making changes, inspect the current `main` branch, including:
- `pom.xml` and dependency scopes;
- all application property files and active profile defaults;
- current Oracle persistence configuration, entities, repositories, and adapters;
- all Flyway migrations and their use of Oracle-specific syntax;
- Redis configuration, cache decorators/adapters, Redis repositories, health indicators, and cache invalidation paths;
- any remaining Caffeine dependencies, beans, references, or configuration;
- integration and smoke tests;
- the current Phase 18.5 prompt and related documentation.

Treat the repository as the source of truth. Do not assume class names or architecture from an earlier plan remain unchanged.

## 2. Profile-based database selection

Provide a clear profile-based configuration:

### Development profile

- H2 is the selected durable database.
- The application must start and perform core operations without Oracle installed or reachable.
- H2 configuration must be isolated from Oracle connection settings.
- Choose and document a sensible development database lifecycle. Prefer a file-backed H2 database if developers need fixture data to survive restarts; keep automated tests isolated and deterministic, typically using an in-memory database.
- Configure the H2 console only if useful, and ensure it is disabled outside explicitly permitted development use.
- Ensure H2 is not selected accidentally by production/pro profiles.

### Production profile

- Oracle remains the selected durable database.
- Preserve the existing Oracle URL, username, password placeholders, driver, and environment-specific settings.
- Do not replace real infrastructure values with localhost or example defaults.
- Startup must fail clearly if production is configured without a valid Oracle datasource; it must not silently fall back to H2.

### Common application behavior

- Keep domain/application services independent of the selected database.
- Use a clear persistence adapter/configuration boundary so exactly one durable database implementation is active.
- Avoid duplicate Spring beans when switching profiles.
- Do not create parallel identity, photo, biometric, or audit data models solely for H2.
- H2 and Oracle must implement the same required persistence contracts.

The exact profile names may follow the repository's existing conventions, but document the resulting run commands and profile behavior.

## 3. H2 schema and Flyway strategy

The development H2 database must support the application's actual required data model, including all persistence features currently implemented and those already included in the accepted Phase 18.5 design where applicable.

At minimum inspect and account for:
- identity reference records and lifecycle/refresh state;
- provider/source metadata;
- audit events;
- biometric reference/embedding persistence;
- photo/version data if present in the implemented schema;
- foreign keys, uniqueness constraints, and lookup indexes;
- date/time, UUID, binary/LOB, and numeric representations.

Requirements:
- Prefer shared, portable migrations when they behave correctly on both databases.
- Where Oracle-specific syntax prevents a shared migration, maintain clearly separated Flyway locations for H2 and Oracle, with equivalent logical schema and aligned version history.
- Do not use Hibernate `ddl-auto=create` or `update` as a substitute for migrations.
- Keep Hibernate schema validation enabled where compatible with the selected profile.
- Ensure startup migration behavior is deterministic and explain how a developer resets the local H2 database.
- Preserve all existing Oracle migration files and production data.
- Never introduce destructive production migrations as part of this change.

If schema parity cannot be achieved for a specific Oracle feature, document the limitation and add an Oracle-specific test rather than hiding the difference.

## 4. Redis is an optional cache in both environments

Redis enablement must be an explicit configuration decision for each deployment, not inferred from whether a Redis library is on the classpath.

Use the established property naming if one exists; otherwise use a clear property such as:

```yaml
identity-reference:
  cache:
    redis:
      enabled: false
```

Expected behavior when Redis is disabled:
- application startup does not require Redis;
- core operations do not attempt to connect to Redis;
- every cache-backed lookup goes directly to the selected durable database;
- Redis connection/client/template/cache beans and Redis-specific repositories are absent or safely conditional;
- no Caffeine or substitute local cache is silently used;
- Redis health checks/metrics do not mark the application unhealthy when Redis is intentionally disabled;
- persistence, lookup, refresh, retirement, audit, and biometric reference operations continue to work through the database.

Expected behavior when Redis is enabled:
- the selected database remains authoritative;
- cache misses fall back to the selected database;
- successful writes and lifecycle changes update or invalidate cache entries correctly;
- Redis failure must never cause loss or corruption of durable data;
- cached identity data must obey existing privacy and retention requirements;
- avoid caching historical photos or embeddings unnecessarily.

### Redis availability and failure policy

Environment configuration should enable Redis only where a Redis instance is intended to be used. Do not attempt to detect infrastructure availability by silently changing the configured mode in an undocumented way.

When Redis is configured as enabled but is unreachable:
- implement and document a deliberate policy;
- prefer a safe cache bypass/fail-open path for ordinary reads and writes, using the database as source of truth, provided this can be done without violating correctness;
- expose Redis as unavailable/degraded in operational status when enabled but unreachable;
- avoid repeated connection attempts or noisy logs on every request;
- do not report cache hits when Redis was bypassed;
- if any operation genuinely requires Redis for correctness (for example, a distributed lease), do not silently bypass it. Identify that operation and define an explicit, safe failure behavior.

In particular, distinguish the optional read/write cache from any Redis-backed distributed coordination mechanism. If the current project uses Redis for leases/locks, inspect that dependency separately; the cache toggle must not accidentally disable required correctness controls or cause unsafe multi-instance behavior. Provide separate configuration or an explicit documented constraint if necessary.

## 5. No mandatory Caffeine or hidden local cache

The desired architecture is:

- H2 -> optional Redis in development;
- Oracle -> optional Redis in production.

When Redis is off, requests go directly to the selected database.

Do not add Caffeine back as a fallback. Inspect existing L1 cache code and remove or disable it as required by the accepted Phase 18.5 direction. Remove the Caffeine runtime dependency if no remaining supported feature needs it. Do not remove unrelated behavior without tracing its use and tests.

If the project still has an explicitly configurable local cache, it must not be silently activated when Redis is disabled. The default and documented behavior for this plan is no local cache.

## 6. Configuration and developer experience

Provide clear, separate configuration for:
- common application settings;
- development/H2 datasource and Flyway settings;
- production/Oracle datasource and Flyway settings;
- Redis enablement and connection properties for each environment.

Requirements:
- Keep secrets in environment variables or the project's established secret mechanism.
- Do not commit new real credentials.
- Do not overwrite existing `application-local.yml` Oracle/Redis values or other user-provided infrastructure settings.
- If current `local` means Oracle-backed, do not silently redefine it without documenting the migration. Prefer introducing an explicit `dev` profile for H2 and retaining the existing Oracle-backed profile for users who need it.
- Provide exact run examples for H2 development, Oracle-backed operation, Redis off, and Redis on.
- Make it easy to run locally without Redis or Oracle when using the H2 development profile.
- Document whether H2 data is persistent across restarts and how to clear it safely.

Example commands may be adjusted to match the final profile names:
- development: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Oracle-backed: `mvn spring-boot:run -Dspring-boot.run.profiles=local`

## 7. Existing features must continue to work

Verify that database selection and optional Redis do not break:
- identity lookup and not-found behavior;
- provider acquisition and provider registry;
- refresh, scheduled refresh, retry and recovery behavior;
- lease/locking semantics;
- retirement, purge, and audit;
- biometric ingestion and model-aware reference lookup;
- admin APIs and configuration guards;
- actuator health and metrics.

If Redis currently backs a distributed lock/lease, do not treat it as an ordinary cache entry. Preserve safe behavior in multi-instance deployments and ensure the development H2 profile does not accidentally imply that H2 is suitable for distributed production coordination.

Do not expand this work into Admin Console or test-identity fixture implementation. Those remain separate plans.

## 8. Testing requirements

Add tests proving the intended modes rather than relying only on successful compilation.

### H2 development tests
- application context starts using the development profile with no Oracle or Redis available;
- Flyway creates the required schema;
- Hibernate validation succeeds;
- core persistence and lookup work against H2;
- identity lifecycle, refresh state, audit, and biometric persistence work as applicable;
- H2 data behavior is deterministic and the configured persistence lifecycle is verified.

### Redis-disabled tests
- context starts without Redis;
- no Redis connection is attempted by core paths;
- lookup reaches the selected database directly;
- write, update, retire, purge, and cache invalidation behavior remain correct;
- no local cache silently serves results;
- Redis health is not a failure when intentionally disabled.

### Redis-enabled tests
- cache hit and miss behavior;
- cache miss reads from the selected database;
- write/update/retirement invalidation;
- Redis unavailable behavior follows the documented policy;
- database data remains authoritative.

Use test doubles or an available ephemeral Redis integration setup for Redis-specific tests. Do not make the default Maven test suite depend on a developer's external Redis instance.

### Oracle verification
- retain and run Oracle smoke tests when explicitly enabled;
- do not claim Oracle compatibility from H2 tests alone;
- document any Oracle verification that could not be run in the current environment.

Run Maven verification and fix regressions introduced by this change.

## 9. Documentation

Update the relevant README/configuration documentation and add a focused implementation report when implementation is later authorized.

Document:
- supported profiles and database selection;
- H2's purpose and its Oracle compatibility limitations;
- H2 persistence/reset behavior;
- Flyway migration strategy;
- Redis enable/disable behavior;
- Redis enabled-but-unavailable behavior;
- distinction between cache and distributed locks/leases;
- no-Caffeine/no-hidden-local-cache behavior;
- exact local run commands;
- tests run and any environment-dependent verification not performed.

Do not update canonical phase status or renumber phases as part of this standalone infrastructure plan unless the user explicitly assigns it to a phase. Coordinate the implementation with Phase 18.5, which already defines optional Redis and the no-Caffeine direction; avoid duplicating or contradicting it.

## Non-goals

Do not:
- replace Oracle in production;
- claim H2 is a complete Oracle emulator;
- use H2 in production by default;
- make Redis mandatory;
- silently fall back to Caffeine or another local cache;
- silently disable Redis-backed distributed locking without a safe replacement/policy;
- change business behavior merely to make H2 tests pass;
- overwrite real Oracle/Redis configuration values;
- implement the Admin Console or development/test identity fixture plan;
- implement unrelated phases automatically.

## Acceptance criteria

This upgrade is complete only when:
- [ ] A documented development profile runs with H2 and no Oracle dependency.
- [ ] Production remains Oracle-backed and cannot silently fall back to H2.
- [ ] H2 schema/migrations cover the application's required development workflows.
- [ ] Oracle-specific differences are identified and documented; H2 is not claimed to be fully equivalent.
- [ ] Exactly one database persistence implementation is active per profile.
- [ ] Redis can be enabled or disabled through configuration in development and production.
- [ ] With Redis disabled, cache-backed requests go directly to H2/Oracle.
- [ ] With Redis disabled, startup and core operations do not require or connect to Redis.
- [ ] With Redis enabled, the database remains the source of truth.
- [ ] Redis outage behavior is explicit, safe, observable, and tested.
- [ ] Redis-backed leases/locks, if present, are handled separately from cache behavior.
- [ ] No Caffeine or hidden local cache is required or silently activated.
- [ ] Existing Oracle/Redis user configuration is preserved.
- [ ] Tests cover H2, Redis-disabled, and Redis-enabled behavior as applicable.
- [ ] Oracle smoke-test support is retained and limitations are reported honestly.
- [ ] Documentation is updated.
- [ ] Maven verification passes.
- [ ] Changes are committed with a focused commit message.

## Handoff

When the user explicitly authorizes implementation:
1. Re-read this prompt and `prompts/00-ai-agent-rules.md`.
2. Inspect the current `main` branch and all relevant code/configuration before editing.
3. Implement only this infrastructure upgrade and minimal prerequisites.
4. Preserve real local infrastructure settings.
5. Run tests and report exactly which H2/Redis/Oracle modes were verified.
6. Report changed files, configuration examples, migration strategy, known compatibility limits, and commit hash.
7. Do not start the Admin Console test-data plan or another phase automatically.
