create table identity_test_fixture (
 identity_reference_id varchar(36) not null, fixture_label varchar(120), scenario_tag varchar(120), nationality varchar(100), expiration_date date,
 photo_version varchar(64), photo_content blob, photo_content_type varchar(64), photo_size bigint, biometric_status varchar(32) not null,
 created_by varchar(128), created_at timestamp(6) with time zone not null, updated_by varchar(128), updated_at timestamp(6) with time zone not null,
 constraint pk_identity_test_fixture primary key(identity_reference_id), constraint fk_identity_test_fixture_ref foreign key(identity_reference_id) references identity_reference(id)
);
create index ix_identity_test_fixture_label on identity_test_fixture(fixture_label);
