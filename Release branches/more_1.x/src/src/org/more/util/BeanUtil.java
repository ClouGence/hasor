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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class BeanUtil {
    private static final ILog log = LogFactory.getLog(BeanUtil.class);
    /**查找某个名称的共有字段，该方法必须有一个参数。*/
    public static Field findField(String fieldName, Class<?> type) {
        if (fieldName == null || type == null) {
            log.error("fieldName an error , fieldName or type is null, please check it.");
            return null;
        }
        for (Field f : type.getFields())
            if (f.getName().equals(fieldName) == true) {
                log.debug("find method {%0}.", f);
                return f;
            }
        log.debug("{%0} method not exist at {%1}.", fieldName, type);
        return null;
    }
    /**查找某个名称的方法，该方法必须有一个参数。*/
    public static Method findMethod(String methodName, Class<?> type) {
        if (methodName == null || type == null) {
            log.error("findMethod an error , methodName or type is null, please check it.");
            return null;
        }
        for (Method m : type.getMethods())
            if (m.getName().equals(methodName) == true)
                if (m.getParameterTypes().length == 1) {
                    log.debug("find method {%0}.", m);
                    return m;
                }
        log.debug("{%0} method not exist at {%1}.", methodName, type);
        return null;
    }
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。返回值表示执行是否成功。*/
    public static boolean writeProperty(Object object, String attName, Object value) {
        if (object == null || attName == null) {
            log.error("putAttribute an error, object or attName is null, please check it.");
            return false;
        }
        //1.查找方法
        String methodName = "set" + StringUtil.toUpperCase(attName);
        Class<?> defineType = object.getClass();
        Method writeMethod = findMethod(methodName, defineType);
        if (writeMethod == null) {
            log.debug("can`t invoke {%0} , this method not exist on {%1}", methodName, defineType);
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object attValueObject = StringConvertUtil.changeType(value, toType);
        //3.执行属性注入
        try {
            writeMethod.invoke(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    };
    /**执行字段注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。*/
    public static boolean writeField(Object object, String fieldName, Object value) {
        if (object == null || fieldName == null) {
            log.error("putAttribute an error, object or fieldName is null, please check it.");
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field writeField = findField(fieldName, defineType);
        if (writeField == null) {
            log.debug("can`t invoke {%0} , this field not exist on {%1}", fieldName, defineType);
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeField.getType();
        Object attValueObject = StringConvertUtil.changeType(value, toType);
        //3.执行属性注入
        try {
            writeField.set(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**执行注入，该方法首先会视图执行属性方法注入。如果失败则执行字段注入。*/
    public static boolean writePropertyOrField(Object object, String attName, Object value) {
        if (writeField(object, attName, value) == false)
            if (writeProperty(object, attName, value) == false)
                return false;
        return true;
    }
}