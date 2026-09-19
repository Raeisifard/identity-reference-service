create table identity_biometric_reference (
    id varchar2(36 char) not null,
    identity_reference_id varchar2(36 char) not null,
    model_id varchar2(128 char) not null,
    model_version varchar2(128 char) not null,
    dimension number(10) not null,
    metric varchar2(32 char) not null,
    normalized number(1) not null,
    source_photo_version varchar2(128 char) not null,
    state varchar2(32 char) not null,
    vector blob not null,
    created_at timestamp(6) with time zone not null,
    constraint pk_identity_biometric_ref primary key (id),
    constraint fk_identity_biometric_ref foreign key (identity_reference_id) references identity_reference(id),
    constraint uk_identity_bio_model_photo unique (identity_reference_id,model_id,model_version,source_photo_version)
);
create index ix_identity_bio_lookup on identity_biometric_reference(identity_reference_id,model_id,model_version);
