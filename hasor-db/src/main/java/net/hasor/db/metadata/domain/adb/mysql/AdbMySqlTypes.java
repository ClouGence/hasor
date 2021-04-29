/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package net.hasor.db.metadata.domain.adb.mysql;
import net.hasor.db.metadata.SqlType;

import java.sql.JDBCType;

/**
 * <li>https://dev.mysql.com/doc/refman/5.7/en/data-types.html</li>
 * <li>https://dev.mysql.com/doc/refman/8.0/en/data-types.html</li>
 * <li>https://help.aliyun.com/document_detail/123577.html</li>
 * @version : 2021-04-01
 * @author 赵永春 (zyc@hasor.net)
 */
public enum AdbMySqlTypes implements SqlType {
    /**
     * BOOLEAN The last two statements display the results shown because 2 is equal to neither 1 nor 0.
     */
    BOOLEAN("BOOLEAN", JDBCType.BOOLEAN),
    /**
     * TINYINT[(M)] [ZEROFILL] A very small integer. The signed range is -128 to 127. The unsigned range is 0 to 255.
     */
    TINYINT("TINYINT", JDBCType.TINYINT),
    /**
     * SMALLINT[(M)] [ZEROFILL] A small integer. The signed range is -32768 to 32767. The unsigned range is 0 to 65535.
     */
    SMALLINT("SMALLINT", JDBCType.SMALLINT),
    /**
     * INT[(M)] [ZEROFILL] A normal-size integer. The signed range is -2147483648 to 2147483647. The unsigned range is 0 to 4294967295.
     */
    INT("INT", JDBCType.INTEGER),
    /**
     * BIGINT[(M)] [UNSIGNED] [ZEROFILL] A large integer. The signed range is -9223372036854775808 to 9223372036854775807. The unsigned range is 0 to 18446744073709551615.
     */
    BIGINT("BIGINT", JDBCType.BIGINT),
    /**
     * DECIMAL[(M[,D])] [ZEROFILL] A packed “exact” fixed-point number. M is the total number of digits (the precision) and D is the number of digits after the decimal point (the scale). The decimal
     * point and (for negative numbers) the - sign are not counted in M. If D is 0, values have no decimal point or fractional part. The maximum number of digits (M) for DECIMAL is 65. The maximum
     * number of supported decimals (D) is 30. If D is omitted, the default is 0. If M is omitted, the default is 10. (There is also a limit on how long the text of DECIMAL literals can be; see
     * Section 12.22.3, “Expression Handling”.)
     */
    DECIMAL("DECIMAL", JDBCType.DECIMAL),
    /**
     * FLOAT[(M,D)] [ZEROFILL] A small (single-precision) floating-point number. Permissible values are -3.402823466E+38 to -1.175494351E-38, 0, and 1.175494351E-38 to 3.402823466E+38. These are the
     * theoretical limits, based on the IEEE standard. The actual range might be slightly smaller depending on your hardware or operating system. M is the total number of digits and D is the number of
     * digits following the decimal point. If M and D are omitted, values are stored to the limits permitted by the hardware. A single-precision floating-point number is accurate to approximately 7
     * decimal places.
     */
    FLOAT("FLOAT", JDBCType.REAL),
    /**
     * DOUBLE[(M,D)] [ZEROFILL] A normal-size (double-precision) floating-point number. Permissible values are -1.7976931348623157E+308 to -2.2250738585072014E-308, 0, and 2.2250738585072014E-308 to
     * 1.7976931348623157E+308. These are the theoretical limits, based on the IEEE standard. The actual range might be slightly smaller depending on your hardware or operating system. M is the total
     * number of digits and D is the number of digits following the decimal point. If M and D are omitted, values are stored to the limits permitted by the hardware. A double-precision floating-point
     * number is accurate to approximately 15 decimal places.
     */
    DOUBLE("DOUBLE", JDBCType.DOUBLE),
    // Numeric Data Types
    /**
     * DATE A date. The supported range is '1000-01-01' to '9999-12-31'. MySQL displays DATE values in 'YYYY-MM-DD' format, but permits assignment of values to DATE columns using either strings or
     * numbers.
     */
    DATE("DATE", JDBCType.DATE),
    /**
     * DATETIME[(fsp)] A date and time combination. The supported range is '1000-01-01 00:00:00.000000' to '9999-12-31 23:59:59.999999'. MySQL displays DATETIME values in 'YYYY-MM-DD
     * hh:mm:ss[.fraction]' format, but permits assignment of values to DATETIME columns using either strings or numbers. An optional fsp value in the range from 0 to 6 may be given to specify
     * fractional seconds precision. A value of 0 signifies that there is no fractional part. If omitted, the default precision is 0.
     */
    DATETIME("DATETIME", JDBCType.TIMESTAMP),
    /**
     * TIMESTAMP[(fsp)] A timestamp. The range is '1970-01-01 00:00:01.000000' UTC to '2038-01-19 03:14:07.999999' UTC. TIMESTAMP values are stored as the number of seconds since the epoch
     * ('1970-01-01 00:00:00' UTC). A TIMESTAMP cannot represent the value '1970-01-01 00:00:00' because that is equivalent to 0 seconds from the epoch and the value 0 is reserved for representing
     * '0000-00-00 00:00:00', the “zero” TIMESTAMP value. An optional fsp value in the range from 0 to 6 may be given to specify fractional seconds precision. A value of 0 signifies that there is no
     * fractional part. If omitted, the default precision is 0.
     */
    TIMESTAMP("TIMESTAMP", JDBCType.TIMESTAMP),
    /**
     * TIME[(fsp)] A time. The range is '-838:59:59.000000' to '838:59:59.000000'. MySQL displays TIME values in 'hh:mm:ss[.fraction]' format, but permits assignment of values to TIME columns using
     * either strings or numbers. An optional fsp value in the range from 0 to 6 may be given to specify fractional seconds precision. A value of 0 signifies that there is no fractional part. If
     * omitted, the default precision is 0.
     */
    TIME("TIME", JDBCType.BINARY),
    // String Data Types
    /**
     * The CHAR and VARCHAR types are similar, but differ in the way they are stored and retrieved. They also differ in maximum length and in whether trailing spaces are retained. VARCHAR is shorthand
     * for CHARACTER VARYING.
     */
    VARCHAR("VARCHAR", JDBCType.VARCHAR),
    /**
     * BINARY[(M)] The VARBINARY type is similar to the VARCHAR type, but stores binary byte strings rather than nonbinary character strings. M represents the maximum column length in bytes.
     */
    BINARY("BINARY", JDBCType.BINARY),
    /**
     * MySQL supports a native JSON data type defined by RFC 7159 that enables efficient access to data in JSON (JavaScript Object Notation) documents. The JSON data type provides these advantages
     * over storing JSON-format strings in a string column:
     */
    JSON("JSON", JDBCType.LONGVARCHAR),
    /** */
    Array("ARRAY", JDBCType.ARRAY),
    /** */
    Map("MAP", JDBCType.STRUCT),
    /**
     * POINT MySQL has spatial data types that correspond to OpenGIS classes. The basis for these types is described in Section 11.4.2, “The OpenGIS Geometry Model”.
     */
    POINT("POINT", JDBCType.BINARY);
    private final String   codeKey;
    private final JDBCType jdbcType;

    AdbMySqlTypes(String codeKey, JDBCType jdbcType) {
        this.codeKey = codeKey;
        this.jdbcType = jdbcType;
    }

    public String getCodeKey() {
        return this.codeKey;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }
}
