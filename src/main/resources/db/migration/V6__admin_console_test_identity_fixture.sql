create table identity_test_fixture (
 identity_reference_id varchar2(36 char) not null, fixture_label varchar2(120 char), scenario_tag varchar2(120 char), nationality varchar2(100 char), expiration_date date,
 photo_version varchar2(64 char), photo_content blob, photo_content_type varchar2(64 char), photo_size number(19), biometric_status varchar2(32 char) not null,
 created_by varchar2(128 char), created_at timestamp(6) with time zone not null, updated_by varchar2(128 char), updated_at timestamp(6) with time zone not null,
 constraint pk_identity_test_fixture primary key(identity_reference_id), constraint fk_identity_test_fixture_ref foreign key(identity_reference_id) references identity_reference(id)
);
create index ix_identity_test_fixture_label on identity_test_fixture(fixture_label);
