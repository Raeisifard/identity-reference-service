create table identity_reference (
    id varchar2(36 char) not null,
    lookup_key_hash varchar2(128 char) not null,
    given_name varchar2(200 char) not null,
    family_name varchar2(200 char) not null,
    father_name varchar2(200 char),
    birth_date date not null,
    gender varchar2(32 char),
    national_id_ciphertext blob,
    lifecycle_state varchar2(32 char) not null,
    acquired_at timestamp(6) with time zone,
    fresh_until timestamp(6) with time zone,
    stale_until timestamp(6) with time zone,
    next_refresh_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    constraint pk_identity_reference primary key (id),
    constraint uk_identity_lookup_hash unique (lookup_key_hash)
);
create index ix_identity_refresh on identity_reference(next_refresh_at);
