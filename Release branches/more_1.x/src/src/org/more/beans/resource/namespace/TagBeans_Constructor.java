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
import org.more.beans.define.ConstructorDefine;
import org.more.beans.define.TemplateBeanDefine;
import org.more.core.xml.stream.EndElementEvent;
import org.more.util.attribute.StackDecorator;
/**
 * 用于解析property标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Constructor extends TagBeans_AbstractPropertyDefine {
    /**创建{@link ConstructorDefine}对象。*/
    protected Object createDefine(StackDecorator context) {
        return new ConstructorDefine();
    }
    /**定义构造方法特有属性。*/
    public enum PropertyKey {
        index,
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.index, "index");
        return propertys;
    }
    /**将属性注册到Bean中。*/
    public void endElement(StackDecorator context, String xpath, EndElementEvent event) {
        ConstructorDefine property = (ConstructorDefine) this.getDefine(context);
        TemplateBeanDefine define = (TemplateBeanDefine) context.getAttribute(TagBeans_TemplateBean.BeanDefine);
        if (property.getIndex() == -1)
            property.setIndex(define.getInitParams().length + 1);
        define.addInitParam(property);
        super.endElement(context, xpath, event);
    }
}