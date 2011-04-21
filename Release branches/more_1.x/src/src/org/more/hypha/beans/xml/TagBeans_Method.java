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
import java.util.HashMap;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.hypha.beans.define.MethodDefine;
import org.more.hypha.beans.define.TemplateBeanDefine;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 解析method标签。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Method extends TagBeans_AbstractDefine<MethodDefine> {
    /**属性元信息.*/
    public static final String MethodDefine = "$more_Beans_MethodDefine";
    /**创建{@link TagBeans_Method}对象。*/
    public TagBeans_Method(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link MethodDefine}对象*/
    protected MethodDefine createDefine(XmlStackDecorator context) {
        TemplateBeanDefine define = (TemplateBeanDefine) context.getAttribute(TagBeans_TemplateBean.BeanDefine);
        return new MethodDefine(define);
    }
    /**属性的定义名称*/
    protected String getAttributeName() {
        return MethodDefine;
    }
    /**定义方法的属性。*/
    public enum PropertyKey {
        name, codeName, boolStatic
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        propertys.put(PropertyKey.name, "name");
        propertys.put(PropertyKey.codeName, "codeName");
        propertys.put(PropertyKey.boolStatic, "static");
        return propertys;
    }
    /**将属性注册到Bean中。*/
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        MethodDefine method = this.getDefine(context);
        TemplateBeanDefine define = (TemplateBeanDefine) context.getAttribute(TagBeans_TemplateBean.BeanDefine);
        //
        if (method.getCodeName() == null)
            throw new NoDefinitionException("[" + define.getName() + "]的方法定义未定义codeName属性。");
        if (method.getName() == null)
            this.putAttribute(method, "name", method.getCodeName());
        //
        define.addMethod(method);
        super.endElement(context, xpath, event);
    }
}