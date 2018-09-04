CREATE TABLE UserInfo (
  id             INTEGER PRIMARY KEY NOT NULL IDENTITY,
  account        VARCHAR(20)         NOT NULL,
  password       VARCHAR(50)         NOT NULL,
  nick           VARCHAR(300)        NOT NULL,
  create_time    TIMESTAMP           NOT NULL,
  modify_time    TIMESTAMP           NOT NULL
);