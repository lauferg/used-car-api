CREATE TABLE IF NOT EXISTS jwt_token
(
    token                  VARCHAR   NOT NULL PRIMARY KEY,
    application_user_email VARCHAR   NOT NULL UNIQUE,
    issued_time            TIMESTAMP NOT NULL DEFAULT NOW(),
    expiry_time            TIMESTAMP NOT NULL,
    is_blacklisted         BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_jwt_token_application_user FOREIGN KEY (application_user_email) REFERENCES APPLICATION_USER (EMAIL)
);