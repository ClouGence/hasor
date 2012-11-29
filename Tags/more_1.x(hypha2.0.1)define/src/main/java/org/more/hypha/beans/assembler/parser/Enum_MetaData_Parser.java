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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.hypha.define.Enum_ValueMetaData;
/**
 * 枚举类型解析。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Enum_MetaData_Parser implements ValueMetaDataParser<Enum_ValueMetaData> {
    private static Log log = LogFactory.getLog(Enum_MetaData_Parser.class);
    /*------------------------------------------------------------------------------*/
    public Enum<?> parser(Object targetObject, Enum_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Enum<?> eValue = data.getEnumType(context.getClassLoader());
        log.debug("parser Enum = {%0}.", eValue);
        return eValue;
    }
};