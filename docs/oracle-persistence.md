# Phase 05 — Oracle durable persistence

**Status: DONE, with data-protection caveat**

Phase 05 introduced the Oracle persistence boundary and Flyway migration.

- identity_reference uses a stable internal ID rather than a national ID as the primary key.
- Lookup uses application lookup material rather than a national ID query column.
- A field named `national_id_ciphertext` exists for the intended protected representation, but the current compatibility implementation must NOT be described as actual encryption.
- next_refresh_at is indexed for policy-driven refresh scheduling.
- IdentityReferenceRepository exposes exact lookup and bounded due-refresh retrieval.
- Oracle persistence can be enabled through the Oracle profile.

The final lookup-token/encryption design is intentionally deferred to Phase 21. Existing compatibility data must be treated as temporary/unprotected until that phase is implemented and migrated.
