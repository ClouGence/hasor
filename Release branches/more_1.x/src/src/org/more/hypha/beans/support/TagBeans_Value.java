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
import org.more.LostException;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.define.AbstractPropertyDefine;
import org.more.hypha.beans.define.Simple_ValueMetaData;
import org.more.hypha.configuration.DefineResourceImpl;
import org.more.util.StringConvert;
/**
 * 用于解析value标签
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Value extends TagBeans_AbstractValueMetaDataDefine<Simple_ValueMetaData> {
    /**创建{@link TagBeans_Value}对象*/
    public TagBeans_Value(DefineResourceImpl configuration) {
        super(configuration);
    }
    protected Simple_ValueMetaData createDefine() {
        return new Simple_ValueMetaData();
    }
    /**定义属性。*/
    public enum PropertyKey {
        value, type
    };
    /**定义模板属性。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        return null;
    }
    /**开始标签*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        AbstractPropertyDefine pdefine = (AbstractPropertyDefine) context.getAttribute(TagBeans_AbstractPropertyDefine.PropertyDefine);
        //1.准备数据
        String propTypeString = event.getAttributeValue("type");
        String propValueString = event.getAttributeValue("value");
        Class<?> propType = null;
        Object propValue = null;
        //2.解析type
        if (propTypeString != null) {
            VariableType typeEnum = (VariableType) StringConvert.changeType(propTypeString, VariableType.class);
            if (typeEnum != null)
                propType = getBaseType(typeEnum);
            else
                try {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    propType = loader.loadClass(propTypeString);
                    pdefine.setClassType(propType);
                } catch (ClassNotFoundException e) {
                    throw new LostException("ClassNotFoundException,属性类型[" + propTypeString + "]丢失.", e);
                }
        } else
            propType = pdefine.getClassType();
        //
        if (propValueString != null)
            if (propType == null)
                /*当检测到value有值但是又没有定义type时候值类型采用的默认数据类型。*/
                propType = Simple_ValueMetaData.DefaultValueType;
        //3.转换属性值
        propValue = StringConvert.changeType(propValueString, propType);
        //4.设置属性值
        Simple_ValueMetaData newMEDATA = this.getDefine(context);
        newMEDATA.setValue(propValue);
        newMEDATA.setValueMetaType(Simple_ValueMetaData.getPropertyType(propType));
    }
}