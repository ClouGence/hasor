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
import org.more.core.xml.XmlStackDecorator;
import org.more.hypha.beans.define.ClassPathBeanDefine;
import org.more.hypha.context.XmlDefineResource;
/**
 * 用于解析/beans/classBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_ClassBean extends TagBeans_AbstractBeanDefine<ClassPathBeanDefine> {
    /**创建{@link TagBeans_ClassBean}对象*/
    public TagBeans_ClassBean(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link ClassPathBeanDefine}对象。*/
    protected ClassPathBeanDefine createDefine(XmlStackDecorator context) {
        return new ClassPathBeanDefine();
    }
    /**定义类型Bean特有属性。*/
    public enum PropertyKey {
        source,
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.source, "class");
        return propertys;
    }
}