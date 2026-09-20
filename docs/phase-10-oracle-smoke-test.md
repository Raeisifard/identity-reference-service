# Phase 10 — Oracle biometric smoke test

**Status: DONE / OPT-IN**

This test verifies the Phase 10 biometric ingestion path against a real Oracle database.

It is tagged `oracle` and enabled only when the JVM system property `identity.oracle.smoke=true` is supplied. Normal `mvn clean verify` therefore does not require a live Oracle instance.

It verifies parent identity persistence, deterministic mock embedding generation, model-contract enforcement, biometric persistence, metadata round-trip and vector dimension/normalization.

This is a test report for Phase 10, not a separate project phase.
