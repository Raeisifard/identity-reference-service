package com.isc.identityreference.application;

import com.isc.identityreference.domain.identity.IdentityLookupKey;
import com.isc.identityreference.domain.identity.IdentityReference;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryIdentityReferenceStore implements IdentityReferenceStore {
    private final ConcurrentMap<String,IdentityReference> values=new ConcurrentHashMap<>();
    public Optional<IdentityReference> find(IdentityLookupKey key){return Optional.ofNullable(values.get(key.nationalId()+":"+key.birthDate()));}
    public IdentityReference save(IdentityReference reference){values.put(reference.lookupKey().nationalId()+":"+reference.lookupKey().birthDate(),reference);return reference;}
    public List<IdentityReference> findDue(Instant now,int limit){return values.values().stream().filter(r->!r.freshness().freshUntil().isAfter(now)).limit(limit).toList();}
}