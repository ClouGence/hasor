/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.server.handler;
import java.lang.reflect.Array;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014年11月14日
 * @author 赵永春(zyc@hasor.net)
 */
class Utils {
    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    public static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
        if (/*   */tType.equals("I") == true || StringUtils.equalsIgnoreCase(tType, "int") == true) {
            return int.class;
        } else if (tType.equals("B") == true || StringUtils.equalsIgnoreCase(tType, "byte") == true) {
            return byte.class;
        } else if (tType.equals("C") == true || StringUtils.equalsIgnoreCase(tType, "char") == true) {
            return char.class;
        } else if (tType.equals("D") == true || StringUtils.equalsIgnoreCase(tType, "double") == true) {
            return double.class;
        } else if (tType.equals("F") == true || StringUtils.equalsIgnoreCase(tType, "float") == true) {
            return float.class;
        } else if (tType.equals("J") == true || StringUtils.equalsIgnoreCase(tType, "long") == true) {
            return long.class;
        } else if (tType.equals("S") == true || StringUtils.equalsIgnoreCase(tType, "short") == true) {
            return short.class;
        } else if (tType.equals("Z") == true || StringUtils.equalsIgnoreCase(tType, "bit") == true || StringUtils.equalsIgnoreCase(tType, "boolean") == true) {
            return boolean.class;
        } else if (tType.equals("V") == true || StringUtils.equalsIgnoreCase(tType, "void") == true) {
            return void.class;
        } else if (tType.charAt(0) == '[') {
            int length = 0;
            while (true) {
                if (tType.charAt(length) != '[') {
                    break;
                }
                length++;
            }
            String arrayType = tType.substring(length, tType.length());
            Class<?> returnType = toJavaType(arrayType, loader);
            for (int i = 0; i < length; i++) {
                Object obj = Array.newInstance(returnType, length);
                returnType = obj.getClass();
            }
            return returnType;
        } else {
            return loader.loadClass(tType);
        }
    }
}