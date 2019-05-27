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
package net.hasor.db.jdbc.mapper;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
/**
 *
 * @version : 2014年5月23日
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {
    /**获取列的值*/
    protected static Object getResultSetValue(final ResultSet rs, final int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        //
        if (obj instanceof Blob) {
            /*Blob 转换为 Bytes*/
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            /*Clob 转换为 String*/
            obj = rs.getString(index);
        } else if (className != null && ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))) {
            /*oracle TIMESTAMP 转换为 Timestamp*/
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            /*oracle DATE 转换为 Date*/
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            /*DATE 转换 Date*/
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }
    /**转换为单值的类型*/
    protected static Object convertValueToRequiredType(final Object value, final Class<?> requiredType) {
        if (value == null) {
            return BeanUtils.getDefaultValue(requiredType);
        }
        if (String.class.equals(requiredType)) {
            return value.toString();
        } else if (Number.class.isAssignableFrom(requiredType) || isNumberPrimitive(requiredType)) {
            if (value instanceof Number || isNumberPrimitive(requiredType)) {
                return AbstractRowMapper.convertNumberToTargetClass((Number) value, requiredType);
            } else {
                return AbstractRowMapper.parseNumber(value.toString(), requiredType);
            }
        } else {
            return ConverterUtils.convert(requiredType, value);
        }
    }
    private static boolean isNumberPrimitive(Class<?> requiredType) {
        return Byte.TYPE == requiredType ||//
                Short.TYPE == requiredType ||//
                Integer.TYPE == requiredType ||//
                Long.TYPE == requiredType ||//
                Float.TYPE == requiredType ||//
                Double.TYPE == requiredType;
    }
    /**
     * Parse the given text into a number instance of the given target class, using the corresponding <code>decode</code> / <code>valueOf</code> methods.
     * <p>Trims the input <code>String</code> before attempting to parse the number. Supports numbers in hex format (with leading "0x", "0X" or "#") as well.
     * @param text the text to convert
     * @param targetClass the target class to parse into
     * @return the parsed number
     * @throws IllegalArgumentException if the target class is not supported (i.e. not a standard Number subclass as included in the JDK)
     * @see java.lang.Byte#decode
     * @see java.lang.Short#decode
     * @see java.lang.Integer#decode
     * @see java.lang.Long#decode
     * @see #decodeBigInteger(String)
     * @see java.lang.Float#valueOf
     * @see java.lang.Double#valueOf
     * @see java.math.BigDecimal#BigDecimal(String)
     */
    private static Number parseNumber(final String text, final Class<?> targetClass) {
        Objects.requireNonNull(text, "Text must not be null");
        Objects.requireNonNull(targetClass, "Target class must not be null");
        String trimmed = text.trim();
        if (targetClass.equals(Byte.class)) {
            return AbstractRowMapper.isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed);
        } else if (targetClass.equals(Short.class)) {
            return AbstractRowMapper.isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed);
        } else if (targetClass.equals(Integer.class)) {
            return AbstractRowMapper.isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed);
        } else if (targetClass.equals(Long.class)) {
            return AbstractRowMapper.isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed);
        } else if (targetClass.equals(BigInteger.class)) {
            return AbstractRowMapper.isHexNumber(trimmed) ? AbstractRowMapper.decodeBigInteger(trimmed) : new BigInteger(trimmed);
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
    /**
     * Convert the given number into an instance of the given target class.
     * @param number the number to convert
     * @param targetClass the target class to convert to
     * @return the converted number
     * @throws IllegalArgumentException if the target class is not supported (i.e. not a standard Number subclass as included in the JDK)
     * @see java.lang.Byte
     * @see java.lang.Short
     * @see java.lang.Integer
     * @see java.lang.Long
     * @see java.math.BigInteger
     * @see java.lang.Float
     * @see java.lang.Double
     * @see java.math.BigDecimal
     */
    private static Number convertNumberToTargetClass(final Number number, final Class<?> targetClass) throws IllegalArgumentException {
        Objects.requireNonNull(number, "Number must not be null");
        Objects.requireNonNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return number;
        } else if (targetClass.equals(Byte.class) || targetClass.equals(Byte.TYPE)) {
            long value = number.longValue();
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                AbstractRowMapper.raiseOverflowException(number, targetClass);
            }
            return new Byte(number.byteValue());
        } else if (targetClass.equals(Short.class) || targetClass.equals(Short.TYPE)) {
            long value = number.longValue();
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                AbstractRowMapper.raiseOverflowException(number, targetClass);
            }
            return new Short(number.shortValue());
        } else if (targetClass.equals(Integer.class) || targetClass.equals(Integer.TYPE)) {
            long value = number.longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                AbstractRowMapper.raiseOverflowException(number, targetClass);
            }
            return new Integer(number.intValue());
        } else if (targetClass.equals(Long.class) || targetClass.equals(Long.TYPE)) {
            return new Long(number.longValue());
        } else if (targetClass.equals(BigInteger.class)) {
            if (number instanceof BigDecimal) {
                // do not lose precision - use BigDecimal's own conversion
                return ((BigDecimal) number).toBigInteger();
            } else {
                // original value is not a Big* number - use standard long conversion
                return BigInteger.valueOf(number.longValue());
            }
        } else if (targetClass.equals(Float.class) || targetClass.equals(Float.TYPE)) {
            return new Float(number.floatValue());
        } else if (targetClass.equals(Double.class) || targetClass.equals(Double.TYPE)) {
            return new Double(number.doubleValue());
        } else if (targetClass.equals(BigDecimal.class)) {
            // always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + //
                    number.getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
        }
    }
    /**
     * Determine whether the given value String indicates a hex number, i.e. needs to be passed into 
     * <code>Integer.decode</code> instead of <code>Integer.valueOf</code> (etc).
     */
    private static boolean isHexNumber(final String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }
    /**
     * Raise an overflow exception for the given number and target class.
     * @param number the number we tried to convert
     * @param targetClass the target class we tried to convert to
     */
    private static void raiseOverflowException(final Number number, final Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }
    /**
     * Decode a {@link java.math.BigInteger} from a {@link String} value. Supports decimal, hex and octal notation.
     * @see BigInteger#BigInteger(String, int)
     */
    private static BigInteger decodeBigInteger(final String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }
        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return negative ? result.negate() : result;
    }
}