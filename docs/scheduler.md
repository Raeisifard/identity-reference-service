# Policy-driven refresh scheduler

The scheduler separates planning from distributed execution.

RefreshSchedulePlanner evaluates the provider policy's quiet/refresh windows and bounds a dispatch batch by the provider rate/concurrency quota. It uses an explicit clock input, making scheduling deterministic in tests.

RefreshScheduler supports dry-run planning and, for real dispatch, requires a RefreshLeaseManager. The lease implementation is intentionally left behind the SPI; distributed locking/recovery is a later milestone.

No synchronized midnight burst is assumed: production scheduling should distribute records with jitter and quotas rather than enqueueing an entire provider population at one instant.
