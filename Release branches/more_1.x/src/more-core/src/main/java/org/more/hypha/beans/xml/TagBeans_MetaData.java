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
package org.more.hypha.beans.xml;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析meta标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_MetaData extends TagBeans_NS implements XmlElementHook {
    /**创建{@link TagBeans_MetaData}对象*/
    public TagBeans_MetaData(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String key = event.getAttributeValue("key");
        String value = event.getAttributeValue("value");
        //1.属性值定义
        ValueMetaData meta_Define = (ValueMetaData) context.getAttribute(TagBeans_AbstractValueMetaDataDefine.ValueMetaDataDefine);
        if (meta_Define != null) {
            meta_Define.setAttribute(key, value);
            return;
        }
        //2.属性
        AbstractPropertyDefine prop_Define = (AbstractPropertyDefine) context.getAttribute(TagBeans_AbstractPropertyDefine.PropertyDefine);
        if (prop_Define != null) {
            prop_Define.setAttribute(key, value);
            return;
        }
        //3.Bean定义
        AbstractBeanDefine bean_Define = (AbstractBeanDefine) context.getAttribute(TagBeans_AbstractBeanDefine.BeanDefine);
        if (bean_Define != null) {
            bean_Define.setAttribute(key, value);
            return;
        }
        //4.config定义
        if (xpath.contains("/beans") == true)
            this.getDefineResource().setAttribute(key, value);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}