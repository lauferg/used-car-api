CREATE TABLE IF NOT EXISTS application_user
(
    email VARCHAR PRIMARY KEY,
    name  VARCHAR(50),

    CONSTRAINT ck_email_plausible CHECK (email REGEXP '\w+@\w+\.\w+')
);