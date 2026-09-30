create table identity_biometric_reference (
 id varchar(36) not null,
 identity_reference_id varchar(36) not null,
 model_id varchar(128) not null,
 model_version varchar(128) not null,
 dimension integer not null,
 metric varchar(32) not null,
 normalized boolean not null,
 source_photo_version varchar(128) not null,
 state varchar(32) not null,
 vector blob not null,
 created_at timestamp(6) with time zone not null,
 constraint pk_identity_biometric_ref primary key (id),
 constraint fk_identity_biometric_ref foreign key (identity_reference_id) references identity_reference(id),
 constraint uk_identity_bio_model_photo unique (identity_reference_id,model_id,model_version,source_photo_version)
);
create index ix_identity_bio_lookup on identity_biometric_reference(identity_reference_id,model_id,model_version);
