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
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.define.Simple_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class SimpleType_MetaData_Parser implements ValueMetaDataParser<Simple_ValueMetaData> {
    public Object parser(Simple_ValueMetaData data, ValueMetaDataParser<Simple_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
    public Class<?> parserType(Simple_ValueMetaData data, ValueMetaDataParser<Simple_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        return Simple_ValueMetaData.getPropertyType(data.getValueMetaType());
    }
};