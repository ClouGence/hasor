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
import java.util.HashMap;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.beans.AbstractBeanDefine;
import org.more.beans.resource.AbstractXmlConfiguration;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
/**
 * 用于解析/beans/*Bean标签的基类
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractBeanDefine<T extends AbstractBeanDefine> extends TagBeans_AbstractDefine<T> {
    /**创建{@link TagBeans_AbstractBeanDefine}对象*/
    public TagBeans_AbstractBeanDefine(AbstractXmlConfiguration configuration) {
        super(configuration);
    }
    /**保存于上下文中的扫描对象。*/
    public static final String BeanDefine = "$more_BeanDefine";
    /**获取保存于上下文中的扫描对象名*/
    protected String getAttributeName() {
        return BeanDefine;
    };
    /**定义模板属性。*/
    public enum PropertyKey {
        name, iocType, scope, boolAbstract, boolInterface, boolSingleton, boolLazyInit, description, factoryName, factoryMethod, useTemplate
    }
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        propertys.put(PropertyKey.name, "name");
        propertys.put(PropertyKey.iocType, "iocType");
        propertys.put(PropertyKey.scope, "scope");
        propertys.put(PropertyKey.boolAbstract, "abstract");
        propertys.put(PropertyKey.boolInterface, "interface");
        propertys.put(PropertyKey.boolSingleton, "singleton");
        propertys.put(PropertyKey.boolLazyInit, "lazy");
        propertys.put(PropertyKey.description, "description");
        propertys.put(PropertyKey.factoryName, "factoryName");
        propertys.put(PropertyKey.factoryMethod, "factoryMethod");
        //propertys.put(PropertyKey.useTemplate, "useTemplate");
        return propertys;
    }
    /**特殊处理下useTemplate属性的注入*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        String useTemplate = event.getAttributeValue("useTemplate");
        if (useTemplate != null) {
            AbstractXmlConfiguration beanDefineManager = (AbstractXmlConfiguration) context.getAttribute(TagBeans_Beans.BeanDefineManager);
            AbstractBeanDefine define = this.getDefine(context);
            AbstractBeanDefine template = beanDefineManager.getBeanDefine(useTemplate);
            if (template == null)
                throw new NoDefinitionException("找不到[" + useTemplate + "]的Bean模板定义.");
            this.putAttribute(define, "useTemplate", template);
        }
    }
    /**结束解析标签。*/
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        AbstractBeanDefine define = this.getDefine(context);
        //context.removeAttribute(this.getAttributeName());//TODO 不需要remove的原因是super.endElement方法会销毁当前栈
        AbstractXmlConfiguration beanDefineManager = (AbstractXmlConfiguration) context.getAttribute(TagBeans_Beans.BeanDefineManager);
        if (define != null)
            beanDefineManager.addBeanDefine(define);
        super.endElement(context, xpath, event);
    }
}