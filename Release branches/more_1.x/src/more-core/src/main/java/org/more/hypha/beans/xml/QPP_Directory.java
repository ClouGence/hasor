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
import org.more.hypha.define.File_ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
 * 目录类型属性值解析器。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_Directory implements QPP {
    private static Log log = LogFactory.getLog(QPP_Directory.class);
    public ValueMetaData parser(IAttribute<String> attribute, AbstractPropertyDefine property) {
        String value = attribute.getAttribute("directory");
        if (value == null)
            return null;
        //2.进行解析
        File_ValueMetaData newMETA = new File_ValueMetaData();
        newMETA.setFileObject(value);
        newMETA.setDir(true);
        log.debug("parser Directory path = {%0}.", value);
        return newMETA;
    }
}