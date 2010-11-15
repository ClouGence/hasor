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
import org.more.FormatException;
import org.more.hypha.beans.AbstractPropertyDefine;
import org.more.hypha.beans.TypeParser;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.define.URI_ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
 * 连接属性值解析器，默认值是null。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_URILocation implements TypeParser {
    public ValueMetaData parser(IAttribute attribute, AbstractPropertyDefine property) {
        String value = (String) attribute.getAttribute("uriLocation");
        if (value == null)
            return null;
        //2.进行解析
        URI_ValueMetaData newMETA = new URI_ValueMetaData();
        try {
            newMETA.setUriObject(new URI(value));
            return newMETA;
        } catch (URISyntaxException e) {
            throw new FormatException("解析uri类型数据发生异常，错误的uri格式数据：[" + value + "]");
        }
    }
}