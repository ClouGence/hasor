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
package org.more.hypha.beans.support;
import java.util.Map;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.configuration.Tag_Abstract;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * bases命名空间解析器的基类，在该类中定义了一些工具性的方法。
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public abstract class TagBeans_AbstractDefine<T> extends Tag_Abstract implements XmlElementHook {
    /**创建{@link TagBeans_AbstractDefine}对象*/
    public TagBeans_AbstractDefine(XmlConfiguration configuration) {
        super(configuration);
    }
    /**如果isPutAttribute方法返回true则设置到{@link XmlStackDecorator}属性范围中的属性名由该方法确定。*/
    protected abstract String getAttributeName();
    /**创建定义类型对象。*/
    protected abstract T createDefine();
    /**
     * 每个标签在beginElement开始执行时都会创建一个自己的堆，当遇到endElement方法调用时候会销毁这个堆。
     * 该方法的返回值可以确定是否跨越{@link XmlStackDecorator}的当前堆去上一层中寻找{@link T},默认值是true。
     */
    protected boolean isSpanStack() {
        return true;
    }
    /**获取一个定义，如果没有就调用createDefine方法创建它。*/
    protected final T getDefine(XmlStackDecorator context) {
        String defineName = this.getAttributeName();
        boolean spanStack = this.isSpanStack();
        T define = null;
        if (spanStack == true)
            define = (T) context.getAttribute(defineName);
        else
            define = (T) context.getSource().getAttribute(defineName);
        //
        if (define == null) {
            define = this.createDefine();
            context.setAttribute(defineName, define);
        }
        return define;
    }
    /**该方法返回一个Map，Map的key定义的是定义中声明的属性，而Value中保存的是对应的XML元素属性名。*/
    protected abstract Map<Enum<?>, String> getPropertyMappings();
    /**开始解析标签，其中包括创建对应Bean和解析各个属性。*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        context.createStack();
        //1.获取Define
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
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        context.dropStack();
    }
}