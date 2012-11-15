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
package org.more.hypha.xml.tags.aop;
import org.more.core.error.LostException;
import org.more.core.xml.XmlAttributeHook;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.AttributeEvent;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.aop.AopService;
import org.more.hypha.define.AopConfigDefine;
import org.more.hypha.xml.XmlDefineResource;
import org.more.hypha.xml.tags.beans.TagBeans_AbstractBeanDefine;
/**
 * 用于解析aop:useConfig标签和useConfig属性
 * @version 2010-10-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_UseConfig extends TagAop_NS implements XmlElementHook, XmlAttributeHook {
    /**创建{@link TagAop_UseConfig}对象*/
    public TagAop_UseConfig(XmlDefineResource configuration) {
        super(configuration);
    }
    /**处理标签形式。*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String name = event.getAttributeValue("name");
        this.processElement(context, name);
    }
    /**处理属性形式。*/
    public void attribute(XmlStackDecorator<Object> context, String xpath, AttributeEvent event) {
        String name = event.getValue();
        this.processElement(context, name);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
    /**处理aop:useConfig*/
    private void processElement(XmlStackDecorator<Object> context, String name) {
        AopService service = this.getAopConfig();
        BeanDefine bean = (BeanDefine) context.getAttribute(TagBeans_AbstractBeanDefine.BeanDefine);
        //
        AopConfigDefine aopConfig = service.getAopDefine(name);
        if (aopConfig == null)
            throw new LostException("useConfig 在[" + bean.getName() + "]上的[" + name + "]配置，无法在aop配置库中找到。");
        service.setAop(bean, aopConfig);
    }
}