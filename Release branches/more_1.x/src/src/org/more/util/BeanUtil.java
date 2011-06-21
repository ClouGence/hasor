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
import java.lang.reflect.Method;
import org.more.core.error.PropertyException;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class BeanUtil {
    private static final ILog log = LogFactory.getLog(BeanUtil.class);
    /**查找某个名称的方法，该方法必须有一个参数。*/
    private static Method findMethod(String methodName, Class<?> type) {
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
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。*/
    public static void writeProperty(Object object, String attName, Object value) {
        if (object == null || attName == null) {
            log.error("putAttribute an error, object or attName is null, please check it.");
            return;
        }
        //1.查找方法
        String methodName = "set" + StringUtil.toUpperCase(attName);
        Class<?> defineType = object.getClass();
        Method writeMethod = findMethod(methodName, defineType);
        if (writeMethod == null) {
            log.warning("can`t invoke {%0} , this method not exist on {%1}", methodName, defineType);
            return;
        }
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object attValueObject = StringConvertUtil.changeType(value, toType);
        //3.执行属性注入
        try {
            writeMethod.invoke(object, attValueObject);
            log.debug("putAttribute OK! name = {%0} , newValue = {%1}.", attName, attValueObject);
        } catch (Exception e) {
            log.error("putAttribute {%0} an error! error = {%1}", attName, e);
            throw new PropertyException("putAttribute '" + attName + "' an error!", e);
        }
    };
}