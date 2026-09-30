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
