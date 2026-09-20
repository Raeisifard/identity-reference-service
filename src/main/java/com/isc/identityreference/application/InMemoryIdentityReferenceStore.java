package com.isc.identityreference.application;
import com.isc.identityreference.domain.identity.*; import com.isc.identityreference.domain.lifecycle.IdentityLifecycleState; import java.time.Instant; import java.util.*; import java.util.concurrent.*;
public final class InMemoryIdentityReferenceStore implements IdentityReferenceStore {
 private final ConcurrentMap<String,IdentityReference> values=new ConcurrentHashMap<>();
 private static String key(IdentityLookupKey k){return k.nationalId()+":"+k.birthDate();}
 public Optional<IdentityReference> find(IdentityLookupKey k){return Optional.ofNullable(values.get(key(k)));}
 public IdentityReference save(IdentityReference r){values.put(key(r.lookupKey()),r);return r;}
 public List<IdentityReference> findDue(Instant now,int limit){return values.values().stream().filter(r->!r.freshness().freshUntil().isAfter(now)).limit(limit).toList();}
 public List<IdentityReference> findRetiredBefore(Instant cutoff,int limit){return values.values().stream().filter(r->r.lifecycleState()==IdentityLifecycleState.RETIRED).filter(r->r.updatedAt().isBefore(cutoff)).limit(limit).toList();}
 public boolean delete(IdentityLookupKey k){return values.remove(key(k))!=null;}
}
