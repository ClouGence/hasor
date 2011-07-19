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
package org.more.hypha.beans.assembler.parser;
import org.more.core.log.ILog;
import org.more.core.log.LogFactory;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.Simple_ValueMetaData;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.IAttribute;
/**
 * 简单类型解析器，类型返回null表示可能其值就是null.
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class SimpleType_MetaData_Parser implements ValueMetaDataParser<Simple_ValueMetaData> {
    private static ILog         log            = LogFactory.getLog(SimpleType_MetaData_Parser.class);
    private static final String FungiCacheName = "$FungiCacheName_Value";
    /*------------------------------------------------------------------------------*/
    public Object parser(Object targetObject, Simple_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        IAttribute fungiAtt = data.getFungi();
        Object value = fungiAtt.getAttribute(FungiCacheName);
        if (value == null) {
            Class<?> sType = Simple_ValueMetaData.getPropertyType(data.getValueMetaType());
            value = StringConvertUtil.changeType(data.getValue(), sType);
            fungiAtt.setAttribute(FungiCacheName, value);
        }
        log.debug("parser SimpleType value = {%0}.", value);
        return value;
    }
};