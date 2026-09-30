create table identity_audit_event (
 event_id varchar(36) not null,
 lookup_key_hash varchar(128) not null,
 event_type varchar(64) not null,
 actor_type varchar(32) not null,
 reason_code varchar(64),
 provider_id varchar(128),
 policy_version bigint,
 from_state varchar(32),
 to_state varchar(32),
 operation_id varchar(36),
 occurred_at timestamp(6) with time zone not null,
 constraint pk_identity_audit_event primary key (event_id)
);
create index ix_identity_audit_lookup on identity_audit_event(lookup_key_hash,occurred_at);
create index ix_identity_audit_retention on identity_audit_event(occurred_at);
