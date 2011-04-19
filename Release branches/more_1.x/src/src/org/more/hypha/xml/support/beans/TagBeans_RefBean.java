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
package org.more.hypha.xml.support.beans;
import java.util.Map;
import org.more.core.xml.XmlStackDecorator;
import org.more.hypha.define.beans.RelationBeanDefine;
import org.more.hypha.xml.context.XmlDefineResource;
/**
 * 用于解析/beans/refBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_RefBean extends TagBeans_AbstractBeanDefine<RelationBeanDefine> {
    /**创建{@link TagBeans_RefBean}对象*/
    public TagBeans_RefBean(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link RelationBeanDefine}对象。*/
    protected RelationBeanDefine createDefine(XmlStackDecorator context) {
        return new RelationBeanDefine();
    }
    /**定义引用类型Bean的属性*/
    public enum PropertyKey {
        refBean, refPackage
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.refBean, "refBean");
        propertys.put(PropertyKey.refPackage, "refPackage");
        return propertys;
    }
}