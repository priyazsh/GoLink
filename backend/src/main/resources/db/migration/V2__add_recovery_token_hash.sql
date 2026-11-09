ALTER TABLE short_links
    ADD COLUMN recovery_token_hash VARCHAR(64);

CREATE UNIQUE INDEX idx_short_links_recovery_token_hash
    ON short_links(recovery_token_hash)
    WHERE recovery_token_hash IS NOT NULL;