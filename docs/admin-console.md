# Phase 15 — Admin console foundation

The first admin-console slice is available on `feature/admin-console-v1`.

## Runtime boundaries
- `admin-console.yml` is imported as an optional separate configuration file.
- `identity-reference.admin-console.enabled` defaults to `false`.
- Development and integration-testing capabilities also default to `false`.
- The console controller is conditionally created only when the console is enabled.
- Enable the console only in a non-production profile and protect it with the existing admin API security boundary.

## Initial monitoring API
- `GET /api/v1/admin/console/overview` — service health, configured providers and capability flags.
- `GET /api/v1/admin/console/capabilities` — explicit feature availability for the UI.

The visual shell will use the referenced Ashna design as the basis for the next slice. Sensitive identity attributes, provider payloads, credentials and biometric data are not returned by the console API.
