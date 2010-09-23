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
import java.util.List;
import java.util.Map;
import org.more.CheckException;
import org.more.LostException;
import org.more.beans.AbstractBeanDefine;
import org.more.beans.ValueMetaData;
import org.more.beans.define.AbstractPropertyDefine;
import org.more.beans.define.QuickProperty_ValueMetaData;
import org.more.beans.define.Simple_ValueMetaData;
import org.more.beans.define.Simple_ValueMetaData.PropertyType;
import org.more.beans.resource.AbstractXmlConfiguration;
import org.more.beans.resource.QuickParserEvent;
import org.more.beans.resource.QuickPropertyParser;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.util.StringConvert;
/**
 * beans命名空间的属性标签解析基类。该类不会处理属性值元信息的解析这部分信息的解析交给其专有标签解析器或者由{@link QuickPropertyParser}接口负责处理。
 * @version 2010-9-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractPropertyDefine<T extends AbstractPropertyDefine> extends TagBeans_AbstractDefine<T> {
    /**创建{@link TagBeans_AbstractPropertyDefine}对象*/
    public TagBeans_AbstractPropertyDefine(AbstractXmlConfiguration configuration) {
        super(configuration);
    }
    /**属性元信息.*/
    public static final String PropertyDefine = "$more_PropertyDefine";
    /**属性的定义名称*/
    protected String getAttributeName() {
        return PropertyDefine;
    }
    /**创建定义类型对象。*/
    protected abstract T createDefine();
    /**定义通用的属性。*/
    public enum PropertyKey {
        value, boolLazyInit, classType, description
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        //propertys.put(PropertyKey.classType, "type");
        propertys.put(PropertyKey.description, "description");
        return propertys;
    }
    /**开始解析属性标签。*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        //1.解析属性
        super.beginElement(context, xpath, event);
        //2.处理特殊属性classType。
        AbstractPropertyDefine pdefine = this.getDefine(context);
        {
            //1).试图将type转换为VariableType枚举.
            String classType = event.getAttributeValue("type");
            if (classType == null)
                classType = "null";
            VariableType typeEnum = (VariableType) StringConvert.changeType(classType, VariableType.class);
            //2).如果转换失败则直接使用ClassLoader装载.
            Class<?> propType = null;
            if (typeEnum != null)
                propType = getBaseType(typeEnum);
            else
                try {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    propType = loader.loadClass(classType);
                } catch (Exception e) {
                    throw new LostException("ClassNotFoundException,属性类型[" + classType + "]丢失.", e);
                }
            pdefine.setClassType(propType);
        }
        //3.负责生成临时属性值元信息
        QuickProperty_ValueMetaData quickMETA = new QuickProperty_ValueMetaData();
        quickMETA.setValue(event.getAttributeValue("value"));
        quickMETA.setEnumeration(event.getAttributeValue("enum"));
        quickMETA.setRefBean(event.getAttributeValue("refBean"));
        quickMETA.setRefScope(event.getAttributeValue("refScope"));
        quickMETA.setFile(event.getAttributeValue("file"));
        quickMETA.setDirectory(event.getAttributeValue("directory"));
        quickMETA.setUriLocation(event.getAttributeValue("uriLocation"));
        quickMETA.setDate(event.getAttributeValue("date"));
        quickMETA.setFormat(event.getAttributeValue("format"));
        //4.负责解析临时属性值元信息
        List<QuickPropertyParser> quickList = this.getConfiguration().getQuickList();
        /**调用快速属性解析器解析属性值元信息*/
        AbstractBeanDefine define = (AbstractBeanDefine) context.getAttribute(TagBeans_AbstractBeanDefine.BeanDefine);
        QuickParserEvent quickEvent = new QuickParserEvent(this.getConfiguration(), define, pdefine, quickMETA);
        ValueMetaData valueMETADATA = null;
        for (QuickPropertyParser parser : quickList) {
            try {
                valueMETADATA = parser.parser(quickEvent);
            } catch (Throwable e) {
                throw new CheckException("[" + define.getName() + "]在执行二次解析过程发生异常", e);
            }
            if (valueMETADATA != null)
                break;
        }
        /**使用默认值null*/
        if (valueMETADATA == null) {
            Simple_ValueMetaData simple = new Simple_ValueMetaData();
            simple.setValueMetaType(PropertyType.Null);
            simple.setValue(null);
            valueMETADATA = simple;
        }
        pdefine.setValueMetaData(valueMETADATA);
    }
}