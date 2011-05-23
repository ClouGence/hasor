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
import java.util.Map;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.Map_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * Ω‚ŒˆMap
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class MapCollection_MetaData_Parser extends AbstractBase_Parser implements ValueMetaDataParser<Map_ValueMetaData> {
    private static ILog           log             = LogFactory.getLog(MapCollection_MetaData_Parser.class);
    private static final Class<?> DefaultListType = ArrayList.class;
    /*------------------------------------------------------------------------------*/
    public Map<?, ?> parser(Map_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<Map> mapType = this.parserType(data, rootParser, context);
        Map<Object, Object> mapObject = mapType.newInstance();
        log.debug("create Map value = {%0}, type = {%1}.", mapObject, mapType);
        //
        Map<AbstractValueMetaData, AbstractValueMetaData> mData = data.getCollectionValue();
        int count = mData.size();
        int index = 0;
        if (mData != null)
            for (AbstractValueMetaData avmdK : mData.keySet()) {
                AbstractValueMetaData avmdV = mData.get(avmdK);
                //
                Object k = rootParser.parser(avmdK, rootParser, context);
                Object v = rootParser.parser(avmdV, rootParser, context);
                log.debug("list parser item {%0} of {%1} , k = {%2}, v = {%3}", index, count, k, v);
                mapObject.put(k, v);
                index++;
            }
        log.debug("finish parser List value = {%0}.", mapObject);
        return mapObject;
    }
    public Class<Map> parserType(Map_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
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
        return (Class<Map>) eType;
    }
};