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
/**
 * 工具类
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class OperatorUtils {
    public static boolean isByteNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Byte.class || numberClass == Byte.TYPE;
    }
    public static boolean isShortNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Short.class || numberClass == Short.TYPE;
    }
    public static boolean isIntegerNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Integer.class || numberClass == Integer.TYPE;
    }
    public static boolean isLongNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Long.class || numberClass == Long.TYPE;
    }
    public static boolean isFloatNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Float.class || numberClass == Float.TYPE;
    }
    public static boolean isDoubleNumber(Number number) {
        if (number == null) {
            return false;
        }
        Class<?> numberClass = number.getClass();
        return numberClass == Double.class || numberClass == Double.TYPE;
    }
    public static boolean isBoolean(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> numberClass = object.getClass();
        return numberClass == Boolean.class || numberClass == Boolean.TYPE;
    }
}