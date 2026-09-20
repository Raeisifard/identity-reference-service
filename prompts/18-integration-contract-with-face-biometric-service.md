# Phase 18 — Integration contract with face-biometric-service

Use prompts/00-ai-agent-rules.md first.

## Objective
Implement the service-to-service contract required for face-biometric-service to obtain an enrolled biometric reference from identity-reference-service.

## Contract source
Inspect the current face-biometric-lab repository before changing identity-reference-service. Treat its current verification contract as authoritative for:
- stable reference ID semantics;
- explicit model ID/version;
- embedding dimension;
- metric and normalization compatibility;
- missing-reference semantics.

## Acceptance criteria
- Inspect both repositories before editing.
- Expose a versioned, pull-based integration endpoint for an active biometric reference.
- Require the complete model compatibility tuple; never infer compatibility from dimension alone.
- Return the exact stored embedding metadata and vector.
- Return no retired/inactive reference.
- Support Oracle and non-Oracle development storage.
- Add focused automated tests.
- Do not add a new migration when the existing biometric-reference schema already satisfies the contract.
- Document the endpoint, response semantics and security boundary.
- Do not log or expose national ID or provider payloads.
- Commit the milestone with a focused message.

## Handoff
Report changed files, discovered face-biometric contract, tests, known limitations and commit hash. Do not start Phase 19 automatically.
