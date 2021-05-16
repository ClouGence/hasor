/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.metadata.domain.postgres;
import net.hasor.db.metadata.SqlType;
import net.hasor.db.metadata.domain.postgres.driver.Oid;

import java.sql.JDBCType;

/**
 * <li>https://www.postgresql.org/docs/13/index.html</li>
 * <li>org.postgresql.jdbc.TypeInfoCache</li>
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public enum PostgresTypes implements SqlType {
    SMALLSERIAL("smallserial", JDBCType.SMALLINT, Oid.INT2, true),  // smallserial、serial2
    SERIAL("serial", JDBCType.INTEGER, Oid.INT4, true),             // serial、serial4
    BIGSERIAL("bigserial", JDBCType.BIGINT, Oid.INT8, true),        // bigserial、serial8
    SMALLINT("smallint", JDBCType.SMALLINT, Oid.INT2, true),        // smallint、int2
    INTEGER("integer", JDBCType.INTEGER, Oid.INT4),                       // integer、int、int4
    BIGINT("bigint", JDBCType.BIGINT, Oid.INT8),                          // bigint、int8
    OID("oid", JDBCType.BIGINT, Oid.OID),
    NUMERIC("numeric", JDBCType.NUMERIC, Oid.NUMERIC),                    // numeric、decimal
    REAL("real", JDBCType.REAL, Oid.FLOAT4),                              // real、float4
    DOUBLE_PRECISION("double precision", JDBCType.DOUBLE, Oid.FLOAT8),    // double precision、float、float8
    //
    MONEY("money", JDBCType.DOUBLE, Oid.MONEY),
    //
    CHARACTER("character", JDBCType.CHAR, Oid.CHAR),                      // char、character
    BPCHAR("bpchar", JDBCType.CHAR, Oid.BPCHAR),
    CHARACTER_VARYING("character varying", JDBCType.VARCHAR, Oid.VARCHAR),// character varying、varchar
    TEXT("text", JDBCType.VARCHAR, Oid.TEXT),
    NAME("name", JDBCType.VARCHAR, Oid.NAME),
    //
    TIMESTAMP_WITHOUT_TIME_ZONE("timestamp without time zone", JDBCType.TIMESTAMP, Oid.TIMESTAMP),// timestamp、timestamp without time zone
    TIMESTAMP_WITH_TIME_ZONE("timestamp with time zone", JDBCType.TIMESTAMP, Oid.TIMESTAMPTZ),    // timestamptz、timestamp with time zone
    TIME_WITHOUT_TIME_ZONE("time without time zone", JDBCType.TIME, Oid.TIME),                    // time、time without time zone
    TIME_WITH_TIME_ZONE("time with time zone", JDBCType.TIME, Oid.TIMETZ),                        // timetz、time with time zone
    DATE("date", JDBCType.DATE, Oid.DATE),
    INTERVAL("interval", JDBCType.OTHER, Oid.INTERVAL),
    //
    BIT("bit", JDBCType.BIT, Oid.BIT),
    BIT_VARYING("bit varying", JDBCType.OTHER, Oid.VARBIT),   // varbit、bit varying
    BOOLEAN("boolean", JDBCType.BIT, Oid.BOOL),               // bool、boolean
    //
    XML("xml", JDBCType.SQLXML, Oid.XML),
    BYTEA("bytea", JDBCType.BINARY, Oid.BYTEA),
    REF_CURSOR("refcursor", JDBCType.REF_CURSOR, Oid.REF_CURSOR),// refcursor
    //
    POINT("point", JDBCType.OTHER, Oid.POINT),
    LINE("line", JDBCType.OTHER, null),
    LSEG("lseg", JDBCType.OTHER, null),
    BOX("box", JDBCType.OTHER, Oid.BOX),
    PATH("path", JDBCType.OTHER, null),
    POLYGON("polygon", JDBCType.OTHER, null),
    CIRCLE("circle", JDBCType.OTHER, null),
    //
    CIDR("cidr", JDBCType.OTHER, null),
    INET("inet", JDBCType.OTHER, null),
    MACADDR("macaddr", JDBCType.OTHER, null),
    MACADDR8("macaddr8", JDBCType.OTHER, null),
    //
    TSVECTOR("tsvector", JDBCType.OTHER, null),
    TSQUERY("tsquery", JDBCType.OTHER, null),
    //
    UUID("uuid", JDBCType.OTHER, Oid.UUID),
    //
    JSON("json", JDBCType.OTHER, Oid.JSON),
    JSONB("jsonb", JDBCType.OTHER, null),
    //
    INT4RANGE("int4range", JDBCType.OTHER, null),
    INT8RANGE("int8range", JDBCType.OTHER, null),
    NUMRANGE("numrange", JDBCType.OTHER, null),
    TSRANGE("tsrange", JDBCType.OTHER, null),
    TSTZRANGE("tstzrange", JDBCType.OTHER, null),
    DATERANGE("daterange", JDBCType.OTHER, null),
    //
    PG_LSN("pg_lsn", JDBCType.OTHER, null),
    TXID_SNAPSHOT("txid_snapshot", JDBCType.OTHER, null),
    //
    // --------------------------------------------------------------------------------------------------------------------------
    SMALLINT_ARRAY("smallint[]", JDBCType.ARRAY, Oid.INT2_ARRAY),                     // smallint、int2
    INTEGER_ARRAY("integer[]", JDBCType.ARRAY, Oid.INT4_ARRAY),                       // integer、int、int4
    BIGINT_ARRAY("bigint[]", JDBCType.ARRAY, Oid.INT8_ARRAY),                         // bigint、int8、
    OID_ARRAY("oid[]", JDBCType.ARRAY, Oid.OID_ARRAY),
    NUMERIC_ARRAY("numeric[]", JDBCType.ARRAY, Oid.NUMERIC_ARRAY),                    // numeric、decimal
    REAL_ARRAY("real[]", JDBCType.ARRAY, Oid.FLOAT4_ARRAY),                           // real、float4
    DOUBLE_PRECISION_ARRAY("double precision[]", JDBCType.ARRAY, Oid.FLOAT8_ARRAY),   // double precision、float、float8
    //
    MONEY_ARRAY("money[]", JDBCType.ARRAY, Oid.MONEY_ARRAY),
    //
    CHARACTER_ARRAY("character[]", JDBCType.ARRAY, Oid.CHAR_ARRAY),                   // char、character
    BPCHAR_ARRAY("bpchar[]", JDBCType.ARRAY, Oid.BPCHAR_ARRAY),
    CHARACTER_VARYING_ARRAY("character varying[]", JDBCType.ARRAY, Oid.VARCHAR_ARRAY),// character varying、varchar
    TEXT_ARRAY("text[]", JDBCType.ARRAY, Oid.TEXT_ARRAY),
    NAME_ARRAY("name[]", JDBCType.ARRAY, Oid.NAME_ARRAY),
    //
    TIMESTAMP_WITHOUT_TIME_ZONE_ARRAY("timestamp without time zone[]", JDBCType.ARRAY, Oid.TIMESTAMP_ARRAY),// timestamp、timestamp without time zone
    TIMESTAMP_WITH_TIME_ZONE_ARRAY("timestamp with time zone[]", JDBCType.ARRAY, Oid.TIMESTAMPTZ_ARRAY),    // timestamptz、timestamp with time zone
    TIME_WITHOUT_TIME_ZONE_ARRAY("time without time zone[]", JDBCType.ARRAY, Oid.TIME_ARRAY),               // time、time without time zone
    TIME_WITH_TIME_ZONE_ARRAY("time with time zone[]", JDBCType.ARRAY, Oid.TIMETZ_ARRAY),                   // timetz、time with time zone
    DATE_ARRAY("date[]", JDBCType.ARRAY, Oid.DATE_ARRAY),
    INTERVAL_ARRAY("interval[]", JDBCType.ARRAY, Oid.INTERVAL_ARRAY),
    //
    BIT_ARRAY("bit[]", JDBCType.ARRAY, Oid.BIT_ARRAY),
    BIT_VARYING_ARRAY("bit varying[]", JDBCType.ARRAY, Oid.VARBIT_ARRAY),   // varbit、bit varying
    BOOLEAN_ARRAY("boolean[]", JDBCType.ARRAY, Oid.BOOL_ARRAY),             // bool、boolean
    //
    XML_ARRAY("xml", JDBCType.ARRAY, Oid.XML_ARRAY),
    BYTEA_ARRAY("bytea[]", JDBCType.ARRAY, Oid.BYTEA_ARRAY),
    REF_CURSOR_ARRAY("refcursor", JDBCType.ARRAY, Oid.REF_CURSOR_ARRAY),    // refcursor
    //
    POINT_ARRAY("point[]", JDBCType.ARRAY, Oid.POINT_ARRAY),
    LINE_ARRAY("line[]", JDBCType.ARRAY, null),             // 629
    LSEG_ARRAY("lseg[]", JDBCType.ARRAY, null),             // 1018
    BOX_ARRAY("box[]", JDBCType.ARRAY, null),               // 1020
    PATH_ARRAY("path[]", JDBCType.ARRAY, null),             // 1019
    POLYGON_ARRAY("polygon[]", JDBCType.ARRAY, null),       // 1027
    CIRCLE_ARRAY("circle[]", JDBCType.ARRAY, null),         // 719
    CIDR_ARRAY("cidr[]", JDBCType.ARRAY, null),             // 651
    INET_ARRAY("inet[]", JDBCType.ARRAY, null),             // 1041
    MACADDR_ARRAY("macaddr[]", JDBCType.ARRAY, null),       // 1040
    MACADDR8_ARRAY("macaddr8[]", JDBCType.ARRAY, null),     // 775
    //
    TSVECTOR_ARRAY("tsvector[]", JDBCType.ARRAY, null),     // 3643
    TSQUERY_ARRAY("tsquery[]", JDBCType.ARRAY, null),       // 3645
    //
    UUID_ARRAY("uuid[]", JDBCType.ARRAY, Oid.UUID_ARRAY),         // 2951
    //
    JSON_ARRAY("json[]", JDBCType.ARRAY, Oid.JSON_ARRAY),
    JSONB_ARRAY("jsonb[]", JDBCType.ARRAY, Oid.JSONB_ARRAY),
    //
    INT4RANGE_ARRAY("int4range[]", JDBCType.ARRAY, null),   // 3905
    INT8RANGE_ARRAY("int8range[]", JDBCType.ARRAY, null),   // 3927
    NUMRANGE_ARRAY("numrange[]", JDBCType.ARRAY, null),     // 3907
    TSRANGE_ARRAY("tsrange[]", JDBCType.ARRAY, null),       // 3909
    TSTZRANGE_ARRAY("tstzrange[]", JDBCType.ARRAY, null),   // 3911
    DATERANGE_ARRAY("daterange[]", JDBCType.ARRAY, null),   // 3913
    //
    PG_LSN_ARRAY("pg_lsn[]", JDBCType.ARRAY, null),         // 3221
    TXID_SNAPSHOT_ARRAY("txid_snapshot[]", JDBCType.ARRAY, null), //2949
    ;
    private final boolean  serial;
    private final String   codeKey;
    private final JDBCType jdbcType;
    private final Integer  pgOid;

    PostgresTypes(String codeKey, JDBCType jdbcType, Integer pgOid) {
        this(codeKey, jdbcType, pgOid, false);
    }

    PostgresTypes(String codeKey, JDBCType jdbcType, Integer pgOid, boolean serial) {
        this.codeKey = codeKey;
        this.jdbcType = jdbcType;
        this.pgOid = pgOid;
        this.serial = serial;
    }

    public static PostgresTypes valueOfCode(String code) {
        for (PostgresTypes tableType : PostgresTypes.values()) {
            if (tableType.codeKey.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }

    public static PostgresTypes valueOfTypeOid(Number pgOid) {
        if (pgOid == null) {
            return null;
        }
        for (PostgresTypes tableType : PostgresTypes.values()) {
            if (!tableType.serial && tableType.pgOid != null && tableType.pgOid.equals(pgOid.intValue())) {
                return tableType;
            }
        }
        return null;
    }

    public boolean isSerial() {
        return this.serial;
    }

    @Override
    public String getCodeKey() {
        return this.codeKey;
    }

    @Override
    public Integer getJdbcType() {
        return this.jdbcType.getVendorTypeNumber();
    }

    @Override
    public JDBCType toJDBCType() {
        return this.jdbcType;
    }

    public Integer getPgOid() {
        return this.pgOid;
    }
}
