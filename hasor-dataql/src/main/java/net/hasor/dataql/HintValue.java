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
package net.hasor.dataql;
/**
 * Hint 的值定义。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface HintValue {
    /** 最大保留的小数位数，默认为：20。超出该范围将会根据 NUMBER_ROUNDING 选项指定的舍入模式进行舍入，默认是四舍五入。 */
    public static final String MAX_DECIMAL_DIGITS          = "MAX_DECIMAL_DIGITS";
    /** 小数的舍入模式，参考 RoundingEnum 定义的舍入模式(一共八种)，默认为：四舍五入。详细配置参考：RoundingEnum 枚举。 */
    public static final String NUMBER_ROUNDING             = "NUMBER_ROUNDING";
    /** 浮点数计算使用的最小数值宽度，可选值有：float,double,big。默认为：double */
    public static final String MIN_DECIMAL_WIDTH           = "MIN_DECIMAL_WIDTH";
    /** 整数计算使用的最小数值宽度，可选值有：byte,short,int,long,big。默认为：int */
    public static final String MIN_INTEGER_WIDTH           = "MIN_INTEGER_WIDTH";
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    /** 浮点数计算使用的最小数值宽度，可选值有：float,double,big。默认为：double */
    public static final String MIN_DECIMAL_WIDTH_FLOAT     = "float";
    public static final String MIN_DECIMAL_WIDTH_DOUBLE    = "double";
    public static final String MIN_DECIMAL_WIDTH_BIG       = "big";
    //
    /** 整数计算使用的最小数值宽度，可选值有：byte,short,int,long,big。默认为：int */
    public static final String MIN_INTEGER_WIDTH_BYTE      = "byte";
    public static final String MIN_INTEGER_WIDTH_SHORT     = "short";
    public static final String MIN_INTEGER_WIDTH_INT       = "int";
    public static final String MIN_INTEGER_WIDTH_LONG      = "long";
    public static final String MIN_INTEGER_WIDTH_BIG       = "big";
    //
    //
    //
    /** 向远离零的方向舍入。舍弃非零部分，并将非零舍弃部分相邻的一位数字加一。*/
    public static final String NUMBER_ROUNDING_UP          = "UP";
    /** 向接近零的方向舍入。舍弃非零部分，同时不会非零舍弃部分相邻的一位数字加一，采取截取行为。*/
    public static final String NUMBER_ROUNDING_DOWN        = "DOWN";
    /** 向正无穷的方向舍入。如果为正数，舍入结果同ROUND_UP一致；如果为负数，舍入结果同ROUND_DOWN一致。注意：此模式不会减少数值大小。*/
    public static final String NUMBER_ROUNDING_CEILING     = "CEILING";
    /**
     * 向负无穷的方向舍入。如果为正数，舍入结果同ROUND_DOWN一致；如果为负数，舍入结果同ROUND_UP一致。
     * 注意：此模式不会增加数值大小。
     */
    public static final String NUMBER_ROUNDING_FLOOR       = "FLOOR";
    /**
     * 向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。如果舍弃部分>= 0.5，则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同。
     * 这种模式也就是我们常说的我们的“四舍五入”。*/
    public static final String NUMBER_ROUNDING_HALF_UP     = "HALF_UP";
    /**
     * 向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则为向下舍入的舍入模式。如果舍弃部分> 0.5，则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同。
     * 这种模式也就是我们常说的我们的“五舍六入”。
     */
    public static final String NUMBER_ROUNDING_HALF_DOWN   = "HALF_DOWN";
    /**
     * 向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则相邻的偶数舍入。如果舍弃部分左边的数字奇数，则舍入行为与 ROUND_HALF_UP 相同；如果为偶数，则舍入行为与 ROUND_HALF_DOWN 相同。
     * 注意：在重复进行一系列计算时，此舍入模式可以将累加错误减到最小。
     * 此舍入模式也称为“银行家舍入法”，主要在美国使用。
     * 四舍六入，五分两种情况，如果前一位为奇数，则入位，否则舍去。
     */
    public static final String NUMBER_ROUNDING_HALF_EVEN   = "HALF_EVEN";
    /** 断言请求的操作具有精确的结果，因此不需要舍入。如果对获得精确结果的操作指定此舍入模式，则抛出ArithmeticException。*/
    public static final String NUMBER_ROUNDING_UNNECESSARY = "UNNECESSARY";
}