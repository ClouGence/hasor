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
import java.io.File;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.define.File_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class File_MetaData_Parser extends AbstractBase_Parser implements ValueMetaDataParser<File_ValueMetaData> {
    private static ILog log = LogFactory.getLog(File_MetaData_Parser.class);
    public File parser(File_ValueMetaData data, ValueMetaDataParser<File_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        // TODO Auto-generated method stub
        log.debug("parser File = {%0}.", eValue);
        // data.ge
        return null;
    }
    public Class<?> parserType(File_ValueMetaData data, ValueMetaDataParser<File_ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<?> eType = super.getCache(data);
        if (eType == null) {
            eType = File.class;
            super.putCache(data, eType);
        }
        return eType;
    };
};