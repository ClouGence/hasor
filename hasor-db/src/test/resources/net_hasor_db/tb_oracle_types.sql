/* https://docs.oracle.com/cd/B28359_01/server.111/b28286/sql_elements001.htm */
create table tb_oracle_types
(
    c_char                       char,
    c_char_n                     char(12),
    c_char_nb                    char(12 byte),
    c_char_nc                    char(12 char),
    c_character                  character(10),
    c_character_b                character(10 byte),
    c_character_c                character(10 char),

    c_nchar_n                    nchar(12),
    c_national_character         national character(12),
    c_national_char              national char(12),

    c_varchar2                   varchar2(24),
    c_varchar2_b                 varchar2(24 byte),
    c_varchar2_c                 varchar2(24 char),
    c_character_varying          character varying(24),
    c_character_varying_b        character varying(24 byte),
    c_character_varying_c        character varying(24 char),
    c_char_varying               char varying(24),
    c_char_varying_b             char varying(24 byte),
    c_char_varying_c             char varying(24 char),
    c_national_char_varying      national char varying(24),

    c_nvarchar2                  nvarchar2(24),
    c_national_character_varying national character varying(24),
    c_nchar_varying              nchar varying(24),

    c_clob                       clob,
    c_nclob                      nclob,

    c_number                     number,
    c_number_n                   number(10),
    c_number_n_n                 number(10, 2),
    c_numeric                    numeric,
    c_numeric_n                  numeric(10),
    c_numeric_n_n                numeric(*, 2),
    c_decimal                    decimal,
    c_decimal_n                  decimal(10),
    c_decimal_n_n                decimal(10, 2),

    c_float                      float,
    c_float_n                    float(10),
    c_integer                    integer,
    c_int                        int,
    c_smallint                   smallint,
    c_double_p                   double precision,
    c_real                       real,

    c_bfloat                     binary_float,
    c_bdouble                    binary_double,

    c_date                       date,

    c_timestamp                  timestamp,
    c_timestamp_n                timestamp(9),
    c_timestamp_z                timestamp with time zone,
    c_timestamp_n_z              timestamp(9) with time zone,
    c_timestamp_lz               timestamp with local time zone,
    c_timestamp_n_lz             timestamp(9) with local time zone,

    c_interval_year              interval year to month,
    c_interval_year_n            interval year (9) to month,
    c_interval_day               interval day to second,
    c_interval_day_n1            interval day (9) to second,
    c_interval_day_n2            interval day to second (9),
    c_interval_day_n1n2          interval day (9) to second (9),

    c_raw                        raw(10),
    c_long_raw                   long raw,
    c_blob                       blob,
    c_bfile                      bfile,
    --c_long                     long,
    c_xml                        xmltype,

    c_rowid                      rowid,
    c_urowid                     urowid
)
