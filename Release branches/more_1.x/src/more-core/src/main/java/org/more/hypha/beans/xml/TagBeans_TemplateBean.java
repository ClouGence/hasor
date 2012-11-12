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
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.define.TemplateBeanDefine;
/**
 * 用于解析/beans/templateBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_TemplateBean extends TagBeans_AbstractBeanDefine<TemplateBeanDefine> {
    /**创建{@link TagBeans_TemplateBean}对象*/
    public TagBeans_TemplateBean(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link TemplateBeanDefine}对象。*/
    protected TemplateBeanDefine createDefine(XmlStackDecorator<Object> context) {
        return new TemplateBeanDefine();
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {
        TemplateBeanDefine define = this.getDefine(context);
        define.setBoolAbstract(true);
        define.setBoolSingleton(false);
        define.setBoolLazyInit(true);
        define.setIocEngine("Ioc");
        define.setFactoryBean(null);
        define.setFactoryMethod(null);
        define.setInitMethod(null);
        define.setDestroyMethod(null);
        define.setBoolCheckType(false);
        super.endElement(context, xpath, event);
    };
}