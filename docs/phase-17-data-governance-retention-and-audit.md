# Phase 17 — Data governance, retention and audit

## Status
DONE

## Implemented
- Added configurable governance controls for retention, retired-record grace period, audit retention, purge cadence and batch size.
- Added lifecycle retirement through the administrative API without exposing raw identity data.
- Added scheduled purge of retired identity records after the configured grace period.
- Added scheduled purge of audit events after the configured audit retention period.
- Added audit events for identity lookups, retirement and physical purge.
- Audit records store only a SHA-256 lookup-key fingerprint plus event metadata; national ID, birth date, biometric payloads and provider payloads are not written to the audit record.
- Added Oracle durable audit persistence and an in-memory audit implementation for non-Oracle operation.
- Added cache invalidation when an identity is retired or purged.
- Added store operations for governed retirement/purge and policy-version lookup.
- Preserved provider policy version traceability in Oracle identity records and lookup audit events.
- Added automated unit coverage for retirement/audit behavior and retention purge behavior.
- Corrected the remaining escaped API credential placeholders in the main YAML configuration.

## Configuration
Default governance configuration: identity retention 365 days, retired retention 30 days, audit retention 730 days, purge interval 1 hour, purge batch size 1000. Values are configurable under identity-reference.governance.

## Privacy boundary
Audit data deliberately excludes raw national ID, birth date, names, face images, embeddings, provider response bodies, credentials and other sensitive payloads. The lookup key is represented by the existing SHA-256 fingerprint.

## Tests
Added DataGovernanceServiceTest covering retirement/privacy-safe audit creation and deletion after the retired-record retention period.

Full Maven verification was not executable through the GitHub connector in this implementation session; local mvn clean verify remains the final verification step.

## Commit
The milestone was committed directly to main.
