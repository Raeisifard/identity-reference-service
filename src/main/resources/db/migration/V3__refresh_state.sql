alter table identity_reference add provider_id varchar2(128 char);
alter table identity_reference add provider_record_id varchar2(256 char);
alter table identity_reference add provider_authority varchar2(32 char);
alter table identity_reference add policy_version number(19);
alter table identity_reference add refresh_status varchar2(32 char);
alter table identity_reference add refresh_error varchar2(256 char);
alter table identity_reference add refresh_retry_count number(10);
create index ix_identity_provider_refresh on identity_reference(provider_id,next_refresh_at);