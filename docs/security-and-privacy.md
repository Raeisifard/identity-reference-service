# Cross-cutting security and privacy baseline

**Status: BASELINE ONLY — not a completion report for Phase 20 or 21**

National identity attributes, photos and biometric vectors are highly sensitive.

The project baseline requires service authentication/authorization, secret management, response minimization, anti-enumeration, rate limiting, safe logging, auditability, retention controls and provider contractual restrictions.

Final cryptographic protection is intentionally deferred to Phase 21. In particular, the current `national_id_ciphertext` compatibility field must not be described as actual encryption until Phase 21 defines, implements, migrates and verifies the final design.

Do not infer jurisdiction-specific legal compliance from this document; deployment governance must configure the applicable rules.
