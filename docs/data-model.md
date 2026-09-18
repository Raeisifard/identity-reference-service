# Data model

Suggested durable entities:
- identity_reference
- identity_provider_record
- identity_biometric_reference
- provider_policy
- refresh_job
- refresh_attempt
- identity_audit_event

Use a stable internal reference ID, not a national ID, as the relational primary key.

Provider identity should be unique by provider ID + provider record ID. Keep protected/deterministic lookup keys separate from displayable identity data.