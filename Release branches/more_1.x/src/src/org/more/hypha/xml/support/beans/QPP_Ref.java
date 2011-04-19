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
package org.more.hypha.xml.support.beans;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.define.beans.Relation_ValueMetaData;
import org.more.hypha.xml.support.BeansTypeParser;
import org.more.util.attribute.IAttribute;
/**
 * 引用类型Bean属性值解析器。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Ref implements BeansTypeParser {
    public ValueMetaData parser(IAttribute attribute, AbstractPropertyDefine property) {
        //1.检查是否可以解析
        String value = (String) attribute.getAttribute("refBean");
        if (value == null)
            return null;
        //2.进行解析
        Relation_ValueMetaData newMETA = new Relation_ValueMetaData();
        newMETA.setRefBean((String) value);
        newMETA.setRefPackage((String) attribute.getAttribute("refPackage"));
        return newMETA;
    }
}