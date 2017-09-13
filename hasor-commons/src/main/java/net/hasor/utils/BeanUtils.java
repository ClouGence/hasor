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
package net.hasor.utils;
import net.hasor.utils.convert.ConverterUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2011-6-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class BeanUtils {
    /**获取指定类型的默认值。*/
    public static Object getDefaultValue(final Class<?> returnType) {
        if (returnType == null) {
            return null;
        } else if (returnType == int.class || returnType == Integer.class) {
            return 0;
        } else if (returnType == byte.class || returnType == Byte.class) {
            return 0;
        } else if (returnType == char.class || returnType == Character.class) {
            return '\0';
        } else if (returnType == double.class || returnType == Double.class) {
            return 0d;
        } else if (returnType == float.class || returnType == Float.class) {
            return 0f;
        } else if (returnType == long.class || returnType == Long.class) {
            return 0l;
        } else if (returnType == short.class || returnType == Short.class) {
            return 0;
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return false;
        } else if (returnType == void.class || returnType == Void.class) {
            return null;
        } else if (returnType.isArray()) {
            return null;
        }
        return null;
    }
    public static Object[] getDefaultValue(Class<?>[] paramArray) {
        if (paramArray == null) {
            return null;
        }
        Object[] objs = new Object[paramArray.length];
        for (int i = 0; i < paramArray.length; i++) {
            objs[i] = getDefaultValue(paramArray[i]);
        }
        return objs;
    }
    /**
     * 该方法的作用是反射的形式调用目标的方法。
     * @param target 被调用的对象
     * @param methodName 要调用的反射方法名。
     * @param objects 参数列表
     */
    public static Object invokeMethod(final Object target, final String methodName, final Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (target == null) {
            return null;
        }
        Class<?> targetType = target.getClass();
        Method invokeMethod = null;
        //反射调用方法
        Method[] ms = targetType.getMethods();
        for (Method m : ms) {
            //1.名字不相等的忽略
            if (!m.getName().equals(methodName)) {
                continue;
            }
            //2.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != objects.length) {
                continue;
            }
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object param_object = objects[i];
                if (param_object == null) {
                    continue;
                }
                //
                if (!paramTypes[i].isAssignableFrom(param_object.getClass())) {
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (isFind == false) {
                continue;
            }
            //符合条件执行调用
            invokeMethod = m;
        }
        if (invokeMethod == null) {
            throw new NullPointerException(methodName + " invokeMethod is null.");
        } else {
            return invokeMethod.invoke(target, objects);
        }
    }
    /*----------------------------------------------------------------------------------------*/
    /**获取类定义的字段和继承父类中定义的字段以及父类的父类（子类重新定义同名字段也会被列入集合）。*/
    public static List<Field> findALLFields(final Class<?> target) {
        if (target == null) {
            return null;
        }
        ArrayList<Field> fList = new ArrayList<Field>();
        BeanUtils.findALLFields(target, fList);
        return fList;
    }
    private static void findALLFields(final Class<?> target, final ArrayList<Field> fList) {
        if (target == null) {
            return;
        }
        for (Field field : target.getDeclaredFields()) {
            if (!fList.contains(field)) {
                fList.add(field);
            }
        }
        for (Field field : target.getFields()) {
            if (!fList.contains(field)) {
                fList.add(field);
            }
        }
        Class<?> superType = target.getSuperclass();
        if (superType == null || superType == target) {
            return;
        }
        BeanUtils.findALLFields(superType, fList);
    }
    /**获取类定义的方法和继承父类中定义的方法以及父类的父类（子类的重写方法也会被返回）。*/
    public static List<Method> findALLMethods(final Class<?> target) {
        if (target == null) {
            return null;
        }
        ArrayList<Method> mList = new ArrayList<Method>();
        BeanUtils.findALLMethods(target, mList);
        return mList;
    }
    private static void findALLMethods(final Class<?> target, final ArrayList<Method> mList) {
        if (target == null) {
            return;
        }
        for (Method method : target.getDeclaredMethods()) {
            if (!mList.contains(method)) {
                mList.add(method);
            }
        }
        for (Method method : target.getMethods()) {
            if (!mList.contains(method)) {
                mList.add(method);
            }
        }
        Class<?> superType = target.getSuperclass();
        if (superType == null || superType == target) {
            return;
        }
        BeanUtils.findALLMethods(superType, mList);
    }
    /*----------------------------------------------------------------------------------------*/
    /**查找一个可操作的字段列表。*/
    public static List<Field> getFields(final Class<?> type) {
        return Arrays.asList(type.getFields());
    }
    /**查找一个可操作的方法列表。*/
    public static List<Method> getMethods(final Class<?> type) {
        return Arrays.asList(type.getMethods());
    }
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
    /**查找一个可操作的方法。*/
    public static Method getMethod(final Class<?> atClass, final String name, final Class<?>[] paramType) {
        try {
            return atClass.getMethod(name, paramType);
        } catch (Exception e) {
            try {
                return atClass.getDeclaredMethod(name, paramType);
            } catch (Exception e1) {
                return null;
            }
        }
    }
    /**获取属性名集合，该方法是{@link #getPropertys(Class)}方法的升级版，通过该方法还可以同时返回可访问的字段作为属性。*/
    public static List<String> getPropertysAndFields(final Class<?> target) {
        List<String> mnames = BeanUtils.getPropertys(target);
        List<Field> fnames = BeanUtils.getFields(target);
        for (Field f : fnames) {
            String fName = f.getName();
            if (!mnames.contains(fName)) {
                mnames.add(fName);
            }
        }
        return mnames;
    }
    /**获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性。*/
    public static List<String> getPropertys(final Class<?> target) {
        List<String> mnames = new ArrayList<String>();
        List<Method> ms = BeanUtils.getMethods(target);
        for (Method m : ms) {
            String name = m.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            } else {
                continue;
            }
            if (!name.equals("")) {
                name = StringUtils.firstCharToLowerCase(name);
                if (!mnames.contains(name)) {
                    mnames.add(name);
                }
            }
        }
        return mnames;
    }
    /**获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性。*/
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> defineType) {
        List<PropertyDescriptor> mnames = new ArrayList<PropertyDescriptor>();
        List<String> ms = BeanUtils.getPropertys(defineType);
        for (String m : ms) {
            try {
                mnames.add(new PropertyDescriptor(m, defineType));
            } catch (Exception e) {
            }
        }
        return mnames.toArray(new PropertyDescriptor[mnames.size()]);
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
    /**测试是否具有propertyName所表示的属性，无论是读或写方法只要存在一个就表示存在该属性。*/
    public static boolean hasProperty(final String propertyName, final Class<?> target) {
        //get、set方法
        if (BeanUtils.getReadMethod(propertyName, target) == null) {
            if (BeanUtils.getWriteMethod(propertyName, target) == null) {
                return false;
            }
        }
        return true;
    }
    /**测试是否具有fieldName所表示的字段，无论是读或写方法只要存在一个就表示存在该属性。*/
    public static boolean hasField(final String propertyName, final Class<?> target) {
        if (BeanUtils.getField(propertyName, target) == null) {
            return false;
        } else {
            return true;
        }
    }
    /**测试是否具有name所表示的属性，hasProperty或hasField有一个返回为true则返回true。*/
    public static boolean hasPropertyOrField(final String name, final Class<?> target) {
        if (!BeanUtils.hasProperty(name, target)) {
            if (!BeanUtils.hasField(name, target)) {
                return false;
            }
        }
        return true;
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
    /**测试是否支持readPropertyOrField方法。*/
    public static boolean canReadPropertyOrField(final String propertyName, final Class<?> target) {
        if (!BeanUtils.canReadProperty(propertyName, target)) {
            if (!BeanUtils.hasField(propertyName, target)) {
                return false;
            }
        }
        return true;
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
    /**测试是否支持writePropertyOrField方法。*/
    public static boolean canWritePropertyOrField(final String propertyName, final Class<?> target) {
        if (!BeanUtils.canWriteProperty(propertyName, target)) {
            if (!BeanUtils.hasField(propertyName, target)) {
                return false;
            }
        }
        return true;
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
    /***/
    public static Class<?> getPropertyType(final Class<?> defineType, final String attName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(attName, defineType);
            return pd.getPropertyType();
        } catch (Exception e) {
            return null;
        }
    }
    /***/
    public static Class<?> getFieldType(final Class<?> defineType, final String attName) {
        Field readField = BeanUtils.getField(attName, defineType);
        if (readField != null) {
            return readField.getType();
        }
        return null;
    }
    /***/
    public static Class<?> getPropertyOrFieldType(final Class<?> defineType, final String attName) {
        Class<?> propType = null;
        //
        propType = BeanUtils.getPropertyType(defineType, attName);
        if (propType != null) {
            return propType;
        }
        propType = BeanUtils.getFieldType(defineType, attName);
        if (propType != null) {
            return propType;
        }
        return null;
    }
    /***/
    public static void copyProperties(final Object dest, final Object orig) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("orig is null");
        }
        //
        List<String> propNames = new ArrayList<String>();
        if (orig instanceof Map) {
            for (Object key : ((Map) orig).keySet()) {
                propNames.add(key.toString());
            }
        } else {
            propNames = BeanUtils.getPropertys(orig.getClass());
        }
        for (String prop : propNames) {
            BeanUtils.copyProperty(dest, orig, prop);
        }
    }
    /***/
    public static void copyProperty(final Object dest, final Object orig, final String propertyName) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("orig is null");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("propertyName is null");
        }
        //
        if (!(orig instanceof Map)) {
            if (!BeanUtils.canReadPropertyOrField(propertyName, orig.getClass())) {
                return;
            }
        }
        if (!(dest instanceof Map)) {
            if (!BeanUtils.canWritePropertyOrField(propertyName, dest.getClass())) {
                return;
            }
        }
        //
        Object val = null;
        if (!(orig instanceof Map)) {
            val = BeanUtils.readPropertyOrField(orig, propertyName);
        } else {
            val = ((Map) orig).get(propertyName);
        }
        //
        if (!(dest instanceof Map)) {
            BeanUtils.writePropertyOrField(dest, propertyName, val);
        } else {
            ((Map) orig).put(propertyName, val);
        }
    }
}