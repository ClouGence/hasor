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
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.hypha.beans.define.VariableBeanDefine;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析/beans/varBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_VarBean extends TagBeans_AbstractBeanDefine<VariableBeanDefine> {
    /**创建{@link TagBeans_VarBean}对象*/
    public TagBeans_VarBean(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建VariableBeanDefine类型对象。*/
    protected VariableBeanDefine createDefine(XmlStackDecorator<Object> context) {
        return new VariableBeanDefine();
    }
    /**定义值Bean的属性*/
    public enum PropertyKey {
        value, type
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.value, "value");
        propertys.put(PropertyKey.type, "type");
        return propertys;
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {
        VariableBeanDefine define = this.getDefine(context);
        if (define.getType() == null)
            throw new FormatException("解析VariableBeanDefine类型Bean错误，无法解析其类型。");
        define.setBoolAbstract(false);
        define.setIocEngine("Ioc");
        define.setFactoryBean(null);
        define.setFactoryMethod(null);
        define.setInitMethod(null);
        define.setDestroyMethod(null);
        define.setUseTemplate(null);
        define.setBoolCheckType(false);
        super.endElement(context, xpath, event);
    }
}