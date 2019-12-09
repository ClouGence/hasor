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

    @Override
    public Object asOri() {
        return this.value;
    }

    @Override
    public Object unwrap() {
        return this.value;
    }

    /** 判断是否为 ValueModel 类型值 */
    public boolean isValueModel() {
        return true;
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
        if (this.value == null) {
            return false;
        }
        if (this.value instanceof Boolean) {
            return (Boolean) this.value;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).intValue() != 0;
        }
        if (this.value instanceof String) {
            String strVal = (String) this.value;
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
        throw new ClassCastException("can not cast to boolean, value : " + this.value);
    }

    /** 判断是否为 byte 类型值 */
    public boolean isByte() {
        return OperatorUtils.isByteNumber(this.value);
    }

    /** 转换为 byte 值，如果为空值，那么返回 0 */
    public byte asByte() {
        if (this.value == null) {
            return 0;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).byteValue();
        }
        if (this.value instanceof String) {
            String strVal = (String) this.value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (NumberUtils.isNumber(strVal)) {
                return Byte.parseByte(strVal);
            }
        }
        if (this.value instanceof Boolean) {
            return (byte) ((Boolean) this.value ? 1 : 0);
        }
        throw new ClassCastException("can not cast to byte, value : " + this.value);
    }

    /** 判断是否为 short 类型值 */
    public boolean isShort() {
        return OperatorUtils.isShortNumber(this.value);
    }

    /** 转换为 short 值，如果为空值，那么返回 0 */
    public short asShort() {
        if (this.value == null) {
            return 0;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).shortValue();
        }
        if (this.value instanceof String) {
            String strVal = (String) this.value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return 0;
            }
            if (NumberUtils.isNumber(strVal)) {
                return Short.parseShort(strVal);
            }
        }
        if (this.value instanceof Boolean) {
            return (short) ((Boolean) this.value ? 1 : 0);
        }
        throw new ClassCastException("can not cast to short, value : " + this.value);
    }

    /** 判断是否为 short 类型值 */
    public boolean isInt() {
        return OperatorUtils.isIntegerNumber(this.value);
    }

    /** 转换为 int 值，如果为空值，那么返回 0 */
    public int asInt() {
        if (this.value == null) {
            return 0;
        }
        if (this.value instanceof Integer) {
            return (Integer) this.value;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).intValue();
        }
        if (this.value instanceof String) {
            String strVal = (String) this.value;
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
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to int, value : " + this.value);
    }

    /** 判断是否为 long 类型值 */
    public boolean isLong() {
        return OperatorUtils.isLongNumber(this.value);
    }

    /** 转换为 long 值，如果为空值，那么返回 0 */
    public long asLong() {
        if (this.value == null) {
            return 0;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).longValue();
        }
        if (this.value instanceof String) {
            String strVal = (String) this.value;
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
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to long, value : " + this.value);
    }

    /** 判断是否为 float 类型值 */
    public boolean isFloat() {
        return OperatorUtils.isFloatNumber(this.value);
    }

    /** 转换为 float 值，如果为空值，那么返回 0.0 */
    public float asFloat() {
        if (this.value == null) {
            return 0.0f;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).floatValue();
        }
        if (this.value instanceof String) {
            String strVal = this.value.toString();
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
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to float, value : " + this.value);
    }

    /** 判断是否为 double 类型值 */
    public boolean isDouble() {
        return OperatorUtils.isDoubleNumber(this.value);
    }

    /** 转换为 double 值，如果为空值，那么返回 0.0 */
    public double asDouble() {
        if (this.value == null) {
            return 0.0d;
        }
        if (this.value instanceof Number) {
            return ((Number) this.value).doubleValue();
        }
        if (this.value instanceof String) {
            String strVal = this.value.toString();
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
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? 1 : 0;
        }
        throw new ClassCastException("can not cast to double, value : " + this.value);
    }

    /** 判断是否为 BigDecimal 类型值 */
    public boolean isBigDecimal() {
        return this.value instanceof BigDecimal;
    }

    /** 转换为 BigDecimal 值，如果为空值，那么返回 BigDecimal.ZERO */
    public BigDecimal asBigDecimal() {
        if (this.value == null) {
            return BigDecimal.ZERO;
        }
        if (this.value instanceof BigDecimal) {
            return (BigDecimal) this.value;
        }
        if (this.value instanceof BigInteger) {
            return new BigDecimal((BigInteger) this.value);
        }
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        String strVal = this.value.toString();
        if (strVal.length() == 0) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(strVal);
        } catch (NumberFormatException e) {
            throw new ClassCastException("can not cast to BigDecimal, value : " + this.value);
        }
    }

    /** 判断是否为 BigInteger 类型值 */
    public boolean isBigInteger() {
        return this.value instanceof BigInteger;
    }

    /** 转换为 BigDecimal 值，如果为空值，那么返回 BigDecimal.ZERO */
    public BigInteger asBigInteger() {
        if (this.value == null) {
            return BigInteger.ZERO;
        }
        if (this.value instanceof BigInteger) {
            return (BigInteger) this.value;
        }
        if (this.value instanceof Float || this.value instanceof Double) {
            return BigInteger.valueOf(((Number) this.value).longValue());
        }
        String strVal = this.value.toString();
        if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
            return BigInteger.ZERO;
        }
        if (this.value instanceof Boolean) {
            return (Boolean) this.value ? BigInteger.ONE : BigInteger.ZERO;
        }
        try {
            return new BigInteger(strVal);
        } catch (NumberFormatException e) {
            throw new ClassCastException("can not cast to BigInteger, value : " + this.value);
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