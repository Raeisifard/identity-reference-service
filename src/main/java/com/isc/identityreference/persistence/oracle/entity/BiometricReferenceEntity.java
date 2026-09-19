package com.isc.identityreference.persistence.oracle.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="identity_biometric_reference",uniqueConstraints=@UniqueConstraint(name="uk_identity_bio_model_photo",columnNames={"identity_reference_id","model_id","model_version","source_photo_version"}),indexes=@Index(name="ix_identity_bio_lookup",columnList="identity_reference_id,model_id,model_version"))
public class BiometricReferenceEntity {
    @Id @Column(name="id",length=36,nullable=false) private String id;
    @Column(name="identity_reference_id",length=36,nullable=false) private String identityReferenceId;
    @Column(name="model_id",length=128,nullable=false) private String modelId;
    @Column(name="model_version",length=128,nullable=false) private String modelVersion;
    @Column(name="dimension",nullable=false) private int dimension;
    @Column(name="metric",length=32,nullable=false) private String metric;
    @Column(name="normalized",nullable=false) private boolean normalized;
    @Column(name="source_photo_version",length=128,nullable=false) private String sourcePhotoVersion;
    @Column(name="state",length=32,nullable=false) private String state;
    @Lob @Column(name="vector",nullable=false) private byte[] vector;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    protected BiometricReferenceEntity(){}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getIdentityReferenceId(){return identityReferenceId;} public void setIdentityReferenceId(String v){identityReferenceId=v;}
    public String getModelId(){return modelId;} public void setModelId(String v){modelId=v;}
    public String getModelVersion(){return modelVersion;} public void setModelVersion(String v){modelVersion=v;}
    public int getDimension(){return dimension;} public void setDimension(int v){dimension=v;}
    public String getMetric(){return metric;} public void setMetric(String v){metric=v;}
    public boolean isNormalized(){return normalized;} public void setNormalized(boolean v){normalized=v;}
    public String getSourcePhotoVersion(){return sourcePhotoVersion;} public void setSourcePhotoVersion(String v){sourcePhotoVersion=v;}
    public String getState(){return state;} public void setState(String v){state=v;}
    public byte[] getVector(){return vector==null?null:vector.clone();} public void setVector(byte[] v){vector=v==null?null:v.clone();}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
