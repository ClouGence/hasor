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
package org.more.util;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.more.classcode.EngineToos;
import org.more.classcode.RootClassLoader;
import org.more.core.error.InvokeException;
/**
 * 
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class BeanUtils {
    /**通过位运算决定data是否在modifiers里。*/
    public static boolean checkModifiers(int modifiers, int data) {
        int a = modifiers | data;
        return a == data;
    };
    /**获取一个类对象字节码的读取流。*/
    public static InputStream getClassInputStream(Class<?> target) {
        ClassLoader loader = target.getClassLoader();
        if (loader instanceof RootClassLoader) {
            byte[] data = ((RootClassLoader) loader).toBytes(target);
            return new ByteArrayInputStream(data);
        }
        String classResourceName = target.getName().replace(".", "/") + ".class";
        if (loader != null)
            return loader.getResourceAsStream(classResourceName);
        else
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(classResourceName);
    };
    /**检查类型是否为一个基本类型或其包装类型，基本类型包括了boolean, byte, char, short, int, long, float, 和 double*/
    public static boolean isBaseType(Class<?> target) {
        /*判断是否为基本类型*/
        if (target.isPrimitive() == true)
            return true;
        /*判断各种包装类型*/
        if (target == Boolean.class)
            return true;
        if (target == Byte.class)
            return true;
        if (target == Character.class)
            return true;
        if (target == Short.class)
            return true;
        if (target == Integer.class)
            return true;
        if (target == Long.class)
            return true;
        if (target == Float.class)
            return true;
        if (target == Double.class)
            return true;
        return false;
    }
    /**获取指定类型的默认值。*/
    public static Object getDefaultValue(Class<?> returnType) {
        if (returnType == null)
            return null;
        else if (returnType == int.class || returnType == Integer.class)
            return 0;
        else if (returnType == byte.class || returnType == Byte.class)
            return 0;
        else if (returnType == char.class || returnType == Character.class)
            return ' ';
        else if (returnType == double.class || returnType == Double.class)
            return 0d;
        else if (returnType == float.class || returnType == Float.class)
            return 0f;
        else if (returnType == long.class || returnType == Long.class)
            return 0l;
        else if (returnType == short.class || returnType == Short.class)
            return 0;
        else if (returnType == boolean.class || returnType == Boolean.class)
            return false;
        else if (returnType == void.class || returnType == Void.class)
            return null;
        else if (returnType.isArray() == true)
            return null;
        else
            return null;
    };
    /**检测类名是否合法。*/
    public static boolean checkClassName(String className) {
        if (className == null || className.equals(""))
            return false;
        String item[] = { "..", "!", "@", "#", "%", "^", "&", "*", "(", ")", "-", "=", "+", "{", "}", ";", ";", "\"", "'", "<", ">", ",", "?", "/", "`", "~", " ", "\\", "|" };
        for (int i = 0; i <= item.length - 1; i++)
            if (className.indexOf(item[i]) >= 0)
                return false;
        if (className.indexOf(".") == 0)
            return false;
        if (className.indexOf(".", className.length()) == className.length())
            return false;
        return true;
    }
    /**判断某个类是否为一个lang包的类。*/
    public static boolean isLangClass(Class<?> target) {
        return target.getName().startsWith("java.lang.");
    };
    /**获取类完整限定名的类名部分。*/
    public static String getSimpleAtFull(String fullName) {
        String[] ns = fullName.split("\\.");
        return ns[ns.length - 1];
    }
    /**获取类完整限定名的包名部分。*/
    public static String getPackageAtFull(String fullName) {
        if (fullName.lastIndexOf(".") > 0)
            return fullName.substring(0, fullName.lastIndexOf("."));
        else
            return fullName;
    }
    /**获取方法的标识代码，在不考虑其所属类的情况下。*/
    public static String getCodeWithoutClass(Method method) {
        //public void addChild(org.noe.safety.services.SYS_TB_MenuTree)
        StringBuffer str = new StringBuffer(method.toString());
        String declaringClass = method.getDeclaringClass().getName();
        int indexStart = str.indexOf(declaringClass);
        str.delete(indexStart, indexStart + declaringClass.length() + 1);
        return str.toString();
    }
    /**
     * 该方法的作用是反射的形式调用目标的方法。
     * @param target 被调用的对象
     * @param methodName 要调用的反射方法名。
     * @param objects 参数列表
     */
    public static Object invokeMethod(Object target, String methodName, Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (target == null)
            return null;
        Class<?> targetType = target.getClass();
        Method invokeMethod = null;
        //反射调用方法
        Method[] ms = targetType.getMethods();
        for (Method m : ms) {
            //1.名字不相等的忽略
            if (m.getName().equals(methodName) == false)
                continue;
            //2.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != objects.length)
                continue;
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object param_object = objects[i];
                if (param_object == null)
                    continue;
                //
                if (paramTypes[i].isAssignableFrom(param_object.getClass()) == false) {
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (isFind == false)
                continue;
            //符合条件执行调用
            invokeMethod = m;
        }
        if (invokeMethod == null)
            throw new InvokeException("无法调用目标方法[" + methodName + "]！");
        else
            return invokeMethod.invoke(target, objects);
    }
    /*----------------------------------------------------------------------------------------*/
    /**获取类定义的字段和继承父类中定义的字段以及父类的父类（子类重新定义同名字段也会被列入集合）。*/
    public static List<Field> findALLFields(Class<?> target) {
        if (target == null)
            return null;
        ArrayList<Field> fList = new ArrayList<Field>();
        findALLFields(target, fList);
        return fList;
    }
    private static void findALLFields(Class<?> target, ArrayList<Field> fList) {
        if (target == null)
            return;
        for (Field field : target.getDeclaredFields())
            if (fList.contains(field) == false)
                fList.add(field);
        for (Field field : target.getFields())
            if (fList.contains(field) == false)
                fList.add(field);
        Class<?> superType = target.getDeclaringClass();
        if (superType == null || superType == target)
            return;
        findALLFields(superType, fList);
    }
    /**{@link #findALLFields(Class))}方法返回值的无重复版本。*/
    public static List<Field> findALLFieldsNoRepeat(Class<?> target) {
        if (target == null)
            return null;
        LinkedHashMap<String, Field> fMap = new LinkedHashMap<String, Field>();
        findALLFieldsNoRepeat(target, fMap);
        return new ArrayList<Field>(fMap.values());
    }
    private static void findALLFieldsNoRepeat(Class<?> target, LinkedHashMap<String, Field> fMap) {
        if (target == null)
            return;
        for (Field field : target.getDeclaredFields())
            if (fMap.containsKey(field.getName()) == false)
                fMap.put(field.getName(), field);
        for (Field field : target.getFields())
            if (fMap.containsKey(field.getName()) == false)
                fMap.put(field.getName(), field);
        Class<?> superType = target.getDeclaringClass();
        if (superType == null || superType == target)
            return;
        findALLFieldsNoRepeat(superType, fMap);
    }
    /**获取类定义的方法和继承父类中定义的方法以及父类的父类（子类的重写方法也会被返回）。*/
    public static List<Method> findALLMethods(Class<?> target) {
        if (target == null)
            return null;
        ArrayList<Method> mList = new ArrayList<Method>();
        findALLMethods(target, mList);
        return mList;
    }
    private static void findALLMethods(Class<?> target, ArrayList<Method> mList) {
        if (target == null)
            return;
        for (Method method : target.getDeclaredMethods())
            if (mList.contains(method) == false)
                mList.add(method);
        for (Method method : target.getMethods())
            if (mList.contains(method) == false)
                mList.add(method);
        Class<?> superType = target.getDeclaringClass();
        if (superType == null || superType == target)
            return;
        findALLMethods(superType, mList);
    }
    /**{@link #findALLMethods(Class))}方法返回值的无重复版本。*/
    public static List<Method> findALLMethodsNoRepeat(Class<?> target) {
        if (target == null)
            return null;
        LinkedHashMap<String, Method> mMap = new LinkedHashMap<String, Method>();
        findALLMethodsNoRepeat(target, mMap);
        return new ArrayList<Method>(mMap.values());
    }
    private static void findALLMethodsNoRepeat(Class<?> target, LinkedHashMap<String, Method> mMap) {
        if (target == null)
            return;
        for (Method method : target.getDeclaredMethods()) {
            String fullDesc = getCodeWithoutClass(method);
            if (mMap.containsKey(fullDesc) == false)
                mMap.put(fullDesc, method);
        }
        for (Method method : target.getMethods()) {
            String fullDesc = getCodeWithoutClass(method);
            if (mMap.containsKey(fullDesc) == false)
                mMap.put(fullDesc, method);
        }
        Class<?> superType = target.getDeclaringClass();
        if (superType == null || superType == target)
            return;
        findALLMethodsNoRepeat(superType, mMap);
    }
    /*----------------------------------------------------------------------------------------*/
    /**查找一个可操作的字段列表。*/
    public static List<Field> getFields(Class<?> type) {
        ArrayList<Field> fList = new ArrayList<Field>();
        for (Field field : type.getFields())
            if (fList.contains(field) == false)
                fList.add(field);
        return fList;
    }
    /**查找一个可操作的方法列表。*/
    public static List<Method> getMethods(Class<?> type) {
        ArrayList<Method> mList = new ArrayList<Method>();
        for (Method method : type.getMethods())
            if (mList.contains(method) == false)
                mList.add(method);
        return mList;
    }
    /**查找一个可操作的字段。*/
    public static Field getField(String fieldName, Class<?> type) {
        if (fieldName == null || type == null)
            return null;
        for (Field f : type.getFields())
            if (f.getName().equals(fieldName) == true)
                return f;
        return null;
    }
    /**查找一个可操作的方法。*/
    public static Method getMethod(Class<?> atClass, String name, Class<?>[] paramType) {
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
    public static List<String> getPropertysAndFields(Class<?> target) {
        List<String> mnames = getPropertys(target);
        List<Field> fnames = getFields(target);
        for (Field f : fnames) {
            String fName = f.getName();
            if (mnames.contains(fName) == false)
                mnames.add(fName);
        }
        return mnames;
    }
    /**获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性。*/
    public static List<String> getPropertys(Class<?> target) {
        List<String> mnames = new ArrayList<String>();
        List<Method> ms = getMethods(target);
        for (Method m : ms) {
            String name = m.getName();
            if (name.startsWith("get") == true || name.startsWith("set") == true)
                name = name.substring(3);
            else if (name.startsWith("is") == true)
                name = name.substring(2);
            else
                continue;
            if (name.equals("") == false) {
                name = StringUtils.toLowerCase(name);
                if (mnames.contains(name) == false)
                    mnames.add(name);
            }
        }
        return mnames;
    }
    /**获取一个属性的读取方法。*/
    public static Method getReadMethod(String property, Class<?> target) {
        if (property == null || target == null)
            return null;
        String methodName_1 = "get" + StringUtils.toUpperCase(property);
        String methodName_2 = "is" + StringUtils.toUpperCase(property);
        //
        for (Method m : target.getMethods())
            if (m.getParameterTypes().length == 0) {
                String methodName = m.getName();
                if (methodName.equals(methodName_1) == true)
                    return m;
                /*是否是布尔*/
                if (methodName.equals(methodName_2) == true) {
                    Class<?> t = m.getReturnType();
                    if (t == Boolean.class || t == boolean.class)
                        return m;
                }
            }
        return null;
    }
    /**获取一个属性的写入方法。*/
    public static Method getWriteMethod(String property, Class<?> target) {
        if (property == null || target == null)
            return null;
        String methodName = "set" + StringUtils.toUpperCase(property);
        for (Method m : target.getMethods())
            if (m.getName().equals(methodName) == true)
                if (m.getParameterTypes().length == 1)
                    return m;
        return null;
    }
    /**测试是否具有propertyName所表示的属性，无论是读或写方法只要存在一个就表示存在该属性。*/
    public static boolean hasProperty(String propertyName, Class<?> target) {
        //get、set方法
        if (getReadMethod(propertyName, target) == null)
            if (getWriteMethod(propertyName, target) == null)
                return false;
        return true;
    }
    /**测试是否具有fieldName所表示的字段，无论是读或写方法只要存在一个就表示存在该属性。*/
    public static boolean hasField(String propertyName, Class<?> target) {
        if (getField(propertyName, target) == null)
            return false;
        else
            return true;
    }
    /**测试是否具有name所表示的属性，hasProperty或hasField有一个返回为true则返回true。*/
    public static boolean hasPropertyOrField(String name, Class<?> target) {
        if (hasProperty(name, target) == false)
            if (hasField(name, target) == false)
                return false;
        return true;
    }
    /**测试是否支持readProperty方法。返回true表示可以进行读取操作。*/
    public static boolean canReadProperty(String propertyName, Class<?> target) {
        Method readMethod = getReadMethod(propertyName, target);
        if (readMethod != null)
            return true;
        else
            return false;
    }
    /**测试是否支持readPropertyOrField方法。*/
    public static boolean canReadPropertyOrField(String propertyName, Class<?> target) {
        if (canReadProperty(propertyName, target) == false)
            if (hasField(propertyName, target) == false)
                return false;
        return true;
    }
    /**测试是否支持writeProperty方法。返回true表示可以进行写入操作。*/
    public static boolean canWriteProperty(String propertyName, Class<?> target) {
        Method writeMethod = getWriteMethod(propertyName, target);
        if (writeMethod != null)
            return true;
        else
            return false;
    }
    /**测试是否支持writePropertyOrField方法。*/
    public static boolean canWritePropertyOrField(String propertyName, Class<?> target) {
        if (canWriteProperty(propertyName, target) == false)
            if (hasField(propertyName, target) == false)
                return false;
        return true;
    }
    /*----------------------------------------------------------------------------------------*/
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。返回值表示执行是否成功。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeProperty(Object object, String attName, Object value) {
        if (object == null || attName == null)
            return false;
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method writeMethod = getWriteMethod(attName, defineType);
        if (writeMethod == null)
            return false;
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object defaultValue = EngineToos.getDefaultValue(toType);
        Object attValueObject = StringConvertUtils.changeType(value, toType, defaultValue);
        //3.执行属性注入
        try {
            writeMethod.invoke(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    };
    /**执行字段注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeField(Object object, String fieldName, Object value) {
        if (object == null || fieldName == null)
            return false;
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field writeField = getField(fieldName, defineType);
        if (writeField == null)
            return false;
        //2.执行属性转换
        Class<?> toType = writeField.getType();
        Object attValueObject = StringConvertUtils.changeType(value, toType);
        //3.执行属性注入
        try {
            writeField.set(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**执行注入，该方法首先会视图执行属性方法注入。如果失败则执行字段注入。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writePropertyOrField(Object object, String attName, Object value) {
        Class<?> defineType = object.getClass();
        if (canWriteProperty(attName, defineType) == true)
            return writeProperty(object, attName, value);//支持方法写入
        if (hasField(attName, defineType) == true)
            return writeField(object, attName, value);//支持字段写入
        return false;
    }
    /**执行属性读取。*/
    public static Object readProperty(Object object, String attName) {
        if (object == null || attName == null)
            return false;
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method readMethod = getReadMethod(attName, defineType);
        if (readMethod == null)
            return null;
        //2.执行属性读取
        try {
            return readMethod.invoke(object);
        } catch (Exception e) {
            return null;
        }
    };
    /**执行字段读取。*/
    public static Object readField(Object object, String fieldName) {
        if (object == null || fieldName == null)
            return null;
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field readField = getField(fieldName, defineType);
        if (readField == null)
            return null;
        //2.执行字段读取
        try {
            return readField.get(object);
        } catch (Exception e) {
            return null;
        }
    }
    /**执行注入，该方法首先会视图执行属性方法注入。如果失败则执行字段注入。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static Object readPropertyOrField(Object object, String attName) {
        Class<?> defineType = object.getClass();
        if (canReadProperty(attName, defineType) == true)
            return readProperty(object, attName);//支持方法读取
        if (hasField(attName, defineType) == true)
            return readField(object, attName);//支持字段读取
        return null;
    }
    /**判断对象是否为空或者是一个空字符串。*/
    public static boolean isNone(Object targetBean) {
        return (targetBean == null || targetBean.equals("") == true) ? true : false;
    }
    /**比较两个值是否相等，同时包含了值为空的判断。该方法使用equals进行判断*/
    public static boolean isEq(Object value1, Object value2) {
        if (value1 == null && value2 != null)
            return false;
        if (value1 != null && value2 == null)
            return false;
        if (value1 == null && value2 == null)
            return true;
        return value1.equals(value2);
    }
    /**比较两个值是否不相等，同时包含了值为空的判断。该方法使用equals进行判断*/
    public static boolean isNe(Object value1, Object value2) {
        return !isEq(value1, value2);
    }
};