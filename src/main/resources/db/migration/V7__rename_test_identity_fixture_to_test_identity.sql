-- Rename the original test-fixture persistence names to the domain term "test identity".
-- V6 remains immutable; this migration preserves existing development/test data.
-- Index names are intentionally not renamed: they are database implementation details.
ALTER TABLE identity_test_fixture RENAME TO identity_test_identity;
ALTER TABLE identity_test_identity RENAME COLUMN fixture_label TO test_identity_label;
ALTER INDEX ix_identity_test_fixture_label RENAME TO ix_identity_test_identity_label;
