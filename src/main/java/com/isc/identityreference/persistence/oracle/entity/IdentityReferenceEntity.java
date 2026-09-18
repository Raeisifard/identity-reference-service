package com.isc.identityreference.persistence.oracle.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "identity_reference",
        indexes = {
                @Index(name = "ix_identity_lookup", columnList = "lookup_key_hash"),
                @Index(name = "ix_identity_refresh", columnList = "next_refresh_at")
        })
public class IdentityReferenceEntity {
    @Id @Column(name = "id", length = 36, nullable = false) private String id;
    @Column(name = "lookup_key_hash", length = 128, nullable = false, unique = true) private String lookupKeyHash;
    @Column(name = "given_name", length = 200, nullable = false) private String givenName;
    @Column(name = "family_name", length = 200, nullable = false) private String familyName;
    @Column(name = "father_name", length = 200) private String fatherName;
    @Column(name = "birth_date", nullable = false) private LocalDate birthDate;
    @Column(name = "gender", length = 32) private String gender;
    @Lob @Column(name = "national_id_ciphertext") private byte[] nationalIdCiphertext;
    @Column(name = "lifecycle_state", length = 32, nullable = false) private String lifecycleState;
    @Column(name = "acquired_at") private Instant acquiredAt;
    @Column(name = "fresh_until") private Instant freshUntil;
    @Column(name = "stale_until") private Instant staleUntil;
    @Column(name = "next_refresh_at") private Instant nextRefreshAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected IdentityReferenceEntity() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getLookupKeyHash(){return lookupKeyHash;} public void setLookupKeyHash(String v){lookupKeyHash=v;}
    public String getGivenName(){return givenName;} public void setGivenName(String v){givenName=v;}
    public String getFamilyName(){return familyName;} public void setFamilyName(String v){familyName=v;}
    public String getFatherName(){return fatherName;} public void setFatherName(String v){fatherName=v;}
    public LocalDate getBirthDate(){return birthDate;} public void setBirthDate(LocalDate v){birthDate=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public byte[] getNationalIdCiphertext(){return nationalIdCiphertext==null?null:nationalIdCiphertext.clone();}
    public void setNationalIdCiphertext(byte[] v){nationalIdCiphertext=v==null?null:v.clone();}
    public String getLifecycleState(){return lifecycleState;} public void setLifecycleState(String v){lifecycleState=v;}
    public Instant getAcquiredAt(){return acquiredAt;} public void setAcquiredAt(Instant v){acquiredAt=v;}
    public Instant getFreshUntil(){return freshUntil;} public void setFreshUntil(Instant v){freshUntil=v;}
    public Instant getStaleUntil(){return staleUntil;} public void setStaleUntil(Instant v){staleUntil=v;}
    public Instant getNextRefreshAt(){return nextRefreshAt;} public void setNextRefreshAt(Instant v){nextRefreshAt=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
