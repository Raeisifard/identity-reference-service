# Admin Console V2 — extensible web panel, security profiles, feature flags and help center

## Purpose

Build the **Admin Console V2** for `identity-reference-service` from the current `main` architecture rather than merging the old `feature/admin-console-v1` implementation.

The attached `Identity_Reference_Service_Admin_Test_Console.pdf` is the visual/design reference. Preserve its visual language and information hierarchy:

- dark brown / near-black application shell
- amber/orange accent color
- restrained cards, borders and status indicators
- clear environment indicator
- left navigation / section navigation
- operational KPI and monitoring cards
- strong distinction between sandbox/development and production
- dense operational information presented in an organized, enterprise/banking-style layout
- API Explorer / testing concepts from the reference design
- governance and security information as first-class console content

Do not copy the old prototype's fake operational numbers or stale backend assumptions.

This is a **plan prompt**. Implement this milestone only when explicitly requested.

---

## 1. Core architectural decision

The console is a presentation and administration layer over the existing service APIs and domain services.

It must be built from the current `main` branch state and must not replace or duplicate existing business logic.

Preferred initial implementation:

- Spring Boot serves static console resources.
- No React/Node build is required for V2.
- HTML + CSS + modern browser JavaScript is sufficient initially.
- Keep the frontend modular so a future React/SPA migration does not require changing the backend contracts.
- Reuse the existing admin, lookup, refresh, governance, observability and biometric-integration services.
- Do not introduce a second identity/reference data path merely for the UI.
- Do not expose persistence entities directly to the browser.
- Console DTOs must be purpose-built and privacy-safe.

The console should be extensible by adding a new section descriptor/controller/service without rewriting the navigation shell.

---

## 2. Dedicated console configuration

The web panel must have its **own property file**. Do not scatter console settings through the main application configuration.

Create a dedicated configuration resource, preferably:

`src/main/resources/admin-console.yml`

The application should import it explicitly/optionally according to the project's existing configuration conventions.

All console settings must live under a dedicated prefix such as:

`identity-reference.admin-console`

The console configuration must include at least:

### Global

- `enabled`
- `title`
- `path`
- `development-mode`
- `authentication.enabled`
- `authentication.login-page-enabled`
- `authentication.session-timeout`
- `help-center.enabled`
- `api-base-path`

### Section feature flags

Every console section must have its own enable/disable property.

At minimum:

- `sections.dashboard.enabled`
- `sections.monitoring.enabled`
- `sections.providers.enabled`
- `sections.cache.enabled`
- `sections.refresh.enabled`
- `sections.lookup.enabled`
- `sections.administration.enabled`
- `sections.biometric.enabled`
- `sections.api-testing.enabled`
- `sections.governance.enabled`
- `sections.audit.enabled`
- `sections.security.enabled`
- `sections.system.enabled`
- `sections.help.enabled`

A disabled section must be removed from navigation **and** its backend endpoints must be disabled/protected. Hiding a button in JavaScript is not sufficient.

This is particularly important for production. For example:

```yaml
identity-reference:
  admin-console:
    sections:
      api-testing:
        enabled: false
```

must prevent the API-testing functionality from being available.

Do not rely only on frontend feature flags for security.

---

## 3. Development versus production/pro profile

The console must have two explicit operating modes.

### Development / sandbox

When running with the development profile:

- console can operate without authentication
- this must be explicit and visible in the UI
- the UI must clearly display `SANDBOX` / `DEVELOPMENT`
- destructive actions should still require confirmation
- sensitive values remain masked
- API testing can be enabled
- test/synthetic functionality can be enabled
- raw development diagnostics may be available only where explicitly configured

The development mode must never silently become the production security model.

### Production / pro

When running with the production/pro profile:

- authentication is mandatory
- a login page/panel is displayed
- unauthenticated users cannot access the console
- API testing is disabled by default
- destructive administration requires explicit confirmation
- PII/raw identity export remains blocked unless a future, explicitly authorized capability permits it
- sensitive configuration and secrets must never be rendered
- all administrative actions must be auditable
- the console must not weaken the security of existing REST APIs

Fail closed where possible. In particular, production must not start with the console exposed anonymously because of a missing authentication setting.

Authentication must be designed as an extensible boundary so the initial implementation can use the project's existing administrative credential mechanism while allowing a future LDAP/AD/OIDC/SSO provider without redesigning the console.

---

## 4. Console navigation

Use the PDF's enterprise-style navigation and organize the console into logical groups.

### Monitoring & Operations

- Dashboard
- Health / system status
- Providers
- Cache
- Refresh / synchronization
- Operational metrics

### Administration

- Identity lookup
- Refresh
- Retire
- Audit

### Integration Testing

- API Explorer
- Biometric reference / contract viewer
- Smoke tests
- Test history

### Governance & Security

- Governance
- Retention
- Audit
- Security controls
- Production guardrails

### Help

- Contextual help
- Help Center
- Architecture
- Features
- Operations guide
- API guide
- Security/privacy guide
- Glossary

Only show navigation entries for enabled sections.

---

## 5. Dashboard requirements

The dashboard must use **real backend information**.

Never hardcode production-looking values such as:

- uptime percentages
- latency values
- cache hit rate
- refresh backlog
- provider availability

If a metric is not implemented or cannot be obtained, show `—`, `N/A`, or an explicit `Not available` state.

The dashboard should include, where supported by current services:

- environment/profile
- service health
- provider availability
- cache/storage state
- refresh status
- governance status
- biometric integration status
- enabled/disabled console capabilities
- security/production guard status
- timestamp of the displayed snapshot

The UI must distinguish:

- implemented and available
- configured but unavailable
- disabled by configuration
- not implemented yet

---

## 6. Existing backend integration

Map the UI to the existing backend instead of creating fake replacement APIs.

Expected integrations include:

- lookup -> existing lookup API
- admin refresh -> existing admin refresh API
- retire -> existing admin retire API
- provider registry -> existing provider SPI/registry
- health -> Spring/application health
- observability -> existing metrics/operational services
- cache -> existing Redis/Caffeine state where safely observable
- refresh -> existing refresh/synchronization engine
- governance -> existing retirement/audit/retention services
- biometric -> Phase 18 model-aware biometric integration

The biometric section must display model-aware metadata such as:

- `identityReferenceId`
- `modelId`
- `modelVersion`
- `dimension`
- `metric`
- `normalized`
- `sourcePhotoVersion`
- `state`
- `createdAt`

Do not expose the raw embedding vector casually in the normal console UI.

---

## 7. API Explorer

Retain the API Explorer concept from the PDF/prototype, but make it a real controlled tool.

Requirements:

- show only APIs that are enabled and authorized
- allow request construction
- show HTTP method/path
- show safe example request bodies
- show response status and formatted response
- show correlation/request ID
- mask sensitive values
- never display secrets or credentials
- never allow arbitrary production destructive calls by default
- disable the entire API-testing section in production unless explicitly and safely enabled
- clearly label sandbox/testing mode

The API Explorer must not bypass normal authentication, authorization, validation, rate limiting or audit mechanisms.

---

## 8. Help button on every section

Every major console section must have a `?` help button.

Clicking it should open a compact callout/popover/drawer containing useful, section-specific help.

Each help item should explain:

1. What this section represents.
2. What information is displayed.
3. How to use it.
4. What each important field/status means.
5. Which backend capability it uses.
6. What actions are safe/read-only.
7. What actions are administrative/destructive.
8. What production restrictions apply.
9. Where to find more detailed documentation.

The help UI should not be a generic placeholder. Each section should have meaningful content.

Help content should be extensible so adding a new console section also allows registration of its help content.

---

## 9. Full Help Center

Create a dedicated Help Center page, accessible from the main navigation/header.

Suggested route:

`/admin-console/help/`

It should feel like the documentation/help areas of mature enterprise systems.

It should contain:

### Welcome / Getting Started

- what the Identity Reference Service is
- who the console is for
- development versus production behavior
- navigation overview
- first-use guide

### System Architecture

- high-level architecture
- service boundaries
- provider SPI
- acquisition/refresh flow
- Oracle persistence
- Redis L2 cache
- Caffeine L1 cache
- refresh scheduler
- failure/recovery and leases
- governance/audit
- biometric integration
- security boundaries

### Feature Documentation

One page/section for each enabled capability:

- dashboard
- provider management/visibility
- lookup
- refresh
- retirement
- cache
- audit
- biometric reference
- API Explorer
- governance
- testing
- security

### Operations Guide

- health checks
- provider troubleshooting
- cache troubleshooting
- refresh troubleshooting
- failure/retry states
- lease behavior
- retention/purge
- audit interpretation
- common operational problems

### Security & Privacy

- development versus production
- authentication
- authorization
- PII handling
- masking
- auditability
- production restrictions
- biometric data handling
- cryptographic protection status
- prohibited actions

The Help Center must describe the actual current implementation. Do not document future functionality as if it already exists.

### API Guide

- lookup
- refresh
- retire
- status
- biometric integration
- console endpoints
- request/response examples
- authentication requirements
- error/status meanings

### Configuration Guide

- console enable/disable
- per-section flags
- profile behavior
- authentication
- testing restrictions
- help settings

### Glossary

Include important domain and technical terms such as:

- identity reference
- provider
- provider policy
- lookup
- refresh
- synchronization
- TTL
- stale
- lease
- retry
- retirement
- purge
- audit event
- biometric reference
- model ID
- model version
- embedding
- normalized vector
- L1 cache
- L2 cache
- production guard
- sandbox
- PII

### Documentation Index

Provide:

- searchable or clearly indexed content
- table of contents
- section links
- breadcrumbs/back links
- glossary links
- related-document links

Prefer a lightweight static/document-driven implementation initially so the help center does not require a Node build.

---

## 10. Help architecture

Do not hardcode all help text directly inside JavaScript.

Use an extensible documentation/help model.

Possible initial structure:

`src/main/resources/static/admin-console/help/`

with separate HTML/JSON/Markdown-derived content as appropriate.

A backend endpoint may expose section metadata/help metadata if that fits the existing architecture.

Each section should have:

- section ID
- display name
- enabled flag
- navigation group
- icon
- short description
- contextual help
- detailed help/document reference
- required permission/capability
- destructive/read-only classification

This metadata should drive navigation and contextual help where practical.

---

## 11. Extensibility model

Design the console so future features can be added without rewriting the shell.

Introduce a concept such as:

`AdminConsoleSection`

or equivalent descriptor containing:

- ID
- label
- route
- group
- icon
- enabled/configuration predicate
- required authority
- contextual help
- documentation reference

The exact class/interface names are implementation details.

A new feature should be able to:

1. implement/register its backend section metadata,
2. add its configuration flag,
3. add its navigation entry,
4. add its contextual help,
5. add its Help Center documentation,
6. add its authorization requirement.

Avoid a monolithic controller containing every future console operation.

---

## 12. Security requirements

The console is an administrative surface and must be treated as security-sensitive.

Requirements:

- no authentication bypass in production
- no console endpoint enabled merely because a frontend flag is true
- no secret values in responses or logs
- no raw PII in normal monitoring views
- mask national ID and other sensitive identifiers
- explicit confirmation for retirement/destructive operations
- audit administrative operations
- respect existing rate limiting/security controls
- use CSRF protection where form/session authentication requires it
- secure cookies/session configuration for production
- safe error messages
- correlation IDs
- no stack traces in browser responses
- no arbitrary server-side command execution
- no arbitrary URL fetching from the API Explorer
- no arbitrary production data export

The console must not become an alternative path around the existing service security model.

---

## 13. UI/UX requirements from the PDF

Preserve the PDF's visual identity rather than replacing it with a generic admin template.

Keep:

- dark brown/near-black shell
- amber/orange accent
- compact enterprise cards
- clear section hierarchy
- environment badge
- operational status chips
- restrained typography
- strong spacing/alignment
- responsive behavior for smaller screens
- clear distinction between primary actions and destructive actions
- visual consistency between dashboard, forms, API Explorer, governance and help pages

Improve the prototype where needed:

- avoid excessive density on complex pages
- use collapsible/expandable groups for large operational sections
- make status semantics explicit
- never use decorative fake metrics
- maintain keyboard accessibility and readable contrast
- make help popovers usable on both desktop and mobile

---

## 14. Production-safe defaults

Recommended defaults:

### Development profile

- console enabled: true
- authentication: false
- API testing: true
- smoke/testing tools: true
- destructive actions: available with confirmation
- help center: true

### Production/pro profile

- console enabled: true
- authentication: true and mandatory
- API testing: false
- synthetic testing: false unless explicitly enabled
- raw export: false
- destructive actions: confirmation + authorization + audit
- help center: true
- sensitive diagnostics: false unless explicitly authorized

These are defaults for the console feature, not a replacement for the application's broader security configuration.

---

## 15. Testing requirements

Add tests for:

### Configuration

- console disabled -> no console endpoints/resources exposed where applicable
- each section flag works independently
- disabled API testing cannot be invoked
- production mode rejects anonymous access
- development mode can run without authentication
- invalid production security configuration fails closed

### Backend

- enabled section endpoints work
- disabled section endpoints are unavailable/forbidden
- DTOs do not expose sensitive fields
- destructive operations require appropriate authorization
- audit events are produced for administrative actions

### Frontend

At minimum verify:

- navigation reflects enabled sections
- environment indicator is correct
- help buttons exist for enabled sections
- help popovers open and close correctly
- Help Center navigation works
- API Explorer respects disabled/production state
- no fake metric values are rendered

Use the project's existing test stack and avoid introducing a large frontend build/test framework unless necessary.

---

## 16. Documentation requirements

When implementation begins:

- add an implementation report under `docs/`
- update `docs/PHASE-REPORTS.md` only if this work is assigned a canonical phase/milestone
- do not mark unrelated phases complete
- document the console configuration
- document development/prod behavior
- document each section flag
- document authentication behavior
- document the Help Center structure
- document known limitations

This prompt is intentionally a cross-cutting Admin Console V2 plan and does not renumber the canonical Phase 19–22 sequence.

---

## 17. Implementation sequence

Implement incrementally:

### Step 1 — Console foundation

- dedicated properties
- configuration binding
- enable/disable mechanism
- profile/security mode
- static shell
- PDF visual theme
- navigation framework

### Step 2 — Real dashboard

- health
- environment
- providers
- cache
- refresh
- governance
- biometric integration status

### Step 3 — Administration

- lookup
- refresh
- retire
- audit

### Step 4 — Integration testing

- API Explorer
- biometric contract viewer
- smoke tests
- test history

Keep production defaults restrictive.

### Step 5 — Contextual help

- `?` button framework
- section-specific help
- help metadata

### Step 6 — Full Help Center

- documentation index
- architecture
- features
- operations
- security/privacy
- API guide
- configuration guide
- glossary

### Step 7 — Security and test hardening

- production authentication
- authorization
- CSRF/session controls
- disabled-feature endpoint enforcement
- audit coverage
- configuration validation
- UI/security tests

---

## 18. Acceptance criteria

The Admin Console V2 milestone is complete only when:

- [ ] Visual theme follows the supplied PDF.
- [ ] Old prototype fake monitoring values are not reused.
- [ ] Console configuration is isolated in its own property file.
- [ ] Every major console section has an independent enable/disable property.
- [ ] Disabled sections are unavailable server-side, not merely hidden client-side.
- [ ] Development profile can operate without authentication.
- [ ] Production/pro profile requires authentication and presents a login panel.
- [ ] Production defaults disable API testing.
- [ ] Console authentication is extensible.
- [ ] Dashboard uses real backend data or explicit unavailable states.
- [ ] Existing backend services remain the source of truth.
- [ ] Biometric UI respects the model-aware contract.
- [ ] Every major section has a `?` contextual help control.
- [ ] Help content explains the section and how to use it.
- [ ] A dedicated Help Center page exists.
- [ ] Help Center contains architecture, features, operations, security/privacy, API/configuration guidance and glossary.
- [ ] Help Center has a user-friendly index/table of contents.
- [ ] Console sections are extensible through metadata/registration rather than a monolithic controller.
- [ ] No secrets or unnecessary PII are exposed.
- [ ] Administrative/destructive actions are confirmed and audited.
- [ ] Automated tests cover feature flags and production/development security behavior.
- [ ] Documentation is updated.
- [ ] Maven verification passes.
- [ ] Implementation is committed with a focused commit message.

## Handoff

When implementation is requested, first inspect the current `main` branch and all relevant existing prompts/docs/controllers/configuration.

Do not merge the old `feature/admin-console-v1` branch wholesale.

Reuse its visual ideas only where they are compatible with the current architecture.

Implement only this console milestone and minimal prerequisites. Do not start Phase 19, 20, 21 or 22 automatically.

At completion report:

- changed files
- configuration properties
- enabled/disabled sections
- authentication behavior
- Help Center structure
- tests executed
- known limitations
- commit hash
