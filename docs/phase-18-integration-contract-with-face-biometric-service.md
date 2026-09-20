# Phase 18 — Integration contract with face-biometric-service

## Status
DONE

## Contract discovered in face-biometric-lab

The biometric service verifies a reference by a stable referenceId and an explicit model contract. The current contract requires model ID and version, and the biometric design treats dimension, metric and normalization as part of embedding compatibility. The server must not infer compatibility from vector dimension alone.

For the current server verification profile the relevant contract is:

- referenceId: the identity reference UUID
- modelId: arcface-512
- modelVersion: w600k-r50
- dimension: 512
- metric: COSINE
- normalized: true

The identity-reference service therefore exposes a pull API that returns an active reference only when the complete embedding contract matches.

## Endpoint

GET /api/v1/biometric/references/{identityReferenceId}

Required query parameters:

- modelId
- modelVersion
- dimension
- metric
- normalized

Example:

GET /api/v1/biometric/references/00000000-0000-0000-0000-000000000001?modelId=arcface-512&modelVersion=w600k-r50&dimension=512&metric=COSINE&normalized=true

The response contains the stable identity reference ID, exact model metadata, source-photo version, lifecycle state, creation time and the embedding vector. Only an ACTIVE reference compatible with every requested model attribute is returned.

404 Not Found means that no compatible active reference exists. This deliberately avoids converting a missing reference into a biometric NO_MATCH; the face-biometric-service contract defines missing references as an inconclusive/reference-not-found condition.

## Persistence behavior

The biometric reference store now supports lookup of the newest active compatible reference for a model profile.

- Oracle uses the existing identity_biometric_reference table and orders active references by creation time.
- Non-Oracle development uses an in-memory store.
- The Oracle configuration now exposes the biometric reference store as a Spring bean.
- No new database migration is required; the required table was introduced by Phase 10.

## Security boundary

The endpoint returns biometric vectors and is a service-to-service integration endpoint. It must not be exposed to public clients. Existing API security controls remain the deployment boundary; production deployments must enable authentication and restrict this endpoint to the face-biometric-service trust boundary. Dedicated service credentials/mTLS and stronger production hardening remain later-phase work.

## Tests

BiometricReferenceIntegrationServiceTest verifies newest-active selection and exact model-space compatibility.

Full Maven verification was not executable through the GitHub connector in this implementation session; run mvn clean verify locally.
