# Observability and operational controls

Phase 13 adds operational visibility without logging identity, biometric, credential, or provider payload data.

## Observability
- Micrometer counters and timers cover identity lookups, rate limiting, and accepted administrative refresh requests.
- Spring Boot Actuator exposes health, metrics, and Prometheus endpoints.
- ProviderHealthIndicator reports whether at least one configured identity provider is available.
- X-Correlation-Id is accepted or generated for every HTTP request and returned in the response.
- Log4j2 includes the correlation ID and emits request fields as structured key/value output.

## Operational controls
Request logging can be disabled without code changes:

    identity-reference:
      operational:
        request-logging-enabled: true

Environment override:

    IDENTITY_REQUEST_LOGGING_ENABLED=false

Request logs contain method, URI path, HTTP status, duration and correlation ID only. They do not log request bodies, national IDs, birth dates, photographs, embeddings, credentials, or provider responses.

## Health
The aggregate health endpoint remains /actuator/health. The identity provider health component is exposed as identityProviders.

## Metrics
Custom metrics use the identity_reference_* namespace:
- identity_reference_lookup_total
- identity_reference_lookup_duration
- identity_reference_api_rate_limited_total
- identity_reference_refresh_total

Provider IDs are used only as bounded metric tags and are never populated from identity data.