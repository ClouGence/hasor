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
import java.io.File;
import java.util.Map;
import org.more.beans.define.File_ValueMetaData;
import org.more.beans.resource.AbstractXmlConfiguration;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
/**
 * 用于解析directory标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Directory extends TagBeans_AbstractValueMetaDataDefine<File_ValueMetaData> {
    /**创建{@link TagBeans_Directory}对象*/
    public TagBeans_Directory(AbstractXmlConfiguration configuration) {
        super(configuration);
    }
    /**创建{@link File_ValueMetaData}对象。*/
    protected File_ValueMetaData createDefine() {
        File_ValueMetaData metaData = new File_ValueMetaData();
        metaData.setDir(true);
        return metaData;
    }
    /**定义模板属性。*/
    public enum PropertyKey {
        path
    }
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        return null;
    }
    /**解析属性*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        String path = event.getAttributeValue("path");
        if (path == null)
            return;
        File_ValueMetaData metaData = this.getDefine(context);
        metaData.setFileObject(new File(path));
    }
}