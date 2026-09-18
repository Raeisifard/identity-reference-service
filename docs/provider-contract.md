# Provider SPI and normalized contract

Milestone 03 defines the provider boundary without binding the service to an external protocol.

## SPI
`IdentityProvider` exposes provider descriptor metadata and a lookup operation using protected `IdentityLookupKey` material. Provider-specific transport, authentication, serialization and response translation stay inside an implementation.

## Normalized result
`ProviderLookupResult` has explicit FOUND, NOT_FOUND and ERROR outcomes. FOUND carries normalized `IdentityAttributes`; NOT_FOUND carries no attributes; ERROR carries a structured `ProviderError`. Results also carry provider identity, provider record identity, authority and retrieval time.

## Errors
`ProviderError` separates a stable code, retryable flag and safe message. Safe messages must not contain national IDs, birth dates, photos, biometric vectors, raw provider payloads or secrets.

## Scope
No real endpoint, credential, retry policy, circuit breaker, provider policy engine or production provider adapter is introduced here. The mock implementation exists only inside tests.
