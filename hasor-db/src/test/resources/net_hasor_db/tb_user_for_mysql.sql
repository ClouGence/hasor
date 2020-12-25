create table tb_user
(
    userUUID      varchar(50)  not null primary key,
    name          varchar(100) null,
    loginName     varchar(100) null,
    loginPassword varchar(100) null,
    email         varchar(200) null,
    `index`       integer      null,
    registerTime  timestamp    null
);
