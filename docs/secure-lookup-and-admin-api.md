# Phase 12 — Secure lookup and administrative APIs

**Status: DONE**

Phase 12 introduced the API boundary. Phase 14 makes the administrative refresh command executable and targeted.

## Endpoints
- POST /api/v1/identity/lookup — authenticated lookup using national ID, birth date and provider ID.
- POST /api/v1/admin/refresh — targeted refresh for one identity using provider ID, national ID and birth date.
- GET /api/v1/admin/status — provider status.

The admin refresh endpoint is intentionally targeted. It does not expose a bulk/full-provider refresh operation.

The lookup response remains minimized: it does not return national ID, birth date, photos, embeddings or provider payloads.

Targeted refresh uses the same `IdentityReferenceRefreshService` used by automatic and scheduled refresh. Provider retrieval, freshness calculation, persistence and cache update are therefore consistent across refresh triggers.

API authentication and the development credential boundary remain unchanged.
