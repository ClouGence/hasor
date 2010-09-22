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
import org.more.beans.define.ClassBeanDefine;
import org.more.core.xml.stream.StartElementEvent;
import org.more.util.attribute.StackDecorator;
/**
 * 用于解析/beans/classBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_ClassBean extends TagBeans_TemplateBean {
    /**创建{@link ClassBeanDefine}对象。*/
    protected Object createDefine(StackDecorator context) {
        return new ClassBeanDefine();
    }
    /**定义类型Bean特有属性。*/
    public enum PropertyKey {
        source,
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        //propertys.put(PropertyKey.source, "class");
        return propertys;
    }
    public void beginElement(StackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String source = event.getAttributeValue("class");
        ClassBeanDefine define = (ClassBeanDefine) this.getDefine(context);
        try {
            define.setSource(loader.loadClass(source));
        } catch (Exception e) {
            throw new DoesSupportException("Bean类型[" + source + "]丢失.", e);
        }
    }
}