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
import java.util.Map;
import org.more.core.xml.XmlStackDecorator;
import org.more.hypha.beans.define.GenerateBeanDefine;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析/beans/generateBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_GenerateBean extends TagBeans_AbstractBeanDefine<GenerateBeanDefine> {
    /**创建{@link TagBeans_GenerateBean}对象*/
    public TagBeans_GenerateBean(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link GenerateBeanDefine}对象。*/
    protected GenerateBeanDefine createDefine(XmlStackDecorator context) {
        return new GenerateBeanDefine();
    }
    /**定义生成Bean的属性。*/
    public enum PropertyKey {
        nameStrategy, aopStrategy, delegateStrategy, methodStrategy, propertyStrategy,
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.nameStrategy, "nameStrategy");
        propertys.put(PropertyKey.aopStrategy, "aopStrategy");
        propertys.put(PropertyKey.delegateStrategy, "delegateStrategy");
        propertys.put(PropertyKey.methodStrategy, "methodStrategy");
        propertys.put(PropertyKey.propertyStrategy, "propertyStrategy");
        return propertys;
    }
}