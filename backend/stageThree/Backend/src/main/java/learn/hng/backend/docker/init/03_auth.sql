CREATE TABLE users (
                       id              VARCHAR(36)  PRIMARY KEY,
                       github_id       VARCHAR(255) NOT NULL UNIQUE,
                       username        VARCHAR(255) NOT NULL,
                       email           VARCHAR(255),
                       avatar_url      VARCHAR(500),
                       role            VARCHAR(20)  NOT NULL DEFAULT 'analyst',
                       is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
                       last_login_at   TIMESTAMP,
                       created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
                                id          VARCHAR(36)  PRIMARY KEY,
                                token       VARCHAR(36)  NOT NULL UNIQUE,
                                user_id     VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                expires_at  TIMESTAMP    NOT NULL,
                                revoked     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE pkce_states (
                             state           VARCHAR(64)  PRIMARY KEY,
                             code_verifier   VARCHAR(128) NOT NULL,
                             expires_at      TIMESTAMP    NOT NULL
);