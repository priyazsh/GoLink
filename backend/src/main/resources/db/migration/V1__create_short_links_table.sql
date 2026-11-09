CREATE TABLE short_links (
                             id UUID PRIMARY KEY,
                             code VARCHAR(16) NOT NULL UNIQUE,
                             original_url TEXT NOT NULL,
                             user_id UUID,
                             created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             expires_at TIMESTAMPTZ,
                             is_active BOOLEAN NOT NULL DEFAULT TRUE,
                             click_count BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_short_links_user_id
    ON short_links(user_id);