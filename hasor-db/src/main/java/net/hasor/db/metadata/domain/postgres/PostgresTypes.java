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
    SMALLSERIAL("smallserial", JDBCType.SMALLINT, Oid.INT2, true, false),  // smallserial、serial2
    SERIAL("serial", JDBCType.INTEGER, Oid.INT4, true, false),             // serial、serial4
    BIGSERIAL("bigserial", JDBCType.BIGINT, Oid.INT8, true, false),        // bigserial、serial8
    SMALLINT("smallint", JDBCType.SMALLINT, Oid.INT2, true, false),        // smallint、int2
    INTEGER("integer", JDBCType.INTEGER, Oid.INT4, false),                       // integer、int、int4
    BIGINT("bigint", JDBCType.BIGINT, Oid.INT8, false),                          // bigint、int8
    OID("oid", JDBCType.BIGINT, Oid.OID, false),
    NUMERIC("numeric", JDBCType.NUMERIC, Oid.NUMERIC, false),                    // numeric、decimal
    REAL("real", JDBCType.REAL, Oid.FLOAT4, false),                              // real、float4
    DOUBLE_PRECISION("double precision", JDBCType.DOUBLE, Oid.FLOAT8, false),    // double precision、float、float8
    //
    MONEY("money", JDBCType.DOUBLE, Oid.MONEY, false),
    //
    CHARACTER("character", JDBCType.CHAR, Oid.CHAR, false),                      // char、character
    BPCHAR("bpchar", JDBCType.CHAR, Oid.BPCHAR, false),
    CHARACTER_VARYING("character varying", JDBCType.VARCHAR, Oid.VARCHAR, false),// character varying、varchar
    TEXT("text", JDBCType.VARCHAR, Oid.TEXT, false),
    NAME("name", JDBCType.VARCHAR, Oid.NAME, false),
    //
    TIMESTAMP_WITHOUT_TIME_ZONE("timestamp without time zone", JDBCType.TIMESTAMP, Oid.TIMESTAMP, false),// timestamp、timestamp without time zone
    TIMESTAMP_WITH_TIME_ZONE("timestamp with time zone", JDBCType.TIMESTAMP, Oid.TIMESTAMPTZ, false),    // timestamptz、timestamp with time zone
    TIME_WITHOUT_TIME_ZONE("time without time zone", JDBCType.TIME, Oid.TIME, false),                    // time、time without time zone
    TIME_WITH_TIME_ZONE("time with time zone", JDBCType.TIME, Oid.TIMETZ, false),                        // timetz、time with time zone
    DATE("date", JDBCType.DATE, Oid.DATE, false),
    INTERVAL("interval", JDBCType.OTHER, Oid.INTERVAL, false),
    //
    BIT("bit", JDBCType.BIT, Oid.BIT, false),
    BIT_VARYING("bit varying", JDBCType.OTHER, Oid.VARBIT, false),   // varbit、bit varying
    BOOLEAN("boolean", JDBCType.BIT, Oid.BOOL, false),               // bool、boolean
    //
    XML("xml", JDBCType.SQLXML, Oid.XML, false),
    BYTEA("bytea", JDBCType.BINARY, Oid.BYTEA, false),
    REF_CURSOR("refcursor", JDBCType.REF_CURSOR, Oid.REF_CURSOR, false),// refcursor
    //
    POINT("point", JDBCType.OTHER, Oid.POINT, false),
    LINE("line", JDBCType.OTHER, null, false),
    LSEG("lseg", JDBCType.OTHER, null, false),
    BOX("box", JDBCType.OTHER, Oid.BOX, false),
    PATH("path", JDBCType.OTHER, null, false),
    POLYGON("polygon", JDBCType.OTHER, null, false),
    CIRCLE("circle", JDBCType.OTHER, null, false),
    //
    CIDR("cidr", JDBCType.OTHER, null, false),
    INET("inet", JDBCType.OTHER, null, false),
    MACADDR("macaddr", JDBCType.OTHER, null, false),
    MACADDR8("macaddr8", JDBCType.OTHER, null, false),
    //
    TSVECTOR("tsvector", JDBCType.OTHER, null, false),
    TSQUERY("tsquery", JDBCType.OTHER, null, false),
    //
    UUID("uuid", JDBCType.OTHER, Oid.UUID, false),
    //
    JSON("json", JDBCType.OTHER, Oid.JSON, false),
    JSONB("jsonb", JDBCType.OTHER, null, false),
    //
    INT4RANGE("int4range", JDBCType.OTHER, null, false),
    INT8RANGE("int8range", JDBCType.OTHER, null, false),
    NUMRANGE("numrange", JDBCType.OTHER, null, false),
    TSRANGE("tsrange", JDBCType.OTHER, null, false),
    TSTZRANGE("tstzrange", JDBCType.OTHER, null, false),
    DATERANGE("daterange", JDBCType.OTHER, null, false),
    //
    PG_LSN("pg_lsn", JDBCType.OTHER, null, false),
    TXID_SNAPSHOT("txid_snapshot", JDBCType.OTHER, null, false),
    //
    // --------------------------------------------------------------------------------------------------------------------------
    SMALLINT_ARRAY("smallint[]", JDBCType.ARRAY, Oid.INT2_ARRAY, true),                     // smallint、int2
    INTEGER_ARRAY("integer[]", JDBCType.ARRAY, Oid.INT4_ARRAY, true),                       // integer、int、int4
    BIGINT_ARRAY("bigint[]", JDBCType.ARRAY, Oid.INT8_ARRAY, true),                         // bigint、int8、
    OID_ARRAY("oid[]", JDBCType.ARRAY, Oid.OID_ARRAY, true),
    NUMERIC_ARRAY("numeric[]", JDBCType.ARRAY, Oid.NUMERIC_ARRAY, true),                    // numeric、decimal
    REAL_ARRAY("real[]", JDBCType.ARRAY, Oid.FLOAT4_ARRAY, true),                           // real、float4
    DOUBLE_PRECISION_ARRAY("double precision[]", JDBCType.ARRAY, Oid.FLOAT8_ARRAY, true),   // double precision、float、float8
    //
    MONEY_ARRAY("money[]", JDBCType.ARRAY, Oid.MONEY_ARRAY, true),
    //
    CHARACTER_ARRAY("character[]", JDBCType.ARRAY, Oid.CHAR_ARRAY, true),                   // char、character
    BPCHAR_ARRAY("bpchar[]", JDBCType.ARRAY, Oid.BPCHAR_ARRAY, true),
    CHARACTER_VARYING_ARRAY("character varying[]", JDBCType.ARRAY, Oid.VARCHAR_ARRAY, true),// character varying、varchar
    TEXT_ARRAY("text[]", JDBCType.ARRAY, Oid.TEXT_ARRAY, true),
    NAME_ARRAY("name[]", JDBCType.ARRAY, Oid.NAME_ARRAY, true),
    //
    TIMESTAMP_WITHOUT_TIME_ZONE_ARRAY("timestamp without time zone[]", JDBCType.ARRAY, Oid.TIMESTAMP_ARRAY, true),// timestamp、timestamp without time zone
    TIMESTAMP_WITH_TIME_ZONE_ARRAY("timestamp with time zone[]", JDBCType.ARRAY, Oid.TIMESTAMPTZ_ARRAY, true),    // timestamptz、timestamp with time zone
    TIME_WITHOUT_TIME_ZONE_ARRAY("time without time zone[]", JDBCType.ARRAY, Oid.TIME_ARRAY, true),               // time、time without time zone
    TIME_WITH_TIME_ZONE_ARRAY("time with time zone[]", JDBCType.ARRAY, Oid.TIMETZ_ARRAY, true),                   // timetz、time with time zone
    DATE_ARRAY("date[]", JDBCType.ARRAY, Oid.DATE_ARRAY, true),
    INTERVAL_ARRAY("interval[]", JDBCType.ARRAY, Oid.INTERVAL_ARRAY, true),
    //
    BIT_ARRAY("bit[]", JDBCType.ARRAY, Oid.BIT_ARRAY, true),
    BIT_VARYING_ARRAY("bit varying[]", JDBCType.ARRAY, Oid.VARBIT_ARRAY, true),   // varbit、bit varying
    BOOLEAN_ARRAY("boolean[]", JDBCType.ARRAY, Oid.BOOL_ARRAY, true),             // bool、boolean
    //
    XML_ARRAY("xml", JDBCType.ARRAY, Oid.XML_ARRAY, true),
    BYTEA_ARRAY("bytea[]", JDBCType.ARRAY, Oid.BYTEA_ARRAY, true),
    REF_CURSOR_ARRAY("refcursor", JDBCType.ARRAY, Oid.REF_CURSOR_ARRAY, true),    // refcursor
    //
    POINT_ARRAY("point[]", JDBCType.ARRAY, Oid.POINT_ARRAY, true),
    LINE_ARRAY("line[]", JDBCType.ARRAY, null, true),             // 629
    LSEG_ARRAY("lseg[]", JDBCType.ARRAY, null, true),             // 1018
    BOX_ARRAY("box[]", JDBCType.ARRAY, null, true),               // 1020
    PATH_ARRAY("path[]", JDBCType.ARRAY, null, true),             // 1019
    POLYGON_ARRAY("polygon[]", JDBCType.ARRAY, null, true),       // 1027
    CIRCLE_ARRAY("circle[]", JDBCType.ARRAY, null, true),         // 719
    CIDR_ARRAY("cidr[]", JDBCType.ARRAY, null, true),             // 651
    INET_ARRAY("inet[]", JDBCType.ARRAY, null, true),             // 1041
    MACADDR_ARRAY("macaddr[]", JDBCType.ARRAY, null, true),       // 1040
    MACADDR8_ARRAY("macaddr8[]", JDBCType.ARRAY, null, true),     // 775
    //
    TSVECTOR_ARRAY("tsvector[]", JDBCType.ARRAY, null, true),     // 3643
    TSQUERY_ARRAY("tsquery[]", JDBCType.ARRAY, null, true),       // 3645
    //
    UUID_ARRAY("uuid[]", JDBCType.ARRAY, Oid.UUID_ARRAY, true),         // 2951
    //
    JSON_ARRAY("json[]", JDBCType.ARRAY, Oid.JSON_ARRAY, true),
    JSONB_ARRAY("jsonb[]", JDBCType.ARRAY, Oid.JSONB_ARRAY, true),
    //
    INT4RANGE_ARRAY("int4range[]", JDBCType.ARRAY, null, true),   // 3905
    INT8RANGE_ARRAY("int8range[]", JDBCType.ARRAY, null, true),   // 3927
    NUMRANGE_ARRAY("numrange[]", JDBCType.ARRAY, null, true),     // 3907
    TSRANGE_ARRAY("tsrange[]", JDBCType.ARRAY, null, true),       // 3909
    TSTZRANGE_ARRAY("tstzrange[]", JDBCType.ARRAY, null, true),   // 3911
    DATERANGE_ARRAY("daterange[]", JDBCType.ARRAY, null, true),   // 3913
    //
    PG_LSN_ARRAY("pg_lsn[]", JDBCType.ARRAY, null, true),         // 3221
    TXID_SNAPSHOT_ARRAY("txid_snapshot[]", JDBCType.ARRAY, null, true), //2949
    ;
    private final boolean  serial;
    private final boolean  array;
    private final String   codeKey;
    private final JDBCType jdbcType;
    private final Integer  pgOid;

    PostgresTypes(String codeKey, JDBCType jdbcType, Integer pgOid, boolean array) {
        this(codeKey, jdbcType, pgOid, false, array);
    }

    PostgresTypes(String codeKey, JDBCType jdbcType, Integer pgOid, boolean serial, boolean array) {
        this.codeKey = codeKey;
        this.jdbcType = jdbcType;
        this.pgOid = pgOid;
        this.serial = serial;
        this.array = array;
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

    public boolean isArray() {
        return this.array;
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
