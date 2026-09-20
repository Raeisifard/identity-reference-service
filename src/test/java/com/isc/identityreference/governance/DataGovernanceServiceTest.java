package com.isc.identityreference.governance;

import com.isc.identityreference.application.InMemoryIdentityReferenceStore;
import com.isc.identityreference.cache.l1.CaffeineL1Cache;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DataGovernanceServiceTest {
 @Test void retireIsAuditedWithoutRawIdentityData(){
  var store=new InMemoryIdentityReferenceStore(); var l1=new CaffeineL1Cache(10); var audit=new InMemoryAuditEventStore(); var p=new GovernanceProperties();
  var now=Instant.parse("2026-09-20T10:00:00Z"); var key=new IdentityLookupKey("1234567890",LocalDate.of(1990,1,2));
  var ref=new IdentityReference(IdentityReferenceId.newId(),key,new IdentityAttributes("A","B",null,key.birthDate(),"M",key.nationalId()),List.of(),List.of(),IdentityLifecycleState.ACTIVE,new Freshness(now,now.plusSeconds(3600),now.plusSeconds(7200)),now,now);
  store.save(ref); var service=new DataGovernanceService(store,l1,null,audit,p);
  var op=UUID.randomUUID(); service.retire(key,"ADMIN","CUSTOMER_REQUEST",op,now.plusSeconds(1));
  assertEquals(IdentityLifecycleState.RETIRED,store.find(key).orElseThrow().lifecycleState());
  var event=audit.recent(1).getFirst(); assertEquals("IDENTITY_RETIRED",event.eventType()); assertEquals(com.isc.identityreference.application.LookupKeyFingerprint.of(key),event.lookupKeyHash()); assertFalse(event.lookupKeyHash().contains(key.nationalId())); assertEquals("CUSTOMER_REQUEST",event.reasonCode());
 }
 @Test void retiredRecordsArePurgedAfterRetention(){
  var store=new InMemoryIdentityReferenceStore(); var audit=new InMemoryAuditEventStore(); var p=new GovernanceProperties(); var now=Instant.parse("2026-09-20T10:00:00Z"); var key=new IdentityLookupKey("9876543210",LocalDate.of(1985,5,6));
  var ref=new IdentityReference(IdentityReferenceId.newId(),key,new IdentityAttributes("A","B",null,key.birthDate(),"F",key.nationalId()),List.of(),List.of(),IdentityLifecycleState.RETIRED,new Freshness(now.minus(Duration.ofDays(40)),now.minus(Duration.ofDays(39)),now.minus(Duration.ofDays(38))),now.minus(Duration.ofDays(40)),now.minus(Duration.ofDays(40)));
  store.save(ref); var service=new DataGovernanceService(store,new CaffeineL1Cache(10),null,audit,p);
  assertEquals(1,service.purgeRetired(now,100)); assertTrue(store.find(key).isEmpty()); assertEquals("IDENTITY_PURGED",audit.recent(1).getFirst().eventType());
 }
}
