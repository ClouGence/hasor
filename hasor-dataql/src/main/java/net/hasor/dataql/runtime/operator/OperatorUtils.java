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
package net.hasor.dataql.runtime.operator;
import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * 数学计算处理工具，提供：加、减、乘、除、整除、求余
 * 尽量考虑在保证性能的前提下不产生精度丢失问题。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class OperatorUtils {
    public static boolean isNumber(Object object) {
        if (object == null) {
            return false;
        }
        return object instanceof Number;
    }
    public static boolean isBoolean(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> numberClass = object.getClass();
        return numberClass == Boolean.class || numberClass == Boolean.TYPE;
    }
    public static boolean isByteNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Byte.class || numberClass == Byte.TYPE;
    }
    public static boolean isShortNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Short.class || numberClass == Short.TYPE;
    }
    public static boolean isIntegerNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Integer.class || numberClass == Integer.TYPE;
    }
    public static boolean isCharacter(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Character.class || numberClass == Character.TYPE;
    }
    public static boolean isLongNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Long.class || numberClass == Long.TYPE;
    }
    public static boolean isFloatNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Float.class || numberClass == Float.TYPE;
    }
    public static boolean isDoubleNumber(Object number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Double.class || numberClass == Double.TYPE;
    }
    //
    // ============================================================================================
    //
    private static final int BOOL   = 1;
    private static final int BYTE   = 2;
    private static final int SHORT  = 3;
    private static final int CHAR   = 4;
    private static final int INT    = 5;
    private static final int LONG   = 6;
    //
    private static final int FLOAT  = 7;
    private static final int DOUBLE = 8;
    //
    private static final int BIGINT = 9;
    private static final int BIGDEC = 10;
    //
    private static final int NONE   = 0;
    //
    /** 对比两个数据类型，返回交大的那个类型作为载体。 */
    protected static int getNumericType(Number v1, Number v2) {
        int v1Type = getNumericType(v1);
        int v2Type = getNumericType(v2);
        //
        // .未知类型
        if (v1Type == v2Type) {
            return v1Type;
        }
        if (v1Type == NONE || v2Type == NONE) {
            return NONE;
        }
        // .整数类型的只使用 long or int 作为承载
        if (v1Type <= LONG && v2Type <= LONG) {
            return (v1Type == LONG || v2Type == LONG) ? LONG : INT;
        }
        // .浮点数使用 float or double 作为承载
        if (v1Type <= DOUBLE && v2Type <= DOUBLE) {
            // boolean、byte、short、float  -> float
            // int、char、double            -> double
            boolean useFloat = v1Type <= SHORT || v2Type <= SHORT && (v1Type == FLOAT || v2Type == FLOAT);
            return useFloat ? FLOAT : DOUBLE;
        }
        // .整数 or 浮点
        boolean useDec = v1Type == FLOAT || v1Type == DOUBLE || v2Type == FLOAT || v2Type == DOUBLE;
        return useDec ? BIGDEC : BIGINT;
    }
    protected static int getNumericType(Object value) {
        if (OperatorUtils.isBoolean(value)) {
            return BOOL;
        }
        if (OperatorUtils.isByteNumber(value)) {
            return BYTE;
        }
        if (OperatorUtils.isShortNumber(value)) {
            return SHORT;
        }
        if (OperatorUtils.isCharacter(value)) {
            return CHAR;
        }
        if (OperatorUtils.isIntegerNumber(value)) {
            return INT;
        }
        if (OperatorUtils.isLongNumber(value)) {
            return LONG;
        }
        if (value instanceof BigInteger) {
            return BIGINT;
        }
        if (OperatorUtils.isFloatNumber(value)) {
            return FLOAT;
        }
        if (OperatorUtils.isDoubleNumber(value)) {
            return DOUBLE;
        }
        if (value instanceof BigDecimal) {
            return BIGDEC;
        }
        return NONE;
    }
    //
    //
    /** 转换为：boolean */
    public static boolean booleanValue(Object value) {
        if (value == null) {
            return false;
        }
        Class c = value.getClass();
        if (c == Boolean.class) {
            return (Boolean) value;
        }
        if (c == Character.class) {
            return (Character) value != 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return true;
    }
    /** 转换为：int */
    private static int intValue(Object value) {
        if (value == null) {
            return 0;
        }
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).intValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        if (c == Character.class) {
            return (Character) value;
        }
        return Integer.parseInt(value.toString().trim());
    }
    /** 转换为：long */
    private static long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).longValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        if (c == Character.class) {
            return (Character) value;
        }
        return Long.parseLong(value.toString().trim());
    }
    /** 转换为：BigInteger */
    private static BigInteger bigIntValue(Object value) {
        if (value == null) {
            return BigInteger.valueOf(0L);
        }
        Class c = value.getClass();
        if (c == BigInteger.class) {
            return (BigInteger) value;
        }
        if (c == BigDecimal.class) {
            return ((BigDecimal) value).toBigInteger();
        }
        if (c.getSuperclass() == Number.class) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        if (c == Boolean.class) {
            return BigInteger.valueOf((Boolean) value ? 1 : 0);
        }
        if (c == Character.class) {
            return BigInteger.valueOf((Character) value);
        }
        return new BigInteger(value.toString().trim());
    }
    /** 转换为：float */
    private static float floatValue(Object value) {
        if (value == null) {
            return 0.0f;
        }
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).floatValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1.0f : 0.0f;
        }
        if (c == Character.class) {
            return (Character) value;
        }
        String s = value.toString().trim();
        return (s.length() == 0) ? 0.0f : Float.parseFloat(s);
    }
    /** 转换为：double */
    private static double doubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).doubleValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        if (c == Character.class) {
            return (Character) value;
        }
        String s = value.toString().trim();
        return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
    }
    /** 转换为：BigDecimal */
    private static BigDecimal bigDecimalValue(Object value) {
        if (value == null) {
            return BigDecimal.valueOf(0L);
        }
        Class c = value.getClass();
        if (c == BigDecimal.class) {
            return (BigDecimal) value;
        }
        if (c == BigInteger.class) {
            return new BigDecimal((BigInteger) value);
        }
        if (c.getSuperclass() == Number.class) {
            return new BigDecimal(((Number) value).doubleValue());
        }
        if (c == Boolean.class) {
            return BigDecimal.valueOf((Boolean) value ? 1 : 0);
        }
        if (c == Character.class) {
            return BigDecimal.valueOf((Character) value);
        }
        return new BigDecimal(value.toString().trim());
    }
    //
    // ============================================================================================
    /** 加 */
    public static Number add(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            return decimalAdd(obj1, obj2);
        } else {
            return integerAdd(obj1, obj2);
        }
    }
    /** 减 */
    public static Number subtract(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            return decimalSubtract(obj1, obj2);
        } else {
            return integerSubtract(obj1, obj2);
        }
    }
    /** 乘 */
    public static Number multiply(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            return decimalMultiply(obj1, obj2);
        } else {
            return integerMultiply(obj1, obj2);
        }
    }
    /** 除 */
    public static Number divide(Number obj1, Number obj2, int precision, RoundingEnum roundingEnum) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            if (roundingEnum == null) {
                roundingEnum = RoundingEnum.HALF_UP;
            }
            if (precision <= 0) {
                precision = 23;
            }
            return decimalDivide(obj1, obj2, precision, roundingEnum);
        } else {
            return integerDivide(obj1, obj2);
        }
    }
    /** 整除 */
    public static Number aliquot(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            return decimalAliquot(obj1, obj2);
        } else {
            return integerDivide(obj1, obj2);
        }
    }
    /** 求余 */
    public static Number mod(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            return decimalMod(obj1, obj2);
        } else {
            return integerMod(obj1, obj2);
        }
    }
    /** 取反，相当于：value * -1 */
    public static Number negate(Number obj) {
        if (testDecimal(obj)) {
            return decimalNegate(obj);
        } else {
            return integerNegate(obj);
        }
    }
    //
    /* 测试是否为一个小数 */
    private static boolean testDecimal(Number tester) {
        if (tester instanceof BigDecimal) {
            return true;
        }
        if (OperatorUtils.isFloatNumber(tester)) {
            return true;
        }
        if (OperatorUtils.isDoubleNumber(tester)) {
            return true;
        }
        return false;
    }
    private static Number newReal(int realType, long value) {
        switch (realType) {
        case BOOL:
            return (value == 0) ? 0 : 1;
        case BYTE:
            return (byte) value;
        case SHORT:
            return (short) value;
        case CHAR:
        case INT:
            return (int) value;
        default:
            return value;
        }
    }
    private static Number newReal(int realType, BigInteger value) {
        switch (realType) {
        case BOOL:
            return BigInteger.ZERO.compareTo(value) == 0 ? 0 : 1;
        case BYTE:
            return value.byteValue();
        case SHORT:
            return value.shortValue();
        case CHAR:
        case INT:
            return value.intValue();
        case LONG:
            return value.longValue();
        default:
            return value;
        }
    }
    private static Number newReal(int realType, double value) {
        if (realType == FLOAT)
            return (float) value;
        return value;
    }
    private static Number newReal(int realType, BigDecimal value) {
        if (realType == FLOAT)
            return value.floatValue();
        if (realType == DOUBLE)
            return value.doubleValue();
        return value;
    }
    //
    /** 整数，加 */
    private static Number integerAdd(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, intValue(obj1) + intValue(obj2));
        case LONG:
            return newReal(maxType, longValue(obj1) + longValue(obj2));
        default:
            return newReal(maxType, bigIntValue(obj1).add(bigIntValue(obj2)));
        }
    }
    /** 整数，减 */
    private static Number integerSubtract(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, intValue(obj1) - intValue(obj2));
        case LONG:
            return newReal(maxType, longValue(obj1) - longValue(obj2));
        default:
            return newReal(maxType, bigIntValue(obj1).subtract(bigIntValue(obj2)));
        }
    }
    /** 整数，乘 */
    private static Number integerMultiply(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, intValue(obj1) * intValue(obj2));
        case LONG:
            return newReal(maxType, longValue(obj1) * longValue(obj2));
        default:
            return newReal(maxType, bigIntValue(obj1).multiply(bigIntValue(obj2)));
        }
    }
    /** 整数，除 or 整除 */
    private static Number integerDivide(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, intValue(obj1) / intValue(obj2));
        case LONG:
            return newReal(maxType, longValue(obj1) / longValue(obj2));
        default:
            return newReal(maxType, bigIntValue(obj1).divide(bigIntValue(obj2)));
        }
    }
    /** 整数，求余 */
    private static Number integerMod(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, intValue(obj1) % intValue(obj2));
        case LONG:
            return newReal(maxType, longValue(obj1) % longValue(obj2));
        default:
            return newReal(maxType, bigIntValue(obj1).mod(bigIntValue(obj2)));
        }
    }
    /** 整数，取反 */
    private static Number integerNegate(Number obj) {
        int maxType = getNumericType(obj);
        switch (maxType) {
        case BOOL:
        case BYTE:
        case SHORT:
        case CHAR:
        case INT:
            return newReal(maxType, -intValue(obj));
        case LONG:
            return newReal(maxType, -longValue(obj));
        default:
            return newReal(maxType, bigIntValue(obj).negate());
        }
    }
    //
    /** 小数，加 */
    private static Number decimalAdd(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, floatValue(obj1) + floatValue(obj2));
        case DOUBLE:
            return newReal(maxType, doubleValue(obj1) + doubleValue(obj2));
        default:
            return newReal(maxType, bigDecimalValue(obj1).add(bigDecimalValue(obj2)));
        }
    }
    /** 小数，减 */
    private static Number decimalSubtract(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, floatValue(obj1) - floatValue(obj2));
        case DOUBLE:
            return newReal(maxType, doubleValue(obj1) - doubleValue(obj2));
        default:
            return newReal(maxType, bigDecimalValue(obj1).subtract(bigDecimalValue(obj2)));
        }
    }
    /** 小数，乘 */
    private static Number decimalMultiply(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, floatValue(obj1) * floatValue(obj2));
        case DOUBLE:
            return newReal(maxType, doubleValue(obj1) * doubleValue(obj2));
        default:
            return newReal(maxType, bigDecimalValue(obj1).multiply(bigDecimalValue(obj2)));
        }
    }
    /** 小数，除 */
    private static Number decimalDivide(Number obj1, Number obj2, int precision, RoundingEnum roundingEnum) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, floatValue(obj1) / floatValue(obj2));
        case DOUBLE:
            return newReal(maxType, doubleValue(obj1) / doubleValue(obj2));
        default:
            return newReal(maxType, bigDecimalValue(obj1).divide(bigDecimalValue(obj2), precision, roundingEnum.getModeNum()));
        }
    }
    /** 小数，整除 */
    private static Number decimalAliquot(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, (int) (floatValue(obj1) / floatValue(obj2)));
        case DOUBLE:
            return newReal(maxType, (long) (doubleValue(obj1) / doubleValue(obj2)));
        default:
            return newReal(maxType, bigDecimalValue(obj1).divideToIntegralValue(bigDecimalValue(obj2)));
        }
    }
    /** 小数，求余 */
    private static Number decimalMod(Number obj1, Number obj2) {
        int maxType = getNumericType(obj1, obj2);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, floatValue(obj1) % floatValue(obj2));
        case DOUBLE:
            return newReal(maxType, doubleValue(obj1) % doubleValue(obj2));
        default:
            return newReal(maxType, bigDecimalValue(obj1).remainder(bigDecimalValue(obj2)));
        }
    }
    /** 小数，取反 */
    private static Number decimalNegate(Number obj) {
        int maxType = getNumericType(obj);
        switch (maxType) {
        case FLOAT:
            return newReal(maxType, -floatValue(obj));
        case DOUBLE:
            return newReal(maxType, -doubleValue(obj));
        default:
            return newReal(maxType, bigDecimalValue(obj).negate());
        }
    }
    //
    // ============================================================================================
    //
    private static int getNumericTypeWithCompare(Number v1, Number v2) {
        int numericType = getNumericType(v1, v2);
        if (numericType == NONE) {
            numericType = (testDecimal(v1) || testDecimal(v2)) ? BIGDEC : BIGINT;
        }
        return numericType;
    }
    /** 相等 */
    public static boolean eq(Number obj1, Number obj2) {
        int numericType = getNumericTypeWithCompare(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) == longValue(obj2);
        }
        if (numericType <= DOUBLE) {
            return doubleValue(obj1) == doubleValue(obj2);
        }
        if (numericType == BIGINT) {
            return bigIntValue(obj1).compareTo(bigIntValue(obj2)) == 0;
        }
        if (numericType == BIGDEC) {
            return bigDecimalValue(obj1).compareTo(bigDecimalValue(obj2)) == 0;
        }
        return obj1.doubleValue() == obj2.doubleValue();
    }
    /** 大于 */
    public static boolean gt(Number obj1, Number obj2) {
        int numericType = getNumericTypeWithCompare(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) > longValue(obj2);
        }
        if (numericType <= DOUBLE) {
            return doubleValue(obj1) > doubleValue(obj2);
        }
        if (numericType == BIGINT) {
            return bigIntValue(obj1).compareTo(bigIntValue(obj2)) > 0;
        }
        if (numericType == BIGDEC) {
            return bigDecimalValue(obj1).compareTo(bigDecimalValue(obj2)) > 0;
        }
        return obj1.doubleValue() > obj2.doubleValue();
    }
    /** 大于等于 */
    public static boolean gteq(Number obj1, Number obj2) {
        int numericType = getNumericTypeWithCompare(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) >= longValue(obj2);
        }
        if (numericType <= DOUBLE) {
            return doubleValue(obj1) >= doubleValue(obj2);
        }
        if (numericType == BIGINT) {
            return bigIntValue(obj1).compareTo(bigIntValue(obj2)) >= 0;
        }
        if (numericType == BIGDEC) {
            return bigDecimalValue(obj1).compareTo(bigDecimalValue(obj2)) >= 0;
        }
        return obj1.doubleValue() >= obj2.doubleValue();
    }
    /** 小于 */
    public static boolean lt(Number obj1, Number obj2) {
        int numericType = getNumericTypeWithCompare(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) < longValue(obj2);
        }
        if (numericType <= DOUBLE) {
            return doubleValue(obj1) < doubleValue(obj2);
        }
        if (numericType == BIGINT) {
            return bigIntValue(obj1).compareTo(bigIntValue(obj2)) < 0;
        }
        if (numericType == BIGDEC) {
            return bigDecimalValue(obj1).compareTo(bigDecimalValue(obj2)) < 0;
        }
        return obj1.doubleValue() < obj2.doubleValue();
    }
    /** 小于等于 */
    public static boolean lteq(Number obj1, Number obj2) {
        int numericType = getNumericTypeWithCompare(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) <= longValue(obj2);
        }
        if (numericType <= DOUBLE) {
            return doubleValue(obj1) <= doubleValue(obj2);
        }
        if (numericType == BIGINT) {
            return bigIntValue(obj1).compareTo(bigIntValue(obj2)) <= 0;
        }
        if (numericType == BIGDEC) {
            return bigDecimalValue(obj1).compareTo(bigDecimalValue(obj2)) <= 0;
        }
        return obj1.doubleValue() <= obj2.doubleValue();
    }
    //
    // ============================================================================================
    //
    private static void checkDecimal(Number obj1, Number obj2) {
        if (testDecimal(obj1) || testDecimal(obj2)) {
            throw new NumberFormatException("value mast be int.");
        }
    }
    /** 与 */
    public static Number and(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) & longValue(obj2);
        } else {
            return bigIntValue(obj1).and(bigIntValue(obj2));
        }
    }
    /** 或 */
    public static Number or(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) | longValue(obj2);
        } else {
            return bigIntValue(obj1).or(bigIntValue(obj2));
        }
    }
    /** 异或 */
    public static Number xor(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) ^ longValue(obj2);
        } else {
            return bigIntValue(obj1).xor(bigIntValue(obj2));
        }
    }
    /** 左位移 */
    public static Number shiftLeft(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) << intValue(obj2);
        } else {
            return bigIntValue(obj1).shiftLeft(intValue(obj2));
        }
    }
    /** 右位移 */
    public static Number shiftRight(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) >> intValue(obj2);
        } else {
            return bigIntValue(obj1).shiftRight(intValue(obj2));
        }
    }
    /** 无符号右位移 */
    public static Number shiftRightWithUnsigned(Number obj1, Number obj2) {
        checkDecimal(obj1, obj2);
        int numericType = getNumericType(obj1, obj2);
        if (numericType <= LONG) {
            return longValue(obj1) >>> intValue(obj2);
        } else {
            //忽略无符号的右位移运算符（>>>），因为该操作与由此类提供的“无穷大的词大小”抽象结合使用时毫无意义。
            // - 无穷大的词大小 -> BigInteger 理论上可以表示无穷大。
            return bigIntValue(obj1).shiftRight(intValue(obj2));
        }
    }
}