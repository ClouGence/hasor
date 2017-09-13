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
package net.hasor.dataql.utils;
import net.hasor.core.convert.ConverterUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 *
 * @version : 2011-6-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class BeanUtils {
    /*----------------------------------------------------------------------------------------*/
    /**查找一个可操作的字段。*/
    public static Field getField(final String fieldName, final Class<?> type) {
        if (fieldName == null || type == null) {
            return null;
        }
        for (Field f : type.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }
    /**获取一个属性的读取方法。*/
    public static Method getReadMethod(final String property, final Class<?> target) {
        if (property == null || target == null) {
            return null;
        }
        String methodName_1 = "get" + StringUtils.firstCharToUpperCase(property);
        String methodName_2 = "is" + StringUtils.firstCharToUpperCase(property);
        //
        for (Method m : target.getMethods()) {
            if (m.getParameterTypes().length == 0) {
                String methodName = m.getName();
                if (methodName.equals(methodName_1)) {
                    return m;
                }
                /*是否是布尔*/
                if (methodName.equals(methodName_2)) {
                    Class<?> t = m.getReturnType();
                    if (t == Boolean.class || t == boolean.class) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
    /**获取一个属性的写入方法。*/
    public static Method getWriteMethod(final String property, final Class<?> target) {
        if (property == null || target == null) {
            return null;
        }
        String methodName = "set" + StringUtils.firstCharToUpperCase(property);
        for (Method m : target.getMethods()) {
            if (m.getName().equals(methodName)) {
                if (m.getParameterTypes().length == 1) {
                    return m;
                }
            }
        }
        return null;
    }
    /**测试是否具有fieldName所表示的字段，无论是读或写方法只要存在一个就表示存在该属性。*/
    public static boolean hasField(final String propertyName, final Class<?> target) {
        if (BeanUtils.getField(propertyName, target) == null) {
            return false;
        } else {
            return true;
        }
    }
    /**测试是否支持readProperty方法。返回true表示可以进行读取操作。*/
    public static boolean canReadProperty(final String propertyName, final Class<?> target) {
        Method readMethod = BeanUtils.getReadMethod(propertyName, target);
        if (readMethod != null) {
            return true;
        } else {
            return false;
        }
    }
    /**测试是否支持writeProperty方法。返回true表示可以进行写入操作。*/
    public static boolean canWriteProperty(final String propertyName, final Class<?> target) {
        Method writeMethod = BeanUtils.getWriteMethod(propertyName, target);
        if (writeMethod != null) {
            return true;
        } else {
            return false;
        }
    }
    /*----------------------------------------------------------------------------------------*/
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。返回值表示执行是否成功。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeProperty(final Object object, final String attName, final Object value) {
        if (object == null || attName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method writeMethod = BeanUtils.getWriteMethod(attName, defineType);
        if (writeMethod == null) {
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object attValueObject = ConverterUtils.convert(toType, value);
        //3.执行属性注入
        try {
            writeMethod.invoke(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**执行字段注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeField(final Object object, final String fieldName, final Object value) {
        if (object == null || fieldName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field writeField = BeanUtils.getField(fieldName, defineType);
        if (writeField == null) {
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeField.getType();
        Object attValueObject = ConverterUtils.convert(toType, value);
        //3.执行属性注入
        try {
            writeField.set(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**执行注入，该方法首先会视图执行属性方法注入。如果失败则执行字段注入。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writePropertyOrField(final Object object, final String attName, final Object value) {
        Class<?> defineType = object.getClass();
        if (BeanUtils.canWriteProperty(attName, defineType)) {
            return BeanUtils.writeProperty(object, attName, value);//支持方法写入
        }
        if (BeanUtils.hasField(attName, defineType)) {
            return BeanUtils.writeField(object, attName, value);//支持字段写入
        }
        return false;
    }
    /**执行属性读取。*/
    public static Object readProperty(final Object object, final String attName) {
        if (object == null || attName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method readMethod = BeanUtils.getReadMethod(attName, defineType);
        if (readMethod == null) {
            return null;
        }
        //2.执行属性读取
        try {
            return readMethod.invoke(object);
        } catch (Exception e) {
            return null;
        }
    }
    /**执行字段读取。*/
    public static Object readField(final Object object, final String fieldName) {
        if (object == null || fieldName == null) {
            return null;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field readField = BeanUtils.getField(fieldName, defineType);
        if (readField == null) {
            return null;
        }
        //2.执行字段读取
        try {
            return readField.get(object);
        } catch (Exception e) {
            return null;
        }
    }
    /**执行注入，该方法首先会视图执行属性方法注入。如果失败则执行字段注入。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static Object readPropertyOrField(final Object object, final String attName) {
        Class<?> defineType = object.getClass();
        if (BeanUtils.canReadProperty(attName, defineType)) {
            return BeanUtils.readProperty(object, attName);//支持方法读取
        }
        if (BeanUtils.hasField(attName, defineType)) {
            return BeanUtils.readField(object, attName);//支持字段读取
        }
        return null;
    }
}