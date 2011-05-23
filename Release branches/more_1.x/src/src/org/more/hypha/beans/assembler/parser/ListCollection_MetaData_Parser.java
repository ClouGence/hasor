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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.List_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 解析列表类型。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class ListCollection_MetaData_Parser extends AbstractBase_Parser implements ValueMetaDataParser<List_ValueMetaData> {
    private static ILog           log             = LogFactory.getLog(ListCollection_MetaData_Parser.class);
    private static final Class<?> DefaultListType = ArrayList.class;
    /*------------------------------------------------------------------------------*/
    public List<?> parser(List_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<List> listType = this.parserType(data, rootParser, context);
        List<Object> listObject = listType.newInstance();
        log.debug("create List value = {%0}, type = {%1}.", listObject, listType);
        //
        List<AbstractValueMetaData> mData = data.getCollectionValue();
        int count = mData.size();
        int index = 0;
        if (mData != null)
            for (AbstractValueMetaData avmd : mData) {
                Object obj = rootParser.parser(avmd, rootParser, context);
                log.debug("list parser item {%0} of {%1} , value = {%2}", index, count, obj);
                listObject.add(obj);
                index++;
            }
        log.debug("finish parser List value = {%0}.", listObject);
        return listObject;
    }
    public Class<List> parserType(List_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<?> eType = super.getTypeForCache(data);
        if (eType == null) {
            String setTypeString = data.getCollectionType();
            if (eType != null)
                eType = context.getBeanClassLoader().loadClass(setTypeString);
            if (eType == null)
                eType = DefaultListType;
            super.putTypeToCache(data, eType);
        }
        log.debug("parser Type = {%0}.", eType);
        return (Class<List>) eType;
    }
};