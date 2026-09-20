# Phase 09 — Policy-driven scheduler and quiet-window refresh

**Status: DONE**

The scheduler separates planning from distributed execution.

`RefreshSchedulePlanner` evaluates provider policy quiet/refresh windows and bounds a dispatch batch by provider rate/concurrency quota. It uses an explicit clock input, making scheduling deterministic in tests.

`RefreshScheduler` supports dry-run planning and, for real dispatch, requires a `RefreshLeaseManager`. Phase 16 will harden distributed locking, lease expiry, duplicate suppression and crash recovery.

No synchronized midnight burst is assumed: production scheduling distributes records with jitter and quotas rather than enqueueing an entire provider population at one instant.
