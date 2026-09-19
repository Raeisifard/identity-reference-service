package com.isc.identityreference.biometric;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockEmbeddingServiceTest {
 @Test void producesDeterministicModelAwareNormalizedEmbedding(){
  var service=new MockEmbeddingService("mock-arcface","1",16,EmbeddingMetric.COSINE,true);
  var request=new EmbeddingRequest("ref-1","photo-v1",new byte[]{1,2,3,4});
  var first=service.generate(request).orElseThrow(); var second=service.generate(request).orElseThrow();
  assertEquals("mock-arcface",first.modelId()); assertEquals("1",first.modelVersion()); assertEquals(16,first.dimension()); assertEquals(EmbeddingMetric.COSINE,first.metric()); assertTrue(first.normalized()); assertArrayEquals(first.vector(),second.vector());
  double norm=0; for(float value:first.vector()) norm+=(double)value*value; assertEquals(1.0,Math.sqrt(norm),0.00001);
 }
}
