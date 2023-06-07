CREATE TABLE customer
(
    id      BIGINT          NOT NULL        PRIMARY KEY,
    name    VARCHAR(255),
    age     INT          DEFAULT NULL,
    year    VARCHAR(255)
);

CREATE TABLE customer2
(
    id      BIGINT          NOT NULL        PRIMARY KEY,
    name    VARCHAR(255),
    age     INT          DEFAULT NULL,
    year    VARCHAR(255)
);