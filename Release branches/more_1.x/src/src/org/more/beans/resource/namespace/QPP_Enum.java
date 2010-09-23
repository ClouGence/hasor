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
import org.more.beans.define.Enum_ValueMetaData;
import org.more.beans.define.QuickProperty_ValueMetaData;
import org.more.beans.resource.QuickParserEvent;
import org.more.beans.resource.QuickPropertyParser;
/**
 * 枚举属性值解析器，默认值是null。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Enum implements QuickPropertyParser {
    public ValueMetaData parser(QuickParserEvent event) {
        QuickProperty_ValueMetaData meta = event.getOldMetaData();
        if (meta.getEnumeration() == null)
            return null;
        Class<?> propType = event.getProperty().getClassType();
        if (propType == null || Enum.class.isAssignableFrom(propType) == false)
            propType = null;
        Enum_ValueMetaData newMEDATA = new Enum_ValueMetaData();
        newMEDATA.setEnumValue(meta.getEnumeration());
        newMEDATA.setEnumType(propType);
        return newMEDATA;
    }
}