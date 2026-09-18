# Oracle durable persistence

Milestone 05 introduces the Oracle persistence boundary and a Flyway migration.

- identity_reference uses a stable internal ID rather than a national ID as the primary key.
- Lookup uses a lookup_key_hash; raw national ID is not stored as a query column.
- The national ID payload has a dedicated ciphertext BLOB column for application-level encryption.
- next_refresh_at is indexed for later policy-driven refresh scheduling.
- IdentityReferenceRepository exposes exact lookup and bounded due-refresh retrieval.
- Oracle persistence is disabled by default in the local profile. The oracle profile activates the datasource, Flyway and JPA validation.

The lookup hash must be a keyed/deterministic application-level digest in production; this milestone does not invent a secret or key-management mechanism. Schema migration is Oracle-specific.
