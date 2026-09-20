# Phase 13 — Operations baseline

**Status: BASELINE DOCUMENTED; later controls remain phase-dependent**

Monitor L1/L2 hit rate, Oracle fallback, provider latency/errors, refresh throughput/backlog, stale population, embedding latency/failures, quota utilization and circuit-breaker state.

Controlled operations should include provider pause/resume, refresh pause/resume, targeted refresh, cache rebuild and stale/failed inspection. Administrative operations require authorization and audit.

Phase 13 implemented observability/operational visibility. Phase 16 and later phases still need to complete stronger failure recovery, governance/audit and production controls.
