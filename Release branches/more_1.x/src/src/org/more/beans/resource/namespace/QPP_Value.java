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
import org.more.beans.ValueMetaData;
import org.more.beans.define.Simple_ValueMetaData;
import org.more.beans.define.QuickProperty_ValueMetaData;
import org.more.beans.resource.QuickParserEvent;
import org.more.beans.resource.QuickPropertyParser;
import org.more.util.StringConvert;
/**
 * 默认属性值解析器，默认属性类型是String
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Value implements QuickPropertyParser {
    public ValueMetaData parser(QuickParserEvent event) {
        QuickProperty_ValueMetaData meta = event.getOldMetaData();
        if (meta.getValue() == null)
            return null;
        Class<?> propType = event.getProperty().getClassType();
        if (propType == null)
            //当检测到value有值但是又没有定义type时候值类型采用的默认数据类型。
            propType = Simple_ValueMetaData.DefaultValueType;
        Object value = StringConvert.changeType(meta.getValue(), propType);
        Simple_ValueMetaData newMEDATA = new Simple_ValueMetaData();
        newMEDATA.setValue(value);
        newMEDATA.setValueMetaType(Simple_ValueMetaData.getPropertyType(propType));
        return newMEDATA;
    }
}