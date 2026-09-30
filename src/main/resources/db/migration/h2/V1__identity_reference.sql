create table identity_reference (
 id varchar(36) not null,
 lookup_key_hash varchar(128) not null,
 given_name varchar(200) not null,
 family_name varchar(200) not null,
 father_name varchar(200),
 birth_date date not null,
 gender varchar(32),
 national_id_ciphertext blob,
 lifecycle_state varchar(32) not null,
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
