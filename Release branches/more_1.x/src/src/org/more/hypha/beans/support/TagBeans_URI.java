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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.more.FormatException;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.define.URI_ValueMetaData;
import org.more.hypha.configuration.DefineResourceImpl;
/**
 * 用于解析uri标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_URI extends TagBeans_AbstractValueMetaDataDefine<URI_ValueMetaData> {
    /**创建{@link TagBeans_URI}对象*/
    public TagBeans_URI(DefineResourceImpl configuration) {
        super(configuration);
    }
    /**创建{@link URI_ValueMetaData}对象。*/
    protected URI_ValueMetaData createDefine() {
        return new URI_ValueMetaData();
    }
    /**定义模板属性。*/
    public enum PropertyKey {
        uriLocation
    }
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        return null;
    }
    /**解析属性*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        String uriLocation = event.getAttributeValue("uriLocation");
        if (uriLocation == null)
            return;
        URI_ValueMetaData metaData = this.getDefine(context);
        try {
            metaData.setUriObject(new URI(uriLocation));
        } catch (URISyntaxException e) {
            throw new FormatException("解析uri类型数据发生异常，错误的uri格式数据：[" + uriLocation + "]");
        }
    }
}