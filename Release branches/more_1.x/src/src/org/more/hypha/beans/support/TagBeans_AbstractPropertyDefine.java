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
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.define.AbstractPropertyDefine;
import org.more.hypha.context.XmlDefineResource;
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
    public TagBeans_AbstractPropertyDefine(XmlDefineResource configuration) {
        super(configuration);
    }
    /**属性的定义名称*/
    protected String getAttributeName() {
        return PropertyDefine;
    }
    /**创建定义类型对象。*/
    protected abstract T createDefine(XmlStackDecorator context);
    /**定义通用的属性。*/
    public enum PropertyKey {
        value, boolLazyInit, classType, description
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        propertys.put(PropertyKey.classType, "type");
        propertys.put(PropertyKey.description, "description");
        return propertys;
    }
    /**开始解析属性标签。*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        //1.解析属性
        super.beginElement(context, xpath, event);
        //2.处理特殊属性classType。
        AbstractPropertyDefine pdefine = this.getDefine(context);
        //3.将元素定义的所有属性都添加到att中。
        AttBase att = new AttBase();
        for (int i = 0; i < event.getAttributeCount(); i++)
            att.put(event.getAttributeName(i).getLocalPart(), event.getAttributeValue(i));
        //4.负责解析属性值元信息，当具有value、date、bigText、ref、file、directory、uri、el等标签对属性进行描述时会自动覆盖这里的解析。
        TypeManager typeManager = this.getDefineResource().getTypeManager();
        ValueMetaData valueMETADATA = typeManager.parserType(att, pdefine);
        if (valueMETADATA == null)
            throw new NullPointerException("通过TypeManager解析属性值元信息失败！返回值为空。");
        pdefine.setValueMetaData(valueMETADATA);
    }
}