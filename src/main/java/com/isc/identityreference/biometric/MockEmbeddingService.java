package com.isc.identityreference.biometric;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

public final class MockEmbeddingService implements EmbeddingService {
    private final String modelId; private final String modelVersion; private final int dimension;
    private final EmbeddingMetric metric; private final boolean normalized;
    public MockEmbeddingService(String modelId, String modelVersion, int dimension, EmbeddingMetric metric, boolean normalized) {
        if (modelId == null || modelId.isBlank()) throw new IllegalArgumentException("modelId must not be blank");
        if (modelVersion == null || modelVersion.isBlank()) throw new IllegalArgumentException("modelVersion must not be blank");
        if (dimension <= 0) throw new IllegalArgumentException("dimension must be positive");
        this.modelId=modelId; this.modelVersion=modelVersion; this.dimension=dimension;
        this.metric=Objects.requireNonNull(metric,"metric"); this.normalized=normalized;
    }
    @Override public Optional<EmbeddingResult> generate(EmbeddingRequest request) {
        Objects.requireNonNull(request,"request"); byte[] seed=digest(request.photoBytes()); float[] vector=new float[dimension];
        for(int i=0;i<dimension;i++){ int b=seed[i%seed.length]&0xff; vector[i]=(b/127.5f)-1.0f; }
        if(normalized){ double norm=0.0; for(float value:vector) norm+=(double)value*value; norm=Math.sqrt(norm); if(norm>0.0) for(int i=0;i<vector.length;i++) vector[i]/=(float)norm; }
        return Optional.of(new EmbeddingResult(modelId,modelVersion,dimension,metric,normalized,vector));
    }
    private static byte[] digest(byte[] bytes){ try{return MessageDigest.getInstance("SHA-256").digest(bytes);}catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 is not available",e);} }
}
