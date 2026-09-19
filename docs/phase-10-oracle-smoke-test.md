# Phase 10 Oracle smoke test

This test verifies the Phase 10 biometric ingestion path against a real Oracle database.

It deliberately does not expose a runtime API. It exercises the application and persistence layers directly:

```
Spring Boot test context
        |
        +-- Flyway V1/V2
        |
        +-- IdentityReferenceRepository
        |
        +-- BiometricIngestionService
        |       |
        |       +-- MockEmbeddingService
        |       |
        |       +-- OracleBiometricReferenceStore
        |
        +-- BiometricReferenceRepository
        |
        +-- Oracle
```

## Why it is opt-in

The test is tagged `oracle` and is enabled only when the JVM system property
`identity.oracle.smoke=true` is supplied. Normal `mvn clean verify` therefore
does not require a live Oracle instance.

## Run

Use the Oracle profile and the same Oracle connection settings configured for the
application. The Oracle profile reads:

- `IDENTITY_ORACLE_URL`
- `IDENTITY_ORACLE_USERNAME`
- `IDENTITY_ORACLE_PASSWORD`

Then run:

```text
mvn -Didentity.oracle.smoke=true -Dtest=OracleBiometricIngestionSmokeTest test
```

If the environment variables are not supplied, Spring uses the defaults defined
in `application-oracle.yml`.

## What the test verifies

1. A parent `identity_reference` row can be persisted.
2. Phase 10 generates a deterministic mock embedding.
3. The configured model contract is enforced.
4. The biometric reference is persisted in `identity_biometric_reference`.
5. Model ID, model version, dimension, metric, normalization and source-photo
   version survive the Oracle round trip.
6. The binary vector is stored and read back with the expected dimension.
7. The returned vector remains normalized.
8. The logical biometric reference is represented by one Oracle row for the
   generated identity/model/photo combination.

The test creates a unique identity reference for each execution and runs inside
the Spring test transaction, so test data is rolled back after the test.
