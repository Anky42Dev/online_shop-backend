CREATE TABLE revoked_tokens (
                                id         BIGSERIAL    PRIMARY KEY,
                                token_hash VARCHAR(64)  NOT NULL UNIQUE,
                                revoked_at TIMESTAMP    NOT NULL DEFAULT now(),
                                expires_at TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_revoked_tokens_hash ON revoked_tokens(token_hash);