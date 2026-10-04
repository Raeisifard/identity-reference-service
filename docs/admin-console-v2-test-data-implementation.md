# Admin Console V2 and Test Data Implementation

Implemented the initial Admin Console V2 shell and development/test identity capability from the two agenda prompts.

## Storage
- `dev`: file-backed H2 remains durable source of truth.
- `local` / `oracle`: Oracle remains durable source of truth.
- Redis remains optional; the feature works with Redis disabled.
- No Caffeine/local in-memory cache is introduced.

## Security
Development exposes the console without authentication. Local/oracle require the existing ADMIN credentials. Test-data is server-side guarded and cannot be enabled outside development mode through normal configuration. National IDs are masked in lists and raw embeddings are not displayed.

## Test data
The implementation provides create/list/update, JPEG/PNG upload validation, webcam capture in the browser, photo replacement/versioning, retirement, audit events, normal identity lookup compatibility, and optional use of the existing `BiometricIngestionService`.

## Biometric development mode
The existing `MockEmbeddingService` is used only when the configured test-data biometric mode is enabled and no other `EmbeddingService` exists. This is deterministic development plumbing, not a second production embedding algorithm.

## Limitations
The initial console shell exposes real service status and the complete configuration-driven navigation model; sections without an existing backend operational API show their current capability/documentation state rather than fabricated metrics. Phase 21 cryptographic protection is intentionally not implemented.


## Admin Console V2 upgrade

The console was expanded beyond the initial shell so enabled navigation sections provide useful operational or administrative information instead of placeholder pages.

### Controlled test scenarios
The Development/Test Data form now uses a server-owned scenario catalog exposed by `GET /api/v1/admin/console/scenarios`. Scenario tags are validated server-side, so arbitrary labels cannot be submitted as scenario identifiers. The catalog currently covers normal identity, expiry/staleness, photo/biometric, provider failure, refresh/concurrency, lifecycle, duplicate and rate-limit test cases.

### Operational sections
The console now exposes:
- registered provider descriptors from the existing provider registry
- application health where the actuator health endpoint is available
- console capability and environment state
- governance retention configuration
- recent privacy-safe audit events
- the existing lookup, refresh and retirement APIs through controlled forms
- a controlled API Explorer with predefined operations and confirmation for destructive actions
- biometric/test-fixture status without exposing raw embedding vectors
- cache/storage semantics that explicitly distinguish durable persistence from optional Redis acceleration and confirm that the console adds no local identity cache

### Help Center
The Help Center now contains getting-started guidance, architecture, operations, development/test-data, biometric, security/privacy, API, configuration and glossary material. Every enabled section has contextual `?` help, and the test-data page provides an expanded scenario catalog explanation.

### Security boundaries
The upgrade preserves the existing development/protected profile split. Test-data remains server-side guarded and development-only, API testing remains independently feature-flagged, and destructive refresh/retire operations require explicit confirmation in the browser while still going through the normal backend authorization and governance paths.

### Known implementation boundary
Provider registration and application health are available as live data. Dedicated Redis health, refresh backlog, latency and cache-hit metrics are not claimed by the console because the current backend does not expose those specific operational measurements. The UI shows that limitation rather than inventing values.
