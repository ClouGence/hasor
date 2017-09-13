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
package net.hasor.dataql.result;
import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * 值类型结果
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueModel implements DataModel {
    private Object value = null;
    public ValueModel(Object value) {
        this.value = value;
    }
    //
    public Object getOriValue() {
        return this.value;
    }
    public Boolean asBoolean() {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
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
    public String asString() {
        return this.value == null ? null : this.value.toString();
    }
    public Byte asByte() {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
            }
            return Byte.parseByte(strVal);
        }
        throw new ClassCastException("can not cast to byte, value : " + value);
    }
    public Short asShort() {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
            }
            return Short.parseShort(strVal);
        }
        throw new ClassCastException("can not cast to short, value : " + value);
    }
    public Integer asInt() {
        if (value == null) {
            return null;
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
                return null;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            return Integer.parseInt(strVal);
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        }
        throw new ClassCastException("can not cast to int, value : " + value);
    }
    public Long asLong() {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            try {
                return Long.parseLong(strVal);
            } catch (NumberFormatException ex) {
                //
            }
        }
        throw new ClassCastException("can not cast to long, value : " + value);
    }
    public Float asFloat() {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            return Float.parseFloat(strVal);
        }
        throw new ClassCastException("can not cast to float, value : " + value);
    }
    public Double asDouble() {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal)) {
                return null;
            }
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }
            return Double.parseDouble(strVal);
        }
        throw new ClassCastException("can not cast to double, value : " + value);
    }
    public BigDecimal asBigDecimal() {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }
        return new BigDecimal(strVal);
    }
    public BigInteger asBigInteger() {
        if (value == null) {
            return null;
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
            return null;
        }
        return new BigInteger(strVal);
    }
}