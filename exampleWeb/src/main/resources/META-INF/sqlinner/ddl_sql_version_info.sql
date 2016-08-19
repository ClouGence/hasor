CREATE TABLE VersionInfo (
  id             INTEGER PRIMARY KEY NOT NULL IDENTITY,
  version        VARCHAR(20)         NOT NULL,
  releaseDate    VARCHAR(50)         NOT NULL,
  downloadURL    VARCHAR(300)        NOT NULL,
  apiURL         VARCHAR(300)        NOT NULL,
  downloadApiURL VARCHAR(300)        NOT NULL
);