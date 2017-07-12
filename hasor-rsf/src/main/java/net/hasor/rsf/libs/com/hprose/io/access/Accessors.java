/**********************************************************\
 |                                                          |
 |                          hprose                          |
 |                                                          |
 | Official WebSite: http://www.hprose.com/                 |
 |                   http://www.hprose.org/                 |
 |                                                          |
 \**********************************************************/
/**********************************************************\
 *                                                        *
 * Accessors.java                                         *
 *                                                        *
 * Accessors class for Java.                              *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.HproseMode;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;
import net.hasor.rsf.libs.com.hprose.utils.LinkedCaseInsensitiveMap;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public final class Accessors {
    private final static ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>> propertiesCache = new ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>>();
    private final static ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>> membersCache    = new ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>>();
    private final static ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>> fieldsCache     = new ConcurrentHashMap<Type, LinkedCaseInsensitiveMap<String, MemberAccessor>>();
    private static sun.misc.Unsafe getUnsafe() {
        try {
            return sun.misc.Unsafe.getUnsafe();
        } catch (Exception e) {
        }
        try {
            Class<sun.misc.Unsafe> k = sun.misc.Unsafe.class;
            for (Field f : k.getDeclaredFields()) {
                f.setAccessible(true);
                Object x = f.get(null);
                if (k.isInstance(x))
                    return k.cast(x);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    final static sun.misc.Unsafe unsafe = getUnsafe();
    public final static boolean isAndroid() {
        String vmName = System.getProperty("java.vm.name");
        if (vmName == null) {
            return false;
        }
        String lowerVMName = vmName.toLowerCase();
        return lowerVMName.contains("dalvik") || lowerVMName.contains("lemur");
    }
    private static Method findGetter(Method[] methods, String name, Class<?> paramType) {
        String getterName = "get" + name;
        String isGetterName = "is" + name;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            String methodName = method.getName();
            if (!methodName.equals(getterName) && !methodName.equals(isGetterName)) {
                continue;
            }
            if (!method.getReturnType().equals(paramType)) {
                continue;
            }
            if (method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }
    private static Map<String, MemberAccessor> getProperties(Type type) {
        LinkedCaseInsensitiveMap<String, MemberAccessor> properties = propertiesCache.get(type);
        if (properties == null) {
            properties = new LinkedCaseInsensitiveMap<String, MemberAccessor>();
            Method[] methods = ClassUtil.toClass(type).getMethods();
            for (Method setter : methods) {
                if (Modifier.isStatic(setter.getModifiers())) {
                    continue;
                }
                String name = setter.getName();
                if (!name.startsWith("set")) {
                    continue;
                }
                if (!setter.getReturnType().equals(void.class)) {
                    continue;
                }
                Class<?>[] paramTypes = setter.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                String propertyName = name.substring(3);
                Method getter = findGetter(methods, propertyName, paramTypes[0]);
                if (getter != null) {
                    PropertyAccessor propertyAccessor = new PropertyAccessor(type, getter, setter);
                    char[] cname = propertyName.toCharArray();
                    cname[0] = Character.toLowerCase(cname[0]);
                    propertyName = new String(cname);
                    properties.put(propertyName, propertyAccessor);
                }
            }
            propertiesCache.put(type, properties);
        }
        return properties;
    }
    private static MemberAccessor getFieldAccessor(Type type, Field field) {
        if (unsafe != null && !isAndroid()) {
            Class<?> cls = field.getType();
            if (cls == int.class) {
                return new IntFieldAccessor(field);
            }
            if (cls == byte.class) {
                return new ByteFieldAccessor(field);
            }
            if (cls == short.class) {
                return new ShortFieldAccessor(field);
            }
            if (cls == long.class) {
                return new LongFieldAccessor(field);
            }
            if (cls == boolean.class) {
                return new BoolFieldAccessor(field);
            }
            if (cls == char.class) {
                return new CharFieldAccessor(field);
            }
            if (cls == float.class) {
                return new FloatFieldAccessor(field);
            }
            if (cls == double.class) {
                return new DoubleFieldAccessor(field);
            }
            return new FieldAccessor(type, field);
        }
        return new SafeFieldAccessor(type, field);
    }
    private static Map<String, MemberAccessor> getFields(Type type) {
        LinkedCaseInsensitiveMap<String, MemberAccessor> fields = fieldsCache.get(type);
        if (fields == null) {
            fields = new LinkedCaseInsensitiveMap<String, MemberAccessor>();
            for (Class<?> clazz = ClassUtil.toClass(type); clazz != null; clazz = clazz.getSuperclass()) {
                Field[] fs = clazz.getDeclaredFields();
                for (Field field : fs) {
                    int mod = field.getModifiers();
                    if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                        String fieldName = field.getName();
                        fields.put(fieldName, getFieldAccessor(type, field));
                    }
                }
            }
            fieldsCache.put(type, fields);
        }
        return fields;
    }
    private static Map<String, MemberAccessor> getMembers(Type type) {
        LinkedCaseInsensitiveMap<String, MemberAccessor> members = membersCache.get(type);
        if (members == null) {
            Class<?> clazz = ClassUtil.toClass(type);
            members = new LinkedCaseInsensitiveMap<String, MemberAccessor>();
            Method[] methods = clazz.getMethods();
            for (Method setter : methods) {
                if (Modifier.isStatic(setter.getModifiers())) {
                    continue;
                }
                String name = setter.getName();
                if (!name.startsWith("set")) {
                    continue;
                }
                if (!setter.getReturnType().equals(void.class)) {
                    continue;
                }
                Class<?>[] paramTypes = setter.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                String propertyName = name.substring(3);
                Method getter = findGetter(methods, propertyName, paramTypes[0]);
                if (getter != null) {
                    PropertyAccessor propertyAccessor = new PropertyAccessor(type, getter, setter);
                    char[] cname = propertyName.toCharArray();
                    cname[0] = Character.toLowerCase(cname[0]);
                    propertyName = new String(cname);
                    members.put(propertyName, propertyAccessor);
                }
            }
            Field[] fs = clazz.getFields();
            for (Field field : fs) {
                int mod = field.getModifiers();
                if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                    String fieldName = field.getName();
                    members.put(fieldName, getFieldAccessor(type, field));
                }
            }
            membersCache.put(type, members);
        }
        return members;
    }
    public final static Map<String, MemberAccessor> getMembers(Type type, HproseMode mode) {
        Class<?> clazz = ClassUtil.toClass(type);
        return ((mode != HproseMode.MemberMode) && Serializable.class.isAssignableFrom(clazz)) ? (mode == HproseMode.FieldMode) ? getFields(type) : getProperties(type) : getMembers(type);
    }
}