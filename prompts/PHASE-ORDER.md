# Canonical phase order

This file is the single source of truth for milestone numbering. Prompt filenames and phase reports use the same phase number.

| Phase | Milestone | Status |
|---:|---|---|
| 01 | Foundation and repository skeleton | DONE |
| 02 | Domain model and lifecycle states | DONE |
| 03 | Provider SPI and normalized provider contract | DONE |
| 04 | Provider policy engine | DONE |
| 05 | Oracle durable persistence | DONE |
| 06 | Redis L2 cache | DONE |
| 07 | Caffeine L1 cache | DONE |
| 08 | Acquisition and refresh pipeline | DONE |
| 09 | Policy-driven scheduler and quiet-window refresh | DONE |
| 10 | Biometric embedding ingestion | DONE |
| 11 | Provider adapter framework and first mock provider | DONE |
| 12 | Secure lookup and administrative APIs | DONE |
| 13 | Observability and operational controls | DONE |
| 14 | Refresh and synchronization engine | DONE |
| 15 | 8-million-record scale and performance engineering | DONE |
| 16 | Failure handling, idempotency, locking and recovery | TODO |
| 17 | Data governance, retention and audit | TODO |
| 18 | Integration contract with face-biometric-service | TODO |
| 19 | Automated tests and end-to-end validation | TODO |
| 20 | Production hardening and security review | TODO |
| 21 | Data protection and cryptographic protection | TODO |
| 22 | Final release gate and architecture audit | TODO |

## Numbering rules

- Phase numbers are canonical; historical duplicate/misnumbered prompt filenames are removed rather than retained.
- Phase 14 is refresh/synchronization.
- Phase 15 is scale/performance.
- Phase 16 is failure/idempotency/locking/recovery.
- Phase 17 is governance/retention/audit.
- Phase 18 is face-biometric-service integration.
- Phase 19 is automated/E2E validation.
- Phase 20 is production hardening/security review.
- Phase 21 is intentionally late: data protection and cryptographic implementation is deferred until the complete functional architecture and workflows are understood.
- Phase 22 is the final release gate after Phase 21.

## Data-protection rule

The current `national_id_ciphertext` field must not be treated as actual encryption until Phase 21 is implemented and verified. Earlier phases must preserve functional behavior without making premature cryptographic assumptions.

## Documentation rule

Phase reports in `docs/` should use the same two-digit phase prefix as their corresponding prompt. Where one historical report covers multiple phases, the report index must identify the relationship explicitly rather than inventing a phase completion claim.

Run one milestone at a time.
