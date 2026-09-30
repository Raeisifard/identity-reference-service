# identity-reference-service

Policy-driven acquisition, normalization, caching, and reference-data service for identity information from multiple authoritative or semi-authoritative providers.

Architecture: Redis (optional) -> active database (H2 file in local development, Oracle in production) durable store. MongoDB is optional for raw provider/document payloads.

The target population is approximately 8 million identities. Capacity must be measured rather than assumed. Provider-specific policies control validation, authority, overwrite/conflict rules, TTL, stale grace, refresh windows, jitter, quotas, retry and embedding behavior.

Refresh is driven by nextRefreshAt and distributed across configurable quiet windows (for example midnight-to-dawn); the scheduler must use jitter and provider quotas rather than creating a synchronized midnight storm.

This service manages reference-data and optional reference-embedding lifecycle. face-biometric-service remains responsible for biometric verification. Every embedding carries model ID, model version, dimension, metric/normalization and source-photo version.

National identity attributes, photos and biometric vectors are highly sensitive. Production requires encryption, Vault-backed secrets, service authorization, auditability, retention/deletion controls, anti-enumeration/rate limiting and strict sensitive-data logging rules.

Real national-agency endpoints and credentials are intentionally not invented. The first external adapter is a mock until contractual API documentation is supplied.

See docs/ for architecture and prompts/ for the sequential AI implementation agenda.

## H2 development database

The `dev` profile uses a persistent file-backed H2 database. It does not use `create-drop` or a local in-memory cache.

```text
./data/identity-reference.mv.db
```

Override the location with `IDENTITY_H2_PATH`. Flyway applies the H2-specific schema migrations and JPA runs with `ddl-auto=validate`.

Redis is optional. Set `IDENTITY_REDIS_ENABLED=true` when Redis should be used as an acceleration layer. When it is `false`, lookups go directly to the active database. There is no Caffeine or other local in-memory cache fallback.

The `local` and `oracle` profiles continue to use Oracle with the existing Oracle connection properties and Oracle migrations.

### Development commands

```text
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

For Redis disabled (default):

```text
IDENTITY_REDIS_ENABLED=false
```

For Redis enabled:

```text
IDENTITY_REDIS_ENABLED=true
```
