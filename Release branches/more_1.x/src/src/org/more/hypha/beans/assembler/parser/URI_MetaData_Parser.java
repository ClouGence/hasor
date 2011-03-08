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
import java.net.URI;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.ValueMetaDataParser;
import org.more.hypha.beans.define.URI_ValueMetaData;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class URI_MetaData_Parser implements ValueMetaDataParser<URI_ValueMetaData> {
    public URI parser(URI_ValueMetaData data, ValueMetaDataParser<URI_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
    public Class<?> parserType(URI_ValueMetaData data, ValueMetaDataParser<URI_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        return URI.class;
    }
};