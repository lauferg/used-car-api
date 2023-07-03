CREATE TABLE IF NOT EXISTS application_user
(
    email        VARCHAR(50) PRIMARY KEY,
    name VARCHAR UNIQUE,

    CONSTRAINT ck_email_plausible CHECK (email REGEXP '\w+@\w+\.\w+')
);