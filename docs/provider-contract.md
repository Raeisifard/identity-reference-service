# Phase 03 — Provider SPI and normalized provider contract

**Status: DONE**

`IdentityProvider` exposes provider descriptor metadata and a lookup operation using protected `IdentityLookupKey` material. Provider-specific transport, authentication, serialization and response translation stay inside an implementation.

`ProviderLookupResult` has explicit FOUND, NOT_FOUND and ERROR outcomes. FOUND carries normalized `IdentityAttributes`; NOT_FOUND carries no attributes; ERROR carries a structured `ProviderError`.

`ProviderError` separates a stable code, retryable flag and safe message. Safe messages must not contain national IDs, birth dates, photos, biometric vectors, raw provider payloads or secrets.

No real endpoint or credential is introduced by this phase.
