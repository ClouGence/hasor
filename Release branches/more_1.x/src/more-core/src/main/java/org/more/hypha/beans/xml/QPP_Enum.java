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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.define.Enum_ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
 * 枚举属性值解析器，默认值是null。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Enum implements QPP {
    private static Log log = LogFactory.getLog(QPP_Enum.class);
    /**试图解析成为{@link Enum_ValueMetaData}如果解析失败返回null。*/
    public ValueMetaData parser(IAttribute<String> attribute, AbstractPropertyDefine property) {
        //1.检查是否可以解析
        String value = attribute.getAttribute("enum");
        if (value == null)
            return null;
        //2.进行解析
        Enum_ValueMetaData newMEDATA = new Enum_ValueMetaData();
        newMEDATA.setEnumValue(value);
        newMEDATA.setEnumType(property.getClassType());
        log.debug("parser Enum type = {%0} value= {%1}.", property.getClassType(), value);
        return newMEDATA;
    }
}