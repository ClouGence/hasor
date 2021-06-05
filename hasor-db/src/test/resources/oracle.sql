CREATE TABLE "DS_ENV"
(
    "ID"           NUMBER(20, 0)      NOT NULL ENABLE,
    "GMT_CREATE"   TIMESTAMP(6),
    "GMT_MODIFIED" TIMESTAMP(6),
    "OWNER_UID"    VARCHAR2(255 CHAR) NOT NULL ENABLE,
    "ENV_NAME"     VARCHAR2(64 CHAR)  NOT NULL ENABLE,
    "DESCRIPTION"  VARCHAR2(512 CHAR) DEFAULT '',
    PRIMARY KEY ("ID")
);

-- insert replace
MERGE INTO DS_ENV TMP
USING (SELECT 3            "ID",
              systimestamp GMT_CREATE,
              systimestamp GMT_MODIFIED,
              'abc'        OWNER_UID,
              'dev'        ENV_NAME,
              'dddddd'     DESCRIPTION
       FROM dual) SRC
ON (TMP."ID" = SRC."ID")
WHEN MATCHED THEN
    UPDATE
    SET "GMT_CREATE"   = SRC."GMT_CREATE",
        "GMT_MODIFIED" = SRC."GMT_MODIFIED",
        "OWNER_UID"    = SRC."OWNER_UID",
        "ENV_NAME"     = SRC."ENV_NAME",
        "DESCRIPTION"  = SRC."DESCRIPTION"
WHEN NOT MATCHED THEN
    INSERT ("ID", "GMT_CREATE", "GMT_MODIFIED", "OWNER_UID", "ENV_NAME", "DESCRIPTION")
    VALUES (SRC."ID", SRC."GMT_CREATE", SRC."GMT_MODIFIED", SRC."OWNER_UID", SRC."ENV_NAME", SRC."DESCRIPTION");

-- insert ignore
MERGE INTO DS_ENV TMP
USING (SELECT 3            "ID",
              systimestamp GMT_CREATE,
              systimestamp GMT_MODIFIED,
              'abc'        OWNER_UID,
              'dev'        ENV_NAME,
              'dddddd'     DESCRIPTION
       FROM dual) SRC
ON (TMP."ID" = SRC."ID")
WHEN NOT MATCHED THEN
    INSERT ("ID", "GMT_CREATE", "GMT_MODIFIED", "OWNER_UID", "ENV_NAME", "DESCRIPTION")
    VALUES (SRC."ID", SRC."GMT_CREATE", SRC."GMT_MODIFIED", SRC."OWNER_UID", SRC."ENV_NAME", SRC."DESCRIPTION");

-- insert block
insert INTO DS_ENV ("ID", "GMT_CREATE", "GMT_MODIFIED", "OWNER_UID", "ENV_NAME", "DESCRIPTION")
values (3, systimestamp, systimestamp, 'abc', 'dev', 'dddddd')

