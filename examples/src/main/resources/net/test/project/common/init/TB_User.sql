create table TB_User (
   userUUID             nvarchar(50)         not null,
   name                 nvarchar(100)        null,
   loginName            nvarchar(100)        not null,
   loginPassword        nvarchar(100)        null,
   email                nvarchar(200)        null,
   registerTime         datetime             not null,
   constraint PK_TB_User primary key (userUUID)
);
