# Provider adapter framework

Phase 11 introduces a runtime registry and configuration boundary for provider adapters.

## Configuration

Adapters are configured under `identity-reference.provider.adapters`. Real integrations remain disabled by default.

```yaml
identity-reference:
  provider:
    real-integrations-enabled: false
    adapters:
      - type: mock-national-agency
        provider-id: mock-national-agency
        display-name: Mock National Agency
        enabled: true
```

The `type` selects an implementation family. `provider-id` is the stable identifier supplied to the acquisition request. Duplicate provider IDs are rejected.

## Mock adapter

The mock national-agency adapter is deterministic and uses synthetic fixtures only:

- `FIXTURE-FOUND-001` with birth date `1990-01-01` returns a normalized authoritative record.
- `FIXTURE-NOT-FOUND` returns NOT_FOUND.
- `FIXTURE-ERROR` returns a retryable UNAVAILABLE error.
- other fixture values return NOT_FOUND.

No real endpoint, credential, authentication scheme or external payload is included.

## Runtime registry

`IdentityProviderRegistry` indexes enabled adapters by provider ID. The acquisition service can now resolve the provider requested by each acquisition request while retaining the existing single-provider constructor for compatibility with earlier milestones.

Provider-specific transport and response mapping remain inside the adapter. Only the normalized `ProviderLookupResult` crosses the SPI boundary.

## Security

The adapter never logs lookup material and its error messages are deliberately safe. Synthetic fixture values must not be replaced with real identity data in source control.
