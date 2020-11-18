/* see http://www.h2database.com/html/datatypes.html */
create table tb_h2types (
    id              identity,
    --
    c_int           int,
    c_integer       integer,
    c_mediumint     mediumint,
    c_int4          int4,
    c_signed        signed,
    --
    c_boolean       boolean,
    c_bit           bit,
    c_bool          bool,
    --
    c_tinyint       tinyint,
    --
    c_smallint      smallint,
    c_int2          int2,
    c_year          year,
    --
    c_bigint        bigint,
    c_int8          int8,
    --
    c_decimal       decimal(20,2),
    c_number        number(10),
    --
    c_double        double,
    c_double_pre    double precision,
    c_float         float,
    c_float8        float8,
    c_float25       float(25),
    --
    c_real          real,
    c_float4        float4,
    c_float_pre     float(10),
    --
    c_time          time,
    c_time_z        time with time zone,
    c_time9         time(9),
    c_time9_z       time(9) with time zone,
    --
    c_date          date,
    c_timestamp     timestamp,
    c_datetime      datetime,
    --
    c_binary        binary(1024),
    c_varbinary     varbinary(1024),
    c_longvarbinary longvarbinary(1024),
    --
    c_varchar       varchar(255),
    c_longvarchar   longvarchar(255),
    c_varchar2      varchar2(255),
    c_nvarchar      nvarchar(255),
    c_nvarchar2     nvarchar2(255),
    --
    c_char          char(10),
    c_nchar         nchar(10),
    --
    c_blob          blob(10k),
    c_tinyblob      tinyblob(10k),
    c_mediumblob    mediumblob(10k),
    c_longblob      longblob(10k),
    --
    c_clob          clob(10k),
    c_tinytext      tinytext(10k),
    c_text          text(10k),
    c_mediumtext    mediumtext(10k),
    c_longtext      longtext(10k),
    c_ntext         ntext(10k),
    c_nclob         nclob(10k),
    --
    c_uuid          UUID,
    --
    c_array         ARRAY
);