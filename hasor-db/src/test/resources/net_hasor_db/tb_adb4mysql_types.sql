/* Aliyun ADB For MySQL 3.0
 *
 * https://dev.mysql.com/doc/refman/5.7/en/data-types.html
 * https://dev.mysql.com/doc/refman/8.0/en/data-types.html
 * https://help.aliyun.com/document_detail/123577.html
 * https://help.aliyun.com/document_detail/197325.html
 */
create table tb_adbmysql_types
(
    c_bool                   bool,
    c_boolean                boolean,

    c_tinyint                tinyint,
    c_tinyint_n              tinyint(3),
    c_tinyint_n_z            tinyint(3) zerofill,

    c_smallint               smallint,
    c_smallint_z             smallint zerofill,
    c_smallint_n             smallint(3),
    c_smallint_n_z           smallint(3) zerofill,

    c_mediumint              mediumint,
    c_mediumint_z            mediumint zerofill,
    c_mediumint_n            mediumint(3),
    c_mediumint_n_z          mediumint(3) zerofill,

    c_int                    int,
    c_int_z                  int zerofill,
    c_int_n                  int(3),
    c_int_n_z                int(3) zerofill,

    c_integer                integer,
    c_integer_z              integer zerofill,
    c_integer_n              integer(3),
    c_integer_n_z            integer(3) zerofill,

    c_bigint                 bigint,
    c_bigint_z               bigint zerofill,
    c_bigint_n               bigint(3),
    c_bigint_n_z             bigint(3) zerofill,

    c_decimal                decimal,
    c_decimal_z              decimal zerofill,
    c_decimal_n              decimal(10),
    c_decimal_n_z            decimal(10) zerofill,
    c_decimal_n_n            decimal(10, 3),
    c_decimal_n_n_z          decimal(10, 3) zerofill,
    c_numeric                numeric,
    c_numeric_z              numeric zerofill,
    c_numeric_n              numeric(10),
    c_numeric_n_z            numeric(10) zerofill,
    c_numeric_n_n            numeric(10, 3),
    c_numeric_n_n_z          numeric(10, 3) zerofill,

    c_float                  float,
    c_float_z                float zerofill,
    c_float_n                float(10),
    c_float_n_z              float(10) zerofill,
    c_float_n_n              float(10, 3),
    c_float_n_n_z            float(10, 3) zerofill,

    c_double                 double,
    c_double_z               double zerofill,
    c_double_n_n             double(10, 3),
    c_double_n_n_z           double(10, 3) zerofill,

    c_double_precision       double precision,
    c_double_precision_z     double precision zerofill,
    c_double_precision_n_n   double precision(10, 3),
    c_double_precision_n_n_z double precision(10, 3) zerofill,

    c_real                   real,
    c_real_z                 real zerofill,
    c_real_n_n               real(10, 3),
    c_real_n_n_z             real(10, 3) zerofill,

    c_date                   date,
    c_datetime               datetime,
    c_datetime_n             datetime(6),
    c_timestamp              timestamp,
    c_timestamp_n            timestamp(9),
    c_time                   time,
    c_time_n                 time(6),
    c_year                   year,
    c_year_n                 year(4),

    c_char                   char,
    c_char_n                 char(10),

    c_varchar                varchar,
    c_string                 string,
    c_varchar_n              varchar(10),

    c_binary                 binary,
    c_binary_n               binary(10),
    c_varbinary              varbinary(10),

    c_blob                   blob,
    c_blob_n                 blob(12),

    c_clob                   clob,
    c_tinytext               tinytext,
    c_text                   text,
    c_text_n                 text(12),
    c_mediumtext             mediumtext,
    c_longtext               longtext,

    c_enum                   enum ('enum1','enum2','enum3','enum4','enum5','enum6','enum7','enum8'),
    c_set                    set ('set1','set2','set3','set4','set5','set6','set7','set8'),
    c_json                   json,

    c_point                  point
)
