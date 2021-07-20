create table test_user
(
    user_uuid      varchar(50) not null primary key,
    name           varchar(100) null,
    login_name     varchar(100) null,
    login_password varchar(100) null,
    email          varchar(200) null,
    `index`        integer null,
    register_time  timestamp null
);