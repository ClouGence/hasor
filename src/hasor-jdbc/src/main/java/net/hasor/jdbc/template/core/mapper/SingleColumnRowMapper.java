/*
 * Copyright 2002-2009 the original author or authors.
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
package net.hasor.jdbc.template.core.mapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import net.hasor.core.Hasor;
import net.hasor.jdbc.template.RowMapper;
import net.hasor.jdbc.template.core.JdbcTemplate;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014年5月23日
 * @author 赵永春 (zyc@byshell.org)
 */
public class SingleColumnRowMapper<T> implements RowMapper<T> {
    private Class<T> requiredType;
    /** Create a new SingleColumnRowMapper. */
    public SingleColumnRowMapper() {}
    /**
     * Create a new SingleColumnRowMapper.
     * @param requiredType the type that each result object is expected to match
     */
    public SingleColumnRowMapper(Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    /** Set the type that each result object is expected to match. <p>If not specified, the column value will be exposed as returned by the JDBC driver. */
    public void setRequiredType(Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    //
    /**将当前行的第一列的值转换为指定的类型。*/
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        //1.Validate column count.
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1)
            throw new SQLException("Incorrect column count: expected 1, actual " + nrOfColumns);
        //2.Extract column value from JDBC ResultSet.
        Object result = getResultSetValue(rs, 1);
        if (requiredType != null) {
            if (result != null && this.requiredType != null && !this.requiredType.isInstance(result))
                result = (T) convertValueToRequiredType(result, requiredType);
        }
        //3.Return
        return (T) result;
    }
    private static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            obj = rs.getString(index);
        } else if (className != null && ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }
    /**转换对象到制定的类型*/
    protected Object convertValueToRequiredType(Object value, Class requiredType) {
        if (String.class.equals(requiredType)) {
            return value.toString();
        } else if (Number.class.isAssignableFrom(requiredType)) {
            if (value instanceof Number) {
                // Convert original Number to target Number class.
                return convertNumberToTargetClass(((Number) value), requiredType);
            } else {
                // Convert stringified value to target Number class.
                return parseNumber(value.toString(), requiredType);
            }
        } else {
            throw new IllegalArgumentException("Value [" + value + "] is of type [" + value.getClass().getName() + "] and cannot be converted to required type [" + requiredType.getName() + "]");
        }
    }
    public static Number parseNumber(String text, Class targetClass) {
        Hasor.assertIsNotNull(number, "Number must not be null");
        Hasor.assertIsNotNull(targetClass, "Target class must not be null");
        String trimmed = StringUtils.trimAllWhitespace(text);
        if (targetClass.equals(Byte.class)) {
            return (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (targetClass.equals(Short.class)) {
            return (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (targetClass.equals(Integer.class)) {
            return (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (targetClass.equals(Long.class)) {
            return (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } else if (targetClass.equals(BigInteger.class)) {
            return (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } else if (targetClass.equals(Float.class)) {
            return Float.valueOf(trimmed);
        } else if (targetClass.equals(Double.class)) {
            return Double.valueOf(trimmed);
        } else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }
    public static Number convertNumberToTargetClass(Number number, Class targetClass) throws IllegalArgumentException {
        Hasor.assertIsNotNull(number, "Number must not be null");
        Hasor.assertIsNotNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return number;
        } else if (targetClass.equals(Byte.class)) {
            long value = number.longValue();
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return new Byte(number.byteValue());
        } else if (targetClass.equals(Short.class)) {
            long value = number.longValue();
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return new Short(number.shortValue());
        } else if (targetClass.equals(Integer.class)) {
            long value = number.longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return new Integer(number.intValue());
        } else if (targetClass.equals(Long.class)) {
            return new Long(number.longValue());
        } else if (targetClass.equals(BigInteger.class)) {
            if (number instanceof BigDecimal) {
                // do not lose precision - use BigDecimal's own conversion
                return ((BigDecimal) number).toBigInteger();
            } else {
                // original value is not a Big* number - use standard long conversion
                return BigInteger.valueOf(number.longValue());
            }
        } else if (targetClass.equals(Float.class)) {
            return new Float(number.floatValue());
        } else if (targetClass.equals(Double.class)) {
            return new Double(number.doubleValue());
        } else if (targetClass.equals(BigDecimal.class)) {
            // always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
        }
    }
    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }
    private static void raiseOverflowException(Number number, Class targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }
}