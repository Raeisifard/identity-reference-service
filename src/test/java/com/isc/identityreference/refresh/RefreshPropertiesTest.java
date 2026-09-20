package com.isc.identityreference.refresh;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class RefreshPropertiesTest {
 @Test void schedulerLookAheadCoversProviderRefreshLeadTime(){
  RefreshProperties properties=new RefreshProperties();
  properties.setSchedulerLookAhead(Duration.ofHours(2));
  RefreshProperties.Provider provider=new RefreshProperties.Provider();
  provider.setRefreshBeforeExpiry(Duration.ofHours(8));
  properties.getProviders().put("provider-a",provider);
  assertEquals(Duration.ofHours(8),properties.effectiveSchedulerLookAhead());
 }

 @Test void schedulerLookAheadKeepsConfiguredMinimum(){
  RefreshProperties properties=new RefreshProperties();
  properties.setSchedulerLookAhead(Duration.ofHours(6));
  RefreshProperties.Provider provider=new RefreshProperties.Provider();
  provider.setRefreshBeforeExpiry(Duration.ofHours(2));
  properties.getProviders().put("provider-a",provider);
  assertEquals(Duration.ofHours(6),properties.effectiveSchedulerLookAhead());
 }
}
