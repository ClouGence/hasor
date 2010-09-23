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
import java.util.Map;
import org.more.DoesSupportException;
import org.more.beans.define.VariableBeanDefine;
import org.more.beans.resource.AbstractXmlConfiguration;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.util.StringConvert;
/**
 * 用于解析/beans/varBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_VarBean extends TagBeans_AbstractBeanDefine<VariableBeanDefine> {
    /**创建{@link TagBeans_VarBean}对象*/
    public TagBeans_VarBean(AbstractXmlConfiguration configuration) {
        super(configuration);
    }
    /**创建VariableBeanDefine类型对象。*/
    protected VariableBeanDefine createDefine() {
        return new VariableBeanDefine();
    }
    /**定义值Bean的属性*/
    public enum PropertyKey {
        value, type, format
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        return null;
    }
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        //1.取得属性
        String _value = event.getAttributeValue("value");
        String _type = event.getAttributeValue("type");
        String _format = event.getAttributeValue("format");
        //2.转换type
        VariableType typeEnum = (VariableType) StringConvert.changeType(_type, VariableType.class, VariableType.Null);
        Class<?> classType = TagBeans_AbstractPropertyDefine.getBaseType(typeEnum);
        if (classType == null && _type != null)
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                classType = loader.loadClass(_type);
            } catch (Exception e) {
                throw new DoesSupportException("属性类型[" + _type + "]丢失.", e);
            }
        //3.取得值
        Object value = null;
        if (typeEnum == VariableType.Date)
            value = StringConvert.parseDate(_value, _format);
        else
            value = StringConvert.changeType(_value, classType);
        //4.设置值
        VariableBeanDefine define = this.getDefine(context);
        define.setType(classType);
        define.setValue(value);
    }
}