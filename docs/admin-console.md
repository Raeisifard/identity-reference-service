# Phase 15 — Admin console

The admin console now uses the reference artifact layout as its visual basis: left workspace rail, grouped navigation, compact status cards, activity panel, system-health panel, provider registry, gradient action banner, and responsive views.

## UI

Open:

```text
/admin-console/index.html
```

Every navigation item has a dedicated rendered workspace, rather than only changing the page heading:

- Overview
- Providers
- Identity pipeline
- Cache
- Refresh & scheduler
- Persistence
- Metrics
- Integration testing
- Settings

## API

- `GET /api/v1/admin/console/overview`
- `GET /api/v1/admin/console/capabilities`
- `GET /api/v1/admin/console/domains`
- `POST /api/v1/admin/console/testing/smoke`

The UI only displays data already available from the service and clearly labels configuration-backed or illustrative operational summaries. It does not expose identity payloads, credentials, biometric vectors, or provider response bodies.

## Safety

Console and testing flags remain disabled by default. The testing endpoint is conditionally registered only when integration testing is explicitly enabled.
