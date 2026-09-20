# Phase 21 — Data protection and cryptographic protection

## Objective

Finalize and implement the production data-protection design only after the complete functional system is available.

This phase is intentionally late. Earlier phases must not prematurely lock the project into encryption, hashing, deterministic encryption, or a particular database-security model before the actual completed data flows are understood.

## Required analysis before implementation

Review the completed system end to end:

- Oracle persistence and query patterns
- Redis L2 and Caffeine L1 data
- provider lookup and provider refresh flows
- scheduled and targeted refresh
- biometric photo and embedding ingestion
- face-biometric-service integration
- administrative APIs
- audit and operational data
- application logs and error handling
- backups and recovery
- configuration and secret management

Classify every sensitive field according to what the application actually needs to do with it.

At minimum evaluate:

- national ID
- birth date
- name and other personal attributes
- address/contact information if present
- provider identifiers
- face photographs
- face embeddings
- audit records
- cached representations
- backup copies
- provider credentials and other secrets

## National ID decision

Do not assume that either hashing or encryption alone is automatically correct.

Determine whether the completed system needs to:

1. recover the original national ID for an external provider call;
2. perform exact database lookup by national ID;
3. correlate records without recovering the ID;
4. prevent equality leakage;
5. support key rotation and migration.

If recoverable provider access is required, use authenticated encryption or an equivalent approved design.

If efficient exact lookup is required while preserving confidentiality, evaluate a keyed lookup token/HMAC in addition to ciphertext.

Do not use unsalted plain SHA-256 as the protected lookup mechanism for a constrained identifier such as national ID.

Do not use deterministic encryption merely to avoid a separate lookup token without explicitly accepting and documenting its equality leakage.

The final schema must be based on the actual completed workflows.

## Cryptographic key management

Use the project's approved secret-management mechanism (Vault or the final selected equivalent).

Define:

- encryption keys
- lookup/HMAC keys where required
- key identifiers/versioning
- rotation procedure
- old-key decryption compatibility
- re-encryption migration
- failure behavior when a key is unavailable
- access permissions
- emergency/key-compromise procedure

No long-lived cryptographic master key should be hard-coded in source code or ordinary application configuration.

## Biometric data

Define protection for:

- face photographs
- face embeddings
- model identifiers/version metadata
- cached biometric material
- backup copies

Preserve the model-aware embedding contract. Encryption must not remove the metadata required to interpret an embedding.

## Cache and operational protection

Review whether sensitive values appear in:

- Redis
- Caffeine
- request/response payloads
- logs
- metrics
- tracing
- exception messages
- admin responses
- database backups

Apply masking, minimization, TTL, access control, and encryption where appropriate.

## Migration

The current implementation may contain compatibility data in fields such as `national_id_ciphertext`. Treat that data as unencrypted legacy/temporary data.

Design an explicit migration:

1. identify existing records;
2. protect them with the final scheme;
3. verify migration;
4. remove or invalidate temporary plaintext representations;
5. verify indexes and lookup behavior;
6. support rollback/recovery safely.

Do not claim that the current field is encrypted before this migration is complete.

## Verification

Add tests for:

- encrypt/decrypt round trips
- lookup-token determinism and key separation where applicable
- wrong-key failures
- key-version rotation
- migration
- no sensitive values in logs
- cache behavior
- backup/restore behavior
- API masking
- unauthorized access
- biometric data protection

Run the complete Maven verification suite and relevant integration tests.

## Deliverables

- final sensitive-data classification
- final cryptographic design
- schema/index changes
- Vault/key-management integration
- migration tooling
- security-focused tests
- updated documentation
- operational runbook
- explicit statement of which fields are encrypted, keyed, hashed, masked, or intentionally left non-sensitive

## Exit criteria

Phase 21 is complete only when the final data-protection design is implemented, migrated, tested, documented, and consistent across Oracle, Redis, application memory, APIs, logs, backups, and biometric integration.

Do not move cryptographic implementation into an earlier phase merely to satisfy this prompt.
