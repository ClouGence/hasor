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
import java.util.HashMap;
import java.util.Map;
import org.more.LostException;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.define.AbstractPropertyDefine;
import org.more.hypha.configuration.DefineResourceImpl;
import org.more.util.StringConvert;
import org.more.util.attribute.AttBase;
/**
 * beans命名空间的属性标签解析基类。该类不会处理属性值元信息的解析这部分信息的解析交给其专有标签解析器或者由{@link QuickPropertyParser}接口负责处理。
 * @version 2010-9-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractPropertyDefine<T extends AbstractPropertyDefine> extends TagBeans_AbstractDefine<T> {
    /**属性元信息.*/
    public static final String PropertyDefine = "$more_Beans_PropertyDefine";
    /**创建{@link TagBeans_AbstractPropertyDefine}对象*/
    public TagBeans_AbstractPropertyDefine(DefineResourceImpl configuration) {
        super(configuration);
    }
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
                    throw new LostException("ClassNotFoundException,属性类型[" + classType + "]无法被装载.", e);
                }
            pdefine.setClassType(propType);
        }
        //3.将元素定义的所有属性都添加到att中。
        AttBase att = new AttBase();
        for (int i = 0; i < event.getAttributeCount(); i++)
            att.put(event.getAttributeName(i).getLocalPart(), event.getAttributeValue(i));
        //4.负责解析属性值元信息
        TypeManager typeManager = this.getConfiguration().getTypeManager();
        ValueMetaData valueMETADATA = typeManager.parserType(event.getAttributeValue("value"), att, pdefine);
        if (valueMETADATA == null)
            throw new NullPointerException("通过TypeManager解析属性元信息类型失败，返回值为空。");
        pdefine.setValueMetaData(valueMETADATA);
    }
}