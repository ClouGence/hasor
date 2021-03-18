create table TB_USER
(
    "USERUUID"      VARCHAR2(50) not null primary key,
    "NAME"          VARCHAR2(100),
    "LOGINNAME"     VARCHAR2(100),
    "LOGINPASSWORD" VARCHAR2(100),
    "EMAIL"         VARCHAR2(200),
    "INDEX"         NUMBER,
    "REGISTERTIME"  TIMESTAMP(6)
)
