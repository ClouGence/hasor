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
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.TypeParser;
import org.more.hypha.beans.define.PropertyType;
import org.more.hypha.beans.define.Simple_ValueMetaData;
import org.more.util.StringConvert;
import org.more.util.attribute.IAttribute;
/**
 * {@link QPP_Value}不处理boolean,byte,short,int,long,float,double,char,string
 * 之外的任何类型，但其包装类型属于处理范畴。如果取不到value属性则也不会处理。
 * @version 2010-11-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Value implements TypeParser {
    public ValueMetaData parser(IAttribute attribute, AbstractPropertyDefine property) {
        String value = (String) attribute.getAttribute("value");
        if (value == null)
            return null;
        //2.不处理boolean,byte,short,int,long,float,double,char,string之外的任何类型。
        PropertyType propType = Simple_ValueMetaData.getPropertyType(property.getClassType());
        if (propType == null)
            propType = Simple_ValueMetaData.DefaultValueType;
        //2.解析
        Class<?> propClass = Simple_ValueMetaData.getPropertyType(propType);
        Object var = StringConvert.changeType(value, propClass);
        Simple_ValueMetaData newMEDATA = new Simple_ValueMetaData();
        newMEDATA.setValue(var);
        newMEDATA.setValueMetaType(propType);
        return newMEDATA;
    }
}