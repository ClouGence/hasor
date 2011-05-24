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
import java.util.Date;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.Date_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
import org.more.util.StringConvert;
/**
 * 解析文本时间日期类型。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Date_MetaData_Parser implements ValueMetaDataParser<Date_ValueMetaData> {
    private static ILog log = LogFactory.getLog(Date_MetaData_Parser.class);
    /*------------------------------------------------------------------------------*/
    public Date parser(Object targetObject, Date_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        String dateString = data.getDateString();
        String formatString = data.getFormatString();
        Date date = null;
        if (formatString != null)
            date = StringConvert.parseDate(dateString, formatString);
        else
            date = StringConvert.parseDate(dateString);
        log.debug("parser Date dateString = {%0} ,formater = {%1}.", dateString, formatString);
        return date;
    }
};