CREATE TABLE IF NOT EXISTS ad
(
    id                     BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    application_user_email VARCHAR     NOT NULL,
    make                   VARCHAR(20) NOT NULL,
    type                   VARCHAR(20) NOT NULL,
    description            VARCHAR(200),
    price                  BIGINT      NOT NULL,

    CONSTRAINT fk_application_user_email FOREIGN KEY (application_user_email) REFERENCES APPLICATION_USER (EMAIL)
);