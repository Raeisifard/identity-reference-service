package com.isc.identityreference.application;

import com.isc.identityreference.biometric.*;
import com.isc.identityreference.domain.biometric.*;
import com.isc.identityreference.domain.provider.ProviderAuthority;
import com.isc.identityreference.policy.ProviderPolicy;
import org.junit.jupiter.api.Test;
import java.time.*; import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BiometricIngestionServiceTest {
 @Test void disabledPolicyDoesNotGenerateOrStore(){
  var service=new BiometricIngestionService(new FailingEmbeddingService(),new RecordingStore());
  assertEquals(BiometricIngestionService.Result.DISABLED,service.ingest("ref-1",new byte[]{1},"photo-v1",embeddingPolicy(false,null,null,0,EmbeddingMetric.COSINE,true),Instant.parse("2026-01-01T00:00:00Z")));
 }
 @Test void storesOnlyWhenEmbeddingMatchesConfiguredContract(){
  var store=new RecordingStore(); var service=new BiometricIngestionService(new MockEmbeddingService("mock-arcface","1",8,EmbeddingMetric.COSINE,true),store);
  assertEquals(BiometricIngestionService.Result.STORED,service.ingest("ref-1",new byte[]{1,2},"photo-v1",embeddingPolicy(true,"mock-arcface","1",8,EmbeddingMetric.COSINE,true),Instant.parse("2026-01-01T00:00:00Z")));
  assertNotNull(store.saved); assertEquals("mock-arcface",store.saved.modelId()); assertEquals("1",store.saved.modelVersion()); assertEquals(8,store.saved.dimension()); assertEquals("photo-v1",store.saved.sourcePhotoVersion());
 }
 private static ProviderPolicy.EmbeddingPolicy embeddingPolicy(boolean enabled,String modelId,String modelVersion,int dimension,EmbeddingMetric metric,boolean normalized){return new ProviderPolicy.EmbeddingPolicy(enabled,modelId,modelVersion,dimension,metric,normalized);}
 private static final class FailingEmbeddingService implements EmbeddingService { public Optional<EmbeddingResult> generate(EmbeddingRequest request){fail("embedding service must not be called when disabled");return Optional.empty();} }
 private static final class RecordingStore implements BiometricReferenceStore { private BiometricReference saved; public Optional<BiometricReference> find(String a,String b,String c,String d){return Optional.empty();} public Optional<BiometricReference> findActiveCompatible(String a,String b,String c,int d,EmbeddingMetric e,boolean f){return Optional.empty();} public BiometricReference save(String id,BiometricReference reference){saved=reference;return reference;} }
}
