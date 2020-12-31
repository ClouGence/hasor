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
package net.hasor.db.types.handler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Clinton Begin
 */
public class ArrayTypeHandler extends AbstractTypeHandler<Object> {
    private static final ConcurrentHashMap<Class<?>, JDBCType> STANDARD_MAPPING;

    static {
        STANDARD_MAPPING = new ConcurrentHashMap<>();
        STANDARD_MAPPING.put(boolean.class, JDBCType.BOOLEAN);
        STANDARD_MAPPING.put(Boolean.class, JDBCType.BOOLEAN);
        STANDARD_MAPPING.put(byte.class, JDBCType.TINYINT);
        STANDARD_MAPPING.put(Byte.class, JDBCType.TINYINT);
        STANDARD_MAPPING.put(short.class, JDBCType.SMALLINT);
        STANDARD_MAPPING.put(Short.class, JDBCType.SMALLINT);
        STANDARD_MAPPING.put(int.class, JDBCType.INTEGER);
        STANDARD_MAPPING.put(Integer.class, JDBCType.INTEGER);
        STANDARD_MAPPING.put(long.class, JDBCType.BIGINT);
        STANDARD_MAPPING.put(Long.class, JDBCType.BIGINT);
        STANDARD_MAPPING.put(float.class, JDBCType.FLOAT);
        STANDARD_MAPPING.put(Float.class, JDBCType.FLOAT);
        STANDARD_MAPPING.put(double.class, JDBCType.DOUBLE);
        STANDARD_MAPPING.put(Double.class, JDBCType.DOUBLE);
        STANDARD_MAPPING.put(Calendar.class, JDBCType.CHAR);
        STANDARD_MAPPING.put(char.class, JDBCType.CHAR);
        // java time
        STANDARD_MAPPING.put(java.util.Date.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(java.sql.Date.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(java.sql.Time.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(Instant.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(LocalDate.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(LocalTime.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(ZonedDateTime.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(JapaneseDate.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(YearMonth.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(Year.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(Month.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(OffsetDateTime.class, JDBCType.TIMESTAMP);
        STANDARD_MAPPING.put(OffsetTime.class, JDBCType.TIMESTAMP);
        // java extensions Types
        STANDARD_MAPPING.put(String.class, JDBCType.VARCHAR);
        STANDARD_MAPPING.put(BigInteger.class, JDBCType.BIGINT);
        STANDARD_MAPPING.put(BigDecimal.class, JDBCType.NUMERIC);
        STANDARD_MAPPING.put(Byte[].class, JDBCType.VARBINARY);
        STANDARD_MAPPING.put(byte[].class, JDBCType.VARBINARY);
        STANDARD_MAPPING.put(URL.class, JDBCType.DATALINK);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JDBCType jdbcType) throws SQLException {
        if (parameter instanceof Array) {
            // it's the user's responsibility to properly free() the Array instance
            ps.setArray(i, (Array) parameter);
        } else {
            if (!parameter.getClass().isArray()) {
                throw new SQLException("ArrayType Handler requires SQL array or java array parameter and does not support type " + parameter.getClass());
            }
            Class<?> componentType = parameter.getClass().getComponentType();
            String arrayTypeName = resolveTypeName(componentType);
            Array array = ps.getConnection().createArrayOf(arrayTypeName, (Object[]) parameter);
            ps.setArray(i, array);
            array.free();
        }
    }

    protected String resolveTypeName(Class<?> type) {
        return STANDARD_MAPPING.getOrDefault(type, JDBCType.JAVA_OBJECT).getName();
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return extractArray(rs.getArray(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return extractArray(rs.getArray(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return extractArray(cs.getArray(columnIndex));
    }

    protected Object extractArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }
        Object result = array.getArray();
        array.free();
        return result;
    }
}