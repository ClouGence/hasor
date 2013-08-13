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
package org.test.more.core.util;
import org.junit.Test;
import org.more.convert.ConverterUtils;
import org.more.util.StringConvertUtils;
public class ConvertTest {
    @Test
    public void toBoolean() {
        System.out.println(ConverterUtils.convert("true", Boolean.class));
        System.out.println(ConverterUtils.convert("false", Boolean.class));
        System.out.println(ConverterUtils.convert("0", Boolean.class));
        System.out.println(ConverterUtils.convert("1", Boolean.class));
    };
    @Test
    public void toByte() {
        System.out.println(ConverterUtils.convert("123", Byte.TYPE));
        //System.out.println(ConverterUtils.convert("566", Byte.TYPE, 56));
    };
    @Test
    public void toShort() {
        System.out.println(StringConvertUtils.parseShort("123", (short) 56));
        System.out.println(StringConvertUtils.parseShort("32100000", (short) 56));
    };
    @Test
    public void toInteger() {
        System.out.println(StringConvertUtils.parseInt("123", (int) 56));
        System.out.println(StringConvertUtils.parseInt("321000000000000000000000", (int) 56));
    };
    @Test
    public void toLong() {
        System.out.println(StringConvertUtils.parseLong("123", (long) 56));
        System.out.println(StringConvertUtils.parseLong("321000000000000000000000", (long) 56));
    };
    @Test
    public void toFloat() {
        System.out.println(StringConvertUtils.parseFloat("123.23", (float) 56));
        System.out.println(StringConvertUtils.parseFloat("321000000000000000.000", (float) 56.6));
    };
    @Test
    public void toDouble() {
        System.out.println(StringConvertUtils.parseDouble("123.23", (double) 56));
        System.out.println(StringConvertUtils.parseDouble("3210000000000000000000000000000000000000000.000", (double) 56.6));
    };
    @Test
    public void toDate() {
        System.out.println(StringConvertUtils.parseDate("2007/05/05", "yyyy/MM/dd"));
    };
    @Test
    public void toEnum() {
        System.out.println(StringConvertUtils.parseEnum("call", TestEnum.class));
    };
    enum TestEnum {
        take, call
    }
}