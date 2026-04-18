-- schema.sql
-- Run once at app startup or via Flyway migration

CREATE TABLE IF NOT EXISTS profiles (
    id              CHAR(36)        NOT NULL,           -- UUID v7, stored as string
    name            VARCHAR(100)    NOT NULL,
    gender          VARCHAR(10)     NOT NULL,           -- 'male' | 'female' | 'unknown'
    gender_prob     DECIMAL(5, 4)   NOT NULL,           -- e.g. 0.9900
    sample_size     INT             NOT NULL,
    age             INT             NOT NULL,
    age_group       VARCHAR(20)     NOT NULL,           -- 'adult' | 'child' | 'senior' etc.
    country_id      CHAR(2)         NOT NULL,           -- ISO 3166-1 alpha-2 e.g. 'NG'
    country_prob    DECIMAL(5, 4)   NOT NULL,           -- e.g. 0.8500
    created_at      DATETIME(3)     NOT NULL,           -- millisecond precision, set by Java

    PRIMARY KEY (id)
    );