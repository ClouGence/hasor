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
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.define.File_ValueMetaData;
/**
 * 用于解析directory标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Directory extends TagBeans_AbstractValueMetaDataDefine<File_ValueMetaData> {
    /**创建{@link TagBeans_Directory}对象*/
    public TagBeans_Directory(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建{@link File_ValueMetaData}对象。*/
    protected File_ValueMetaData createDefine(XmlStackDecorator<Object> context) {
        File_ValueMetaData metaData = new File_ValueMetaData();
        metaData.setDir(true);
        return metaData;
    }
    /**定义模板属性。*/
    public enum PropertyKey {
        fileObject
    }
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        propertys.put(PropertyKey.fileObject, "path");
        return propertys;
    }
}