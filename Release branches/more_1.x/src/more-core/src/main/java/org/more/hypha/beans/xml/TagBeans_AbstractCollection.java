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
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.define.Collection_ValueMetaData;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析集合类型标签的基类。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractCollection<T extends Collection_ValueMetaData<?>> extends TagBeans_AbstractValueMetaDataDefine<T> {
    /**创建{@link TagBeans_AbstractCollection}对象*/
    public TagBeans_AbstractCollection(XmlDefineResource configuration) {
        super(configuration);
    }
    /**定义模板属性。*/
    public enum PropertyKey {
        collectionType, initSize
    }
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        //propertys.put(PropertyKey.collectionType, "collectionType");
        propertys.put(PropertyKey.initSize, "initSize");
        return propertys;
    }
    /**当集合类型没有设置属性时可以通过该方法来获取默认类型。*/
    protected abstract Class<?> getDefaultCollectionType();
    /**开始执行标签*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        Collection_ValueMetaData<?> valueMetaData = this.getDefine(context);
        String arrayTypeString = event.getAttributeValue("collectionType");
        // 1.转换collectionType属性类型
        if (arrayTypeString == null)
            arrayTypeString = this.getDefaultCollectionType().getName();
        //2.设置值
        valueMetaData.setCollectionType(arrayTypeString);
    }
}