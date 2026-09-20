package com.isc.identityreference.persistence.oracle;

import com.isc.identityreference.application.IdentityReferenceStore;
import com.isc.identityreference.application.LookupKeyFingerprint;
import com.isc.identityreference.domain.freshness.Freshness;
import com.isc.identityreference.domain.identity.*;
import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState;
import com.isc.identityreference.domain.provider.*;
import com.isc.identityreference.persistence.oracle.entity.IdentityReferenceEntity;
import com.isc.identityreference.persistence.oracle.repository.IdentityReferenceRepository;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class OracleIdentityReferenceStore implements IdentityReferenceStore {
 private final IdentityReferenceRepository repository; public OracleIdentityReferenceStore(IdentityReferenceRepository repository){this.repository=repository;}
 public Optional<IdentityReference> find(IdentityLookupKey key){return repository.findByLookupKeyHash(LookupKeyFingerprint.of(key)).map(OracleIdentityReferenceStore::toDomain);}
 public List<IdentityReference> findDue(Instant now,int limit){return repository.findByNextRefreshAtLessThanEqualOrderByNextRefreshAtAsc(now,org.springframework.data.domain.PageRequest.of(0,limit)).stream().map(OracleIdentityReferenceStore::toDomain).toList();}
 public IdentityReference save(IdentityReference r){
  var e=repository.findByLookupKeyHash(LookupKeyFingerprint.of(r.lookupKey())).orElseGet(IdentityReferenceEntity::new);
  e.setId(r.id().value().toString()); e.setLookupKeyHash(LookupKeyFingerprint.of(r.lookupKey())); e.setGivenName(r.attributes().givenName()); e.setFamilyName(r.attributes().familyName()); e.setFatherName(r.attributes().fatherName()); e.setBirthDate(r.attributes().birthDate()); e.setGender(r.attributes().gender()); e.setNationalIdCiphertext(r.attributes().nationalId().getBytes(StandardCharsets.UTF_8));
  e.setLifecycleState(r.lifecycleState().name()); e.setAcquiredAt(r.freshness().acquiredAt()); e.setFreshUntil(r.freshness().freshUntil()); e.setStaleUntil(r.freshness().staleUntil()); e.setNextRefreshAt(r.freshness().freshUntil().minus(Duration.ofHours(6)));
  if(!r.providerRecords().isEmpty()){var p=r.providerRecords().getFirst();e.setProviderId(p.providerId());e.setProviderRecordId(p.providerRecordId());e.setProviderAuthority(p.authority().name());}
  e.setPolicyVersion(1L); e.setRefreshStatus("SUCCESS"); e.setRefreshError(null); e.setRefreshRetryCount(0); e.setCreatedAt(r.createdAt()); e.setUpdatedAt(r.updatedAt()); repository.save(e); return r;
 }
 private static IdentityReference toDomain(IdentityReferenceEntity e){
  String nid=e.getNationalIdCiphertext()==null?"REDACTED":new String(e.getNationalIdCiphertext(),StandardCharsets.UTF_8);
  IdentityAttributes a=new IdentityAttributes(e.getGivenName(),e.getFamilyName(),e.getFatherName(),e.getBirthDate(),e.getGender(),nid);
  Freshness f=new Freshness(e.getAcquiredAt(),e.getFreshUntil(),e.getStaleUntil());
  List<ProviderRecord> records=e.getProviderId()==null?List.of():List.of(new ProviderRecord(e.getProviderId(),e.getProviderRecordId(),ProviderAuthority.valueOf(e.getProviderAuthority()),ProviderRecordState.CURRENT,a,f,e.getAcquiredAt()));
  return new IdentityReference(IdentityReferenceId.of(java.util.UUID.fromString(e.getId())),new IdentityLookupKey(nid,e.getBirthDate()),a,records,List.of(),IdentityLifecycleState.valueOf(e.getLifecycleState()),f,e.getCreatedAt(),e.getUpdatedAt());
 }
}