-- Flyway runs this once per database and records it in the flyway_schema_history table.
-- Never edit this file after it has run: add V2__..., V3__... for later changes.

CREATE TABLE users (
    id            UUID                     PRIMARY KEY,
    email         VARCHAR(255)             NOT NULL UNIQUE,
    password_hash VARCHAR(100)             NOT NULL,
    role          VARCHAR(30)              NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);
