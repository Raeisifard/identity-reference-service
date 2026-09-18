# Biometric integration

The service acquires and stores reference embeddings; it does not perform face verification.

An embedding reference must contain modelId, modelVersion, dimension, metric, vector, sourcePhotoVersion and creation time.

face-biometric-service must reject incompatible model/version/metric combinations. Dimension alone is not sufficient compatibility evidence.