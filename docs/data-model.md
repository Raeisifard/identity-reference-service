# Phase 02 — Domain model and lifecycle states

**Status: DONE**

Suggested durable entities:
- identity_reference
- identity_provider_record
- identity_biometric_reference
- provider_policy
- refresh_job
- refresh_attempt
- identity_audit_event

The domain model introduced in Phase 02 remains persistence-agnostic:
- IdentityReferenceId is the stable internal identifier.
- IdentityLookupKey contains protected lookup material and is never a display/logging DTO.
- IdentityAttributes contains normalized identity attributes.
- ProviderRecord identifies a provider record by provider ID + provider record ID and carries provider authority, state and freshness.
- BiometricReference is model-aware: model ID/version, dimension, metric, normalization, source-photo version and lifecycle state are part of the reference.

Use a stable internal reference ID, not a national ID, as the relational primary key.

## Lifecycle

Identity references use explicit states:
- NEW — created but not yet successfully acquired.
- ACTIVE — usable reference data is current.
- REFRESHING — an acquisition/refresh is in progress.
- STALE — data is outside its fresh window but may remain usable during stale grace.
- FAILED — the latest acquisition attempt failed; recovery may return to refresh/active.
- RETIRED — terminal state; no further lifecycle transitions are allowed.

## Freshness

Freshness is represented by acquiredAt, freshUntil and staleUntil. Given an explicit Instant, the domain classifies data as FRESH, STALE or EXPIRED.

## Biometric compatibility

Embedding compatibility is intentionally stricter than dimension matching. A reference is compatible only when model ID, model version, dimension, metric and normalization all match the requested biometric contract.
