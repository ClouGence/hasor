/* http://www.h2database.com/html/datatypes.html */
create table tb_h2_types
(
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
    c_decimal_1     decimal(20, 10),
    c_decimal_2     decimal(20, 2),
    c_decimal_3     decimal(10),
    c_number_1      number(20, 10),
    c_number_2      number(20, 2),
    c_number_3      number(10),
    c_dec_1         dec(20, 10),
    c_dec_2         dec(20, 2),
    c_dec_3         dec(10),
    c_numeric_1     numeric(20, 10),
    c_numeric_2     numeric(20, 2),
    c_numeric_3     numeric(10),
    --
    c_double        double,
    c_double_pre    double precision,
    c_float         float,
    c_float25       float(25),
    c_float8        float8,
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
    c_timestamp_z   timestamp(9) with time zone,
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
    c_blob          blob(10 k),
    c_tinyblob      tinyblob(10 k),
    c_mediumblob    mediumblob(10 k),
    c_longblob      longblob(10 k),
    --
    c_clob          clob(10 k),
    c_tinytext      tinytext(10 k),
    c_text          text(10 k),
    c_mediumtext    mediumtext(10 k),
    c_longtext      longtext(10 k),
    c_ntext         ntext(10 k),
    c_nclob         nclob(10 k),
    --
    c_uuid          uuid,
    c_other         other,
    --
    c_array         array
);
