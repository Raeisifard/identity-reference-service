package com.isc.identityreference.application;

import com.isc.identityreference.biometric.*;
import com.isc.identityreference.domain.biometric.BiometricReference;
import com.isc.identityreference.domain.biometric.BiometricReferenceState;
import com.isc.identityreference.policy.ProviderPolicy;
import java.time.Instant;
import java.util.Objects;

public final class BiometricIngestionService {
    private final EmbeddingService embeddingService; private final BiometricReferenceStore store;
    public BiometricIngestionService(EmbeddingService embeddingService, BiometricReferenceStore store){this.embeddingService=Objects.requireNonNull(embeddingService,"embeddingService");this.store=Objects.requireNonNull(store,"store");}
    public Result ingest(String identityReferenceId, byte[] photoBytes, String sourcePhotoVersion, ProviderPolicy.EmbeddingPolicy policy, Instant now){
        Objects.requireNonNull(identityReferenceId,"identityReferenceId"); Objects.requireNonNull(now,"now"); Objects.requireNonNull(policy,"policy");
        if(!policy.enabled()) return Result.DISABLED;
        EmbeddingResult result=embeddingService.generate(new EmbeddingRequest(identityReferenceId,sourcePhotoVersion,photoBytes)).orElse(null);
        if(result==null) return Result.NOT_GENERATED;
        validatePolicy(result,policy);
        BiometricReference reference=new BiometricReference(result.modelId(),result.modelVersion(),result.dimension(),result.metric(),result.normalized(),sourcePhotoVersion,BiometricReferenceState.ACTIVE,result.vector(),now);
        store.save(identityReferenceId,reference); return Result.STORED;
    }
    private static void validatePolicy(EmbeddingResult result, ProviderPolicy.EmbeddingPolicy policy){
        if(!policy.modelId().equals(result.modelId())||!policy.modelVersion().equals(result.modelVersion())||policy.dimension()!=result.dimension()) throw new IllegalArgumentException("embedding result does not satisfy configured model contract");
        if(policy.metric()!=result.metric()||policy.normalized()!=result.normalized()) throw new IllegalArgumentException("embedding result does not satisfy configured metric/normalization contract");
    }
    public enum Result { DISABLED, NOT_GENERATED, STORED }
}
