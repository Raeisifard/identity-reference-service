# Admin Test Console

The uploaded DOCX was usable as an implementation template. It defines a four-page console concept:

1. Monitoring & Operations
2. Administration & Development/Test Workbench
3. Governance, Configuration & Operational Controls
4. Scenario Lifecycle, Test History & Extensibility Patterns

The static console now follows that information architecture with a shared dark lifecycle sidebar, sandbox environment card, production-safety guardrail card, operator identity, sepia/cream content surface, compact metrics, provider/latency monitoring, refresh backlog, capability registry, lifecycle workspaces, locked/proposed states, and masked-data messaging.

## Navigation

- Monitoring & Operations
- Administration
- Development & Integration Testing
- Governance & Controls
- Scenario Lifecycle

Each navigation item renders a different content workspace. Disabled modules remain visible as locked items rather than silently disappearing.

## Safety model

- Sandbox mode is shown when development is enabled.
- Production-facing behavior remains read-only and guarded.
- PII masking and correlation-ID requirements are displayed as enforced controls.
- Testing endpoints are configuration-gated.
- The UI does not display raw identity payloads or credentials.
