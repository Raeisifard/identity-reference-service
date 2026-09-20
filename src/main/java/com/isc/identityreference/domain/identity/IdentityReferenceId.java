package com.isc.identityreference.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record IdentityReferenceId(UUID value){
 public IdentityReferenceId{Objects.requireNonNull(value,"value");}
 public static IdentityReferenceId newId(){return new IdentityReferenceId(UUID.randomUUID());}
 public static IdentityReferenceId of(UUID value){return new IdentityReferenceId(value);}
}