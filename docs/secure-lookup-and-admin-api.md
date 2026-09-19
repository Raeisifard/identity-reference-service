# Secure lookup and administrative APIs

Phase 12 adds the first HTTP API boundary for identity lookup and privileged operational commands.

## Endpoints

- POST /api/v1/identity/lookup — authenticated lookup using national ID, birth date and provider ID.
- POST /api/v1/admin/refresh — ADMIN-only refresh command hook. It queues an operation ID but does not execute provider refresh itself.
- GET /api/v1/admin/status — ADMIN-only operational/provider status.

The lookup response is deliberately minimized: it never returns national ID, birth date, photos, embeddings or provider payloads. Provider errors are mapped to a generic unavailable response.

## Authentication

Spring Security HTTP Basic is used as a replaceable authentication boundary for this milestone. It is disabled by default so existing local startup remains unchanged. Enable it through configuration/environment variables:

IDENTITY_API_LOOKUP_USERNAME
IDENTITY_API_LOOKUP_PASSWORD
IDENTITY_API_ADMIN_USERNAME
IDENTITY_API_ADMIN_PASSWORD

and set identity-reference.api.security.enabled=true.

The lookup principal receives ROLE_LOOKUP; the administrative principal receives ROLE_ADMIN. Real deployments should replace the development credential mechanism with the platform's service authentication/mTLS or token infrastructure.

## Anti-enumeration and rate limiting

The API does not expose lookup material or provider payloads, uses a stable response shape for found/not-found/error outcomes, and never logs sensitive lookup fields. Lookup requests are rate limited per authenticated principal by a bounded in-process hook. A distributed rate limiter should replace this hook before horizontal production deployment.

The current provider lookup still uses the Phase 11 normalized provider SPI. Durable lookup/cache integration and executable refresh processing remain owned by their existing application/persistence/scheduler boundaries; the admin refresh endpoint is intentionally a command hook rather than a fake refresh implementation.

## Security configuration

API security is disabled by default in the base configuration. No Oracle or Redis local connection settings are changed by this milestone.
