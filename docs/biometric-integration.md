# Biometric integration

The service acquires and stores optional reference embeddings; it does not perform face verification.

## Embedding contract

Every stored reference carries model ID, model version, dimension, metric, normalization flag, source-photo version, lifecycle state, creation time and the embedding vector.

Compatibility is exact across model ID, model version, dimension, metric and normalization. Dimension alone is never sufficient.

## Phase 10 implementation

EmbeddingService is an application boundary. The milestone includes a deterministic MockEmbeddingService for development and tests. The mock never represents production biometric quality.

BiometricIngestionService is policy-gated: when embedding is disabled, no embedding service is invoked. When enabled, the generated embedding must match the configured model ID, model version, dimension, metric and normalization contract before persistence.

Oracle persistence stores the vector as a binary float array in identity_biometric_reference. The table is linked to the stable identity reference ID and uniquely identifies a model/version/source-photo combination.

No API endpoint or real provider integration is introduced by this milestone; those belong to later milestones.
