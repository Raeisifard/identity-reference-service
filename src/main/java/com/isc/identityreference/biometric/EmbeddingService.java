package com.isc.identityreference.biometric;

import java.util.Optional;

public interface EmbeddingService {
    Optional<EmbeddingResult> generate(EmbeddingRequest request);
}
