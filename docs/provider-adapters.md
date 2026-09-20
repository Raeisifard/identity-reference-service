# Phase 11 — Provider adapter framework and first mock provider

**Status: DONE**

Phase 11 introduces a runtime registry and configuration boundary for provider adapters.

Real integrations remain disabled by default. The mock national-agency adapter uses synthetic fixtures only and returns normalized FOUND, NOT_FOUND or retryable ERROR results.

`IdentityProviderRegistry` indexes enabled adapters by provider ID. Provider-specific transport and response mapping remain inside the adapter; only the normalized `ProviderLookupResult` crosses the SPI boundary.

The adapter never logs lookup material and its error messages are deliberately safe.
