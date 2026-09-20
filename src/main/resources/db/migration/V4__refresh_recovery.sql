alter table identity_reference add constraint ck_identity_refresh_status
 check (refresh_status is null or refresh_status in ('SUCCESS','NOT_FOUND','PROVIDER_FAILURE','RETRY_EXHAUSTED','PROVIDER_TIMEOUT','ERROR'));
create index ix_identity_refresh_status on identity_reference(refresh_status,next_refresh_at);