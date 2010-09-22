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
package org.more.beans.resource.namespace;
import java.lang.reflect.Method;
import java.util.Map;
import org.more.DoesSupportException;
import org.more.PropertyException;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.util.StringConvert;
import org.more.util.StringUtil;
import org.more.util.attribute.StackDecorator;
/**
 * 各种bean的基本解析器，在该类中定义了一些工具性的方法。
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractDefine implements XmlElementHook {
    /**定义名称*/
    protected abstract String getDefineName();
    /**创建定义类型对象。*/
    protected abstract Object createDefine(StackDecorator context);
    /**获取一个定义，如果没有就调用createDefine方法创建它。*/
    protected final Object getDefine(StackDecorator context) {
        String defineName = getDefineName();
        Object define = context.getAttribute(defineName);
        if (define != null)
            return define;
        define = this.createDefine(context);
        context.setAttribute(defineName, define);
        return define;
    }
    /**该方法返回一个Map，Map的key定义的是定义中声明的属性，而Value中保存的是对应的XML元素属性名。*/
    protected abstract Map<Enum<?>, String> getPropertyMappings();
    /**查找某个名称的方法，该方法必须有一个参数。*/
    private Method findMethod(String methodName, Class<?> type) {
        for (Method m : type.getMethods())
            if (m.getName().equals(methodName) == true)
                if (m.getParameterTypes().length == 1)
                    return m;
        return null;
    }
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。*/
    protected final void putAttribute(Object define, String attName, Object value) {
        if (define == null || attName == null)
            throw new NullPointerException("定义对象或者要注入的属性名为空。");
        //1.查找方法
        String methodName = "set" + StringUtil.toUpperCase(attName);
        Method writeMethod = this.findMethod(methodName, define.getClass());
        if (writeMethod == null)
            throw new DoesSupportException(define.getClass().getSimpleName() + "：定义中不存在[" + methodName + "]方法。");
        try {
            //2.执行属性转换
            Class<?> toType = writeMethod.getParameterTypes()[0];
            Object attValueObject = StringConvert.changeType(value, toType);
            //3.执行属性注入
            writeMethod.invoke(define, attValueObject);
        } catch (Exception e) {
            throw new PropertyException("无法将Xml中定义的" + attName + ",属性写入[" + define + "]的定义.", e);
        }
    };
    /**开始解析标签，其中包括创建对应Bean和解析各个属性。*/
    public void beginElement(StackDecorator context, String xpath, StartElementEvent event) {
        context.createStack();
        //1.创建BeanDefine
        Object define = this.getDefine(context);
        //2.设置BeanDefine的值
        Map<Enum<?>, String> propertys = this.getPropertyMappings();
        if (propertys == null)
            return;
        for (Enum<?> att : propertys.keySet()) {
            String definePropertyName = att.name();
            String xmlPropertyName = propertys.get(att);
            //
            String xmlPropertyValue = event.getAttributeValue(xmlPropertyName);
            if (xmlPropertyValue == null)
                continue;
            this.putAttribute(define, definePropertyName, xmlPropertyValue);
        }
    };
    /**结束解析标签。*/
    public void endElement(StackDecorator context, String xpath, EndElementEvent event) {
        context.dropStack();
    }
}