# Phase order

01 Foundation and repository skeleton
02 Domain model and lifecycle states
03 Provider SPI and normalized provider contract
04 Provider policy engine
05 Oracle durable persistence
06 Redis L2 cache
07 Caffeine L1 cache
08 Acquisition and refresh pipeline
09 Policy-driven scheduler and quiet-window refresh
10 Biometric embedding ingestion
11 Provider adapter framework and first mock provider
12 Secure lookup and administrative APIs
13 Observability and operational controls
14 Refresh and synchronization engine
15 8-million-record scale and performance engineering
16 Failure handling, idempotency, locking and recovery
17 Data governance, retention and audit
18 Integration contract with face-biometric-service
19 Automated tests and end-to-end validation
20 Production hardening and security review
21 Data protection and cryptographic protection
22 Final release gate and architecture audit

Phase 21 is intentionally late. Data-protection implementation is deferred until the functional architecture, persistence model, provider flows, refresh flows, biometric integration, caching, audit, and operational behavior are complete.

The current `national_id_ciphertext` field must not be treated as actual encryption until Phase 21 is implemented and verified. Do not introduce premature cryptographic assumptions into earlier phases.

Phase 21 must make field-by-field decisions for sensitive data rather than assuming that every field should be encrypted or hashed. In particular, determine whether national ID requires recoverable encryption, a keyed lookup token/HMAC, or both, based on the completed lookup and provider-refresh flows.

Historical prompt filenames for later milestones are retained to avoid destructive renaming; this phase order is the canonical execution order.

Run one milestone at a time.
