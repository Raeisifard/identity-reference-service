create table identity_audit_event (
 event_id varchar2(36 char) not null, lookup_key_hash varchar2(128 char) not null, event_type varchar2(64 char) not null,
 actor_type varchar2(32 char) not null, reason_code varchar2(64 char), provider_id varchar2(128 char), policy_version number(19),
 from_state varchar2(32 char), to_state varchar2(32 char), operation_id varchar2(36 char), occurred_at timestamp(6) with time zone not null,
 constraint pk_identity_audit_event primary key (event_id)
);
create index ix_identity_audit_lookup on identity_audit_event(lookup_key_hash,occurred_at);
create index ix_identity_audit_retention on identity_audit_event(occurred_at);
