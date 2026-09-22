# Admin Console — Development/Test Identity Fixture Management Plan

## Purpose

Add a dedicated Development/Test Identity Data section to the Admin Console so developers and testers can create, edit, inspect, replace and retire synthetic/test identity-reference records without depending on an external identity provider.

The feature is intended for environments where a real provider is unavailable, unreachable, unsuitable for automated testing, or where deterministic test identities are required.

The uploaded/admin-console PDF remains the visual reference. The new page must use the same dark brown / near-black shell, amber/orange accents, compact enterprise cards, status chips, left-side navigation and form/table conventions.

This is a plan prompt. Do not implement it until explicitly requested.

---

## 1. Architectural evaluation

This capability is a good fit for the service, provided it is implemented as a controlled test-fixture management capability, not as a second production identity-ingestion path.

Recommended architecture:

Admin Console -> Test Identity API -> existing domain/application services -> Oracle persistence -> existing biometric ingestion/reference path.

Do not let the browser write directly to Oracle.

The test-data feature should create records through the same domain model and persistence boundaries used by the service wherever practical. This keeps lookup, lifecycle, refresh, biometric reference resolution and retirement behavior representative of real service behavior.

The test fixture must be distinguishable from externally sourced identities.

Recommended source classification: DEV_TEST or TEST_FIXTURE, or an equivalent existing source/provider classification if the current domain already supports it.

Do not invent a second incompatible identity model merely for the console.

---

## 2. Primary use cases

The console must support:

1. Create a development/test identity.
2. Upload a face photo.
3. Capture a face photo using the browser webcam when available.
4. Replace/update the test identity photo.
5. View the stored test identity and its current status.
6. Search/list test identities.
7. Edit allowed demographic/test fields.
8. Retire a test identity.
9. Rebuild/update its biometric reference when the photo changes.
10. Use the created identity through the normal lookup/reference APIs.
11. Delete/purge test data only through an explicitly protected test-data operation where the existing governance model permits it.
12. Generate deterministic test data where useful for automated/integration testing.

The normal lookup path must be able to consume an eligible test fixture exactly as a reference record, subject to the environment and source policy.

---

## 3. Environment and security boundary

This is the most important requirement: the capability is for development and test environments only.

Recommended defaults:

### Development
- test-data section enabled
- test-data API enabled
- webcam/upload enabled
- authentication may remain disabled according to the Admin Console V2 development rules
- destructive actions require confirmation
- all administrative/test-data changes are audited

### Test
- test-data API enabled
- authentication behavior depends on the test profile/security configuration
- deterministic fixture creation is allowed
- destructive actions require confirmation
- test data is clearly labelled

### Production / pro
- test-data section disabled by default
- test-data endpoints disabled server-side
- fixture creation/update/deletion rejected even if a frontend request is manually crafted
- no test-data navigation item
- no webcam/upload endpoint for test fixtures
- startup should fail closed if an unsafe production configuration attempts to enable this capability unless an explicit future security override is introduced and reviewed

A hidden UI button is not a security boundary. The server must enforce the environment restriction.

---

## 4. Dedicated configuration

The Admin Console already has its own configuration plan. Extend it with a dedicated test-data section.

Preferred configuration file: src/main/resources/admin-console.yml

Example structure:

    identity-reference:
      admin-console:
        sections:
          test-data:
            enabled: true

        test-data:
          enabled: true
          allow-create: true
          allow-update: true
          allow-retire: true
          allow-purge: false
          allow-photo-upload: true
          allow-webcam-capture: true
          max-photo-size: 10MB
          allowed-image-types:
            - image/jpeg
            - image/png
          max-identities: 10000

The exact property names may be adjusted to match the implemented configuration model.

There must be no accidental production default that enables fixture creation.

The implementation should validate that test-data functionality is only enabled in allowed profiles/environments, production/pro cannot accidentally enable it through a frontend setting, limits are validated at startup, and maximum photo size and supported image types are enforced server-side.

---

## 5. Identity fixture data model

The form should support the fields required by the identity-reference domain.

At minimum consider:

### Identity data
- first name
- family/last name
- national ID
- father name
- birth date
- nationality
- document/identity expiration date
- optional gender if the current domain requires it
- optional document type if the current domain supports it
- optional provider/source metadata
- notes/test description where appropriate

### Test metadata
Add metadata that clearly identifies the record as a fixture, for example:
- source type = DEV_TEST
- created by
- created at
- updated by
- updated at
- fixture label/name
- optional test scenario/tag
- active/retired state

Do not overload normal customer fields with test markers.

If the existing Oracle schema/domain already contains some of these fields, reuse them.

If fields are missing: inspect the current domain/entity/schema first; add only the missing fields; use a forward-only Oracle migration; do not drop existing data; preserve compatibility with provider-sourced identities; do not create a parallel customer table merely for the UI.

---

## 6. National ID and sensitive data

National ID and other PII are sensitive.

The feature must follow the project's existing privacy/security model.

Important Phase 21 constraint: the current national_id_ciphertext field must not be described as real cryptographic protection until Phase 21 implements and verifies the cryptographic design.

Until then:
- do not claim that the fixture page provides cryptographic protection
- do not expose national IDs unnecessarily in lists/logs
- mask national ID in tables by default
- show full value only in an explicitly authorized edit/view context where appropriate
- never log raw national ID
- never put raw PII into correlation IDs, URLs, metrics or audit messages

The final implementation must remain compatible with the later Phase 21 cryptographic protection work.

---

## 7. Photo upload

The form must support a normal file upload.

Requirements:
- drag/drop or file chooser
- JPEG/PNG support initially
- configurable maximum size
- server-side MIME/content validation
- reject malformed/non-image content
- reasonable image dimension limits
- orientation handling
- optional client-side preview
- server-side validation remains authoritative
- do not trust the browser-provided filename or MIME type
- do not log image contents
- do not expose arbitrary filesystem paths

The uploaded photo must become the identity's reference photo through the existing domain/persistence architecture.

Do not store a browser-generated base64 string as the canonical identity record.

---

## 8. Browser webcam capture

Where supported, provide a Capture from webcam option.

Use standard browser media APIs:
- request camera permission
- display live preview
- capture a frame
- allow retake
- allow confirmation
- send the captured image through the same backend upload/validation path as a normal file

The browser feature must degrade gracefully:
- if webcam is unavailable, keep file upload available
- if permission is denied, show a useful explanation
- do not require webcam support for the page to function

The UI must explain that browser camera access may require HTTPS or a secure local development context and user permission.

Do not implement native camera drivers in the Spring Boot service.

---

## 9. Face-photo and biometric integration

The photo is not merely an attachment. It is intended to become a usable biometric reference.

When the identity/photo is saved:
1. validate and persist the identity data
2. validate/process the reference photo
3. invoke the existing biometric ingestion/reference pipeline where available
4. create/update the model-aware biometric reference
5. associate the biometric reference with the identity reference
6. expose the resulting biometric status to the UI

The implementation must reuse the existing face-biometric integration contract.

The biometric reference identity must remain model-aware:
- identityReferenceId
- modelId
- modelVersion
- dimension
- metric
- normalized
- sourcePhotoVersion
- state
- createdAt

Do not infer model compatibility from vector dimension alone.

Do not implement a second embedding algorithm inside the Admin Console.

If biometric generation is unavailable, the identity may be created only if the configured policy permits it; the UI must clearly show identity saved, photo saved, and biometric reference pending/unavailable/failed.

Do not silently report a successful biometric reference when generation failed.

---

## 10. Photo versioning

A photo replacement must not silently destroy traceability.

Use the existing source-photo versioning architecture if already present.

Recommended lifecycle:
- photo v1 uploaded
- biometric reference generated for v1
- photo replaced -> v2
- v1 remains historically traceable according to retention policy
- active biometric reference points to the current accepted photo version

The exact retention behavior must follow the existing governance rules.

Avoid storing unlimited historical images without a policy.

---

## 11. Oracle persistence

Oracle should be the durable source of truth for these test identities.

The implementation must:
- persist identity metadata in Oracle
- persist the reference photo using the project's selected Oracle representation
- persist biometric-reference metadata/vector through the existing biometric persistence design where enabled
- participate in existing lifecycle/state handling
- survive service restart
- be queryable through the normal identity-reference lookup path

The implementation must inspect the current Oracle schema before adding tables/columns.

Prefer reusing existing identity-reference and biometric tables when their model supports test fixtures.

If a dedicated fixture metadata table is necessary, it must reference the canonical identity-reference record rather than duplicate the whole customer record.

Do not make Redis or browser storage the authoritative source.

---

## 12. Lookup behavior

After creation, the fixture must be usable through the normal identity-reference lookup API.

Example flow:
1. Create test identity in console.
2. Save to Oracle.
3. Generate/attach biometric reference.
4. Search by the supported lookup inputs.
5. Receive the same normalized identity-reference representation used by provider-sourced data.
6. Use the returned identityReferenceId with face-biometric-service.

The test fixture should be clearly identified internally as test-origin data, but ordinary consumers should not need a special test-only lookup API merely to resolve the identity.

Any environment-specific filtering must be explicit and documented.

---

## 13. Provider-independent operation

This capability solves an important development problem: a developer should be able to run the identity-reference service with Oracle and the test-data feature even when no external provider is available.

Therefore the implementation should support:
- Oracle enabled
- provider integrations disabled/unavailable
- test fixture source enabled
- normal lookup against Oracle
- biometric reference integration available according to configuration

Do not require a live national/provider integration to create or use test identities.

This should be documented as a supported development/test operating mode.

---

## 14. Test Identity Management page

Add a new Admin Console side-menu item: Development & Test Data or Test Identity Data.

Recommended page sections:

### Create Test Identity
- First name
- Family name
- Father name
- National ID
- Birth date
- Expiration date
- Nationality
- other domain-required fields
- Photo source: Upload photo / Capture from webcam
- photo preview
- biometric processing status
- test label/scenario
- Save / Reset

### Existing Test Identities
A searchable table containing:
- masked national ID
- name
- birth date
- nationality
- photo status
- biometric status
- lifecycle state
- created/updated time
- fixture label
- actions

Actions:
- View
- Edit
- Replace photo
- Rebuild biometric reference
- Retire
- Purge, only if explicitly enabled and authorized

### Identity Detail
Show:
- identity metadata
- masked sensitive identifiers
- photo preview
- photo version
- biometric reference status
- model metadata
- lifecycle state
- source = development/test fixture
- audit/history summary

Do not display raw embedding vectors in the normal page.

---

## 15. Form validation

The UI should provide useful validation, but the backend is authoritative.

Validate where appropriate:
- required names
- national ID format
- national ID uniqueness according to the existing identity key rules
- birth date
- expiration date
- nationality
- maximum photo size
- supported image type
- image decodability
- required face/photo quality rules if the biometric pipeline exposes them

The implementation should clearly distinguish invalid identity data, invalid photo, face not detected, biometric generation failure, duplicate identity and persistence failure.

Do not collapse all failures into Save failed.

---

## 16. Duplicate and overwrite semantics

The feature must have explicit semantics for an existing national ID.

Do not silently overwrite an existing provider-sourced identity.

Recommended behavior:
- no identity exists -> create fixture
- existing DEV_TEST identity -> offer update/edit
- existing provider-sourced identity -> reject creation by default
- require an explicit, separately authorized operation for any future merge/override behavior

A test fixture must never accidentally replace real provider data.

---

## 17. Lifecycle and governance

Test identities should participate in the existing lifecycle model.

Use existing states such as ACTIVE and RETIRED or the project's actual equivalent.

Retirement must:
- stop normal use according to existing lookup rules
- invalidate/evict applicable cache entries
- handle biometric references consistently
- produce an audit event
- record a reason

Purge must follow retention/governance policy and must not be implemented as an ad-hoc direct delete from the browser.

---

## 18. Audit requirements

Audit all administrative fixture operations:
- create
- update
- photo replacement
- biometric rebuild
- retire
- purge
- failed destructive operation where useful

Audit events must contain operational metadata, not raw identity payloads.

Never place raw national ID, raw photo, embedding vector or passwords into audit messages.

Use existing privacy-safe lookup-key hashing/fingerprinting mechanisms where applicable.

---

## 19. Integration with Admin Console V2

This page must be a first-class Admin Console section.

Extend the Admin Console V2 plan with:
- section ID: test-data
- navigation group: Integration Testing or Development & Test
- appropriate test/identity icon
- route such as /admin-console/test-data/
- required authority: test-data administration capability
- contextual ? help
- Help Center documentation
- environment badge indicating DEV/TEST ONLY

The section must disappear from navigation when disabled.

Its backend endpoints must also be disabled/protected.

The page should use the same shell, typography, cards, form controls, dialogs and status chips as the rest of the Admin Console.

---

## 20. Extensibility for future test scenarios

Do not make the form a one-off hardcoded customer editor.

Design it so future capabilities can be added, such as:
- multiple test identities per scenario
- test data sets
- scenario tags
- import/export of non-sensitive synthetic fixtures
- deterministic fixture generation
- bulk fixture creation
- test-case association
- expiry of temporary fixtures
- reset-to-known-fixture
- synthetic versus manually entered data
- automated test setup hooks

These should be future extensions, not necessarily part of the first implementation.

The initial architecture should provide a clean extension point such as TestIdentityFixtureService or equivalent.

---

## 21. API design

Do not expose Oracle entities directly.

Create purpose-built request/response DTOs.

Possible API shape:
- POST /api/v1/admin/test-data/identities
- GET /api/v1/admin/test-data/identities
- GET /api/v1/admin/test-data/identities/{id}
- PUT /api/v1/admin/test-data/identities/{id}
- POST /api/v1/admin/test-data/identities/{id}/photo
- POST /api/v1/admin/test-data/identities/{id}/biometric/rebuild
- POST /api/v1/admin/test-data/identities/{id}/retire
- DELETE /api/v1/admin/test-data/identities/{id} only if purge is explicitly supported

The exact routes are implementation details and must be reconciled with existing Admin API conventions before implementation.

---

## 22. Testing requirements

### Security
- test-data disabled -> endpoints unavailable
- production/pro profile -> test-data endpoints unavailable by default
- anonymous access cannot create test data in production
- configuration cannot accidentally expose the feature
- authorization is enforced

### Identity persistence
- create fixture
- retrieve fixture
- update fixture
- restart/reload persistence
- duplicate fixture behavior
- provider identity cannot be silently overwritten

### Photo
- valid JPEG/PNG accepted
- unsupported type rejected
- oversized photo rejected
- malformed image rejected
- replacement increments/changes photo version
- no raw image data appears in logs

### Biometric integration
- photo save invokes the existing biometric path where configured
- model-aware metadata is preserved
- failed biometric generation is reported accurately
- retired identity does not expose an active biometric reference

### Governance
- retire is audited
- purge obeys retention/configuration
- cache invalidation occurs where applicable

### UI
- section appears only when enabled
- DEV/TEST-only badge is visible
- upload workflow works
- webcam-unavailable fallback works
- help button works
- destructive confirmations are displayed
- raw embedding is not shown

---

## 23. Help Center documentation

Add documentation for this feature to the Admin Console Help Center.

Explain:
- why test identities exist
- when to use them
- how to create one
- how to upload a photo
- how webcam capture works
- how biometric processing works
- how to replace a photo
- how to retire/purge a fixture
- how test fixtures differ from provider data
- why provider data must not be overwritten
- how Oracle persistence works
- how lookup uses the resulting identity
- security and privacy restrictions
- production restrictions
- troubleshooting

Include examples using clearly synthetic/non-real identity values.

Do not place real customer data in documentation, examples, fixtures or source code.

---

## 24. Implementation constraints

Before implementation:
1. Inspect current main.
2. Inspect the current domain model and Oracle schema/migrations.
3. Inspect the current biometric ingestion/reference implementation.
4. Inspect the current Admin Console V2 plan.
5. Inspect existing admin/security conventions.
6. Reuse existing lifecycle, audit, cache and biometric services.
7. Do not overwrite real values in application-local.yml.
8. Do not create a second source of truth for identity data.
9. Do not implement real cryptographic protection early; remain compatible with Phase 21.
10. Do not merge the old feature/admin-console-v1 branch wholesale.

Use forward-only Oracle migrations and preserve existing data.

---

## 25. Recommended implementation sequence

### Step 1 — Domain/storage assessment
- identify existing identity/photo/biometric fields
- identify missing fields
- identify whether a fixture-source marker already exists
- design Oracle migration only where necessary

### Step 2 — Fixture service
- create TestIdentityFixtureService
- validation
- create/update/retire
- duplicate rules
- audit integration

### Step 3 — Photo management
- multipart upload
- validation
- Oracle persistence
- photo versioning
- replacement semantics

### Step 4 — Biometric integration
- invoke existing ingestion/reference flow
- model-aware reference metadata
- rebuild operation
- clear failure states

### Step 5 — Admin API
- DTOs
- endpoints
- environment guard
- authorization
- rate limits/audit

### Step 6 — Admin Console page
- side navigation
- create/edit form
- upload/capture UI
- fixture list
- detail view
- status indicators
- confirmations

### Step 7 — Help and documentation
- contextual ?
- Help Center section
- configuration documentation
- troubleshooting guide

### Step 8 — Automated verification
- unit tests
- Oracle integration tests where available
- security tests
- photo tests
- biometric contract tests
- console/UI checks

---

## 26. Acceptance criteria

The feature is complete only when:
- [ ] A dedicated DEV/TEST identity fixture capability exists.
- [ ] The capability is integrated into the Admin Console side menu.
- [ ] The PDF visual theme is preserved.
- [ ] The section has its own enable/disable configuration.
- [ ] Test-data endpoints are disabled server-side outside permitted DEV/TEST environments.
- [ ] Production/pro cannot anonymously create or modify fixture data.
- [ ] Test identities are stored durably in Oracle.
- [ ] Test identities use the canonical identity-reference domain rather than a parallel customer table unless a justified fixture metadata table is required.
- [ ] The form supports required identity fields.
- [ ] National ID handling follows the existing privacy model and remains compatible with Phase 21.
- [ ] Photo upload works with server-side validation.
- [ ] Webcam capture works where browser support/permissions allow it.
- [ ] Webcam failure gracefully falls back to upload.
- [ ] Photo replacement has explicit version semantics.
- [ ] Biometric reference generation uses the existing biometric integration path.
- [ ] Biometric references remain model-aware.
- [ ] Failed biometric processing is visible and not falsely reported as successful.
- [ ] Existing provider identities cannot be silently overwritten.
- [ ] Test fixtures are usable through the normal lookup/reference path.
- [ ] Retirement integrates with existing governance/cache/audit behavior.
- [ ] Administrative operations are audited without raw PII/photo/vector data.
- [ ] Raw embedding vectors are not shown in the normal UI.
- [ ] Contextual help exists.
- [ ] Help Center documentation exists.
- [ ] Automated tests cover environment/security/storage/photo/biometric behavior.
- [ ] Maven verification passes.
- [ ] Documentation is updated.
- [ ] Implementation is committed with a focused commit message.

## Relationship to other plans

This plan is intentionally separate from the canonical numbered phases.

It should be implemented as an Admin Console capability and coordinated with:
- prompts/ADMIN-CONSOLE-V2-PLAN.md
- Phase 18 biometric integration
- Phase 18.5 storage/schema and cache architecture
- Phase 19 automated/E2E validation
- Phase 20 production hardening/security
- Phase 21 cryptographic protection
- Phase 22 final release gate

Do not renumber the canonical phases merely because this capability is added.

## Handoff

When implementation is requested:
- inspect the current repository and relevant phase reports/prompts first
- report the current domain/schema constraints before changing them
- implement only this capability and minimal prerequisites
- preserve real local Oracle/Redis configuration
- do not start unrelated phases automatically
- report changed files, schema changes, configuration flags, API endpoints, security behavior, tests, limitations and commit hash.