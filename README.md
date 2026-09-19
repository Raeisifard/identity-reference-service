# identity-reference-service

Policy-driven acquisition, normalization, caching, and reference-data service for identity information from multiple authoritative or semi-authoritative providers.

Architecture: Caffeine L1 -> Redis L2 -> Oracle durable store. MongoDB is optional for raw provider/document payloads.

The target population is approximately 8 million identities. Capacity must be measured rather than assumed. Provider-specific policies control validation, authority, overwrite/conflict rules, TTL, stale grace, refresh windows, jitter, quotas, retry and embedding behavior.

Refresh is driven by nextRefreshAt and distributed across configurable quiet windows (for example midnight-to-dawn); the scheduler must use jitter and provider quotas rather than creating a synchronized midnight storm.

This service manages reference-data and optional reference-embedding lifecycle. face-biometric-service remains responsible for biometric verification. Every embedding carries model ID, model version, dimension, metric/normalization and source-photo version.

National identity attributes, photos and biometric vectors are highly sensitive. Production requires encryption, Vault-backed secrets, service authorization, auditability, retention/deletion controls, anti-enumeration/rate limiting and strict sensitive-data logging rules.

Real national-agency endpoints and credentials are intentionally not invented. The first external adapter is a mock until contractual API documentation is supplied.

See docs/ for architecture and prompts/ for the sequential AI implementation agenda.
