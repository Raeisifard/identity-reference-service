alter table identity_reference add provider_id varchar(128);
alter table identity_reference add provider_record_id varchar(256);
alter table identity_reference add provider_authority varchar(32);
alter table identity_reference add policy_version bigint;
alter table identity_reference add refresh_status varchar(32);
alter table identity_reference add refresh_error varchar(256);
alter table identity_reference add refresh_retry_count integer;
create index ix_identity_provider_refresh on identity_reference(provider_id,next_refresh_at);
