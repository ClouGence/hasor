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
package net.hasor.dataql.domain;
import net.hasor.dataql.runtime.operator.OperatorUtils;
import net.hasor.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 值类型结果
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueModel implements DataModel {
    public static ValueModel NULL  = new ValueModel(null);
    public static ValueModel TRUE  = new ValueModel(true);
    public static ValueModel FALSE = new ValueModel(false);
    private       Object     value = null;

    ValueModel(Object value) {
        this.value = value;
    }

    /** 判断是否为 Null */
    public boolean isNull() {
        return this.value == null;
    }

    /** 判断是否为 Number 类型值 */
    public boolean isNumber() {
        return OperatorUtils.isNumber(this.value);
    }

    /** 判断是否为 Decimal 类型值 */
    public boolean isDecimal() {
        return isFloat() || isDouble() || isBigDecimal();
    }

    /** 判断是否为 boolean 类型值 */
    public boolean isBoolean() {
        return OperatorUtils.isBoolean(this.value);
    }

    /** 转换为 boolean 值，如果为空值，那么返回false。任何整数非0值都为true */
    public boolean asBoolean() {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return false;
            }
            if ("true".equalsIgnoreCase(strVal) //
                    || "1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal) //
                    || "0".equals(strVal)) {
                return Boolean.FALSE;
            }
        }
        throw new ClassCastException("can not cast to boolean, value : " + value);
    }

    /** 判断是否为 byte 类型值 */
    public boolean isByte() {
        return OperatorUtils.isByteNumber(this.value);
    }

    /** 转换为 byte 值，如果为空值，那么返回 0 */
    public byte asByte() {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (NumberUtils.isNumber(strVal)) {
                return Byte.parseByte(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (byte) ((Boolean) value ? 1 : 0);
        }
        throw new ClassCastException("can not cast to byte, value : " + value);
    }

    /** 判断是否为 short 类型值 */
    public boolean isShort() {
        return OperatorUtils.isShortNumber(this.value);
    }

    /** 转换为 short 值，如果为空值，那么返回 0 */
    public short asShort() {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (NumberUtils.isNumber(strVal)) {
                return Short.parseShort(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (short) ((Boolean) value ? 1 : 0);
        }
        throw new ClassCastException("can not cast to short, value : " + value);
    }

    /** 判断是否为 short 类型值 */
    public boolean isInt() {
        return OperatorUtils.isIntegerNumber(this.value);
    }

    /** 转换为 int 值，如果为空值，那么返回 0 */
    public int asInt() {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            if (NumberUtils.isNumber(strVal)) {
                return Integer.parseInt(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to int, value : " + value);
    }

    /** 判断是否为 long 类型值 */
    public boolean isLong() {
        return OperatorUtils.isLongNumber(this.value);
    }

    /** 转换为 long 值，如果为空值，那么返回 0 */
    public long asLong() {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            if (NumberUtils.isNumber(strVal)) {
                return Long.parseLong(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to long, value : " + value);
    }

    /** 判断是否为 float 类型值 */
    public boolean isFloat() {
        return OperatorUtils.isFloatNumber(this.value);
    }

    /** 转换为 float 值，如果为空值，那么返回 0.0 */
    public float asFloat() {
        if (value == null) {
            return 0.0f;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0.0f;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            if (NumberUtils.isNumber(strVal)) {
                return Float.parseFloat(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to float, value : " + value);
    }

    /** 判断是否为 double 类型值 */
    public boolean isDouble() {
        return OperatorUtils.isDoubleNumber(this.value);
    }

    /** 转换为 double 值，如果为空值，那么返回 0.0 */
    public double asDouble() {
        if (value == null) {
            return 0.0d;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0.0d;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            if (NumberUtils.isNumber(strVal)) {
                return Double.parseDouble(strVal);
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to double, value : " + value);
    }

    /** 判断是否为 BigDecimal 类型值 */
    public boolean isBigDecimal() {
        return this.value instanceof BigDecimal;
    }

    /** 转换为 BigDecimal 值，如果为空值，那么返回 BigDecimal.ZERO */
    public BigDecimal asBigDecimal() {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(strVal);
        } catch (NumberFormatException e) {
            throw new ClassCastException("can not cast to BigDecimal, value : " + value);
        }
    }

    /** 判断是否为 BigInteger 类型值 */
    public boolean isBigInteger() {
        return this.value instanceof BigInteger;
    }

    /** 转换为 BigDecimal 值，如果为空值，那么返回 BigDecimal.ZERO */
    public BigInteger asBigInteger() {
        if (value == null) {
            return BigInteger.ZERO;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if (value instanceof Float || value instanceof Double) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        String strVal = value.toString();
        if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
            return BigInteger.ZERO;
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? BigInteger.ONE : BigInteger.ZERO;
        }
        try {
            return new BigInteger(strVal);
        } catch (NumberFormatException e) {
            throw new ClassCastException("can not cast to BigInteger, value : " + value);
        }
    }

    /** 判断是否为 String 类型值 */
    public boolean isString() {
        return this.value instanceof CharSequence;
    }

    /** 转换为 String 值 */
    public String asString() {
        return this.value == null ? null : this.value.toString();
    }
}