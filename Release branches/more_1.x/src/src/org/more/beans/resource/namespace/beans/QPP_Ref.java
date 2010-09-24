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
package org.more.beans.resource.namespace.beans;
import org.more.beans.ValueMetaData;
import org.more.beans.define.QuickProperty_ValueMetaData;
import org.more.beans.define.Relation_ValueMetaData;
import org.more.beans.resource.QuickParserEvent;
import org.more.beans.resource.QuickPropertyParser;
/**
 * 引用类型Bean属性值解析器。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Ref implements QuickPropertyParser {
    /**试图解析成为{@link Relation_ValueMetaData}如果解析失败返回null。*/
    public ValueMetaData parser(QuickParserEvent event) {
        QuickProperty_ValueMetaData meta = event.getOldMetaData();
        if (meta.getRefBean() == null)
            return null;
        Relation_ValueMetaData newMETA = new Relation_ValueMetaData();
        newMETA.setRefBean(meta.getRefBean());
        newMETA.setRefBean(meta.getRefScope());
        return newMETA;
    }
}