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
import java.util.HashMap;
import java.util.Map;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.assembler.MetaDataUtil;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.Map_ValueMetaData;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 解析Map
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class MapCollection_MetaData_Parser implements ValueMetaDataParser<Map_ValueMetaData> {
    private static ILog           log               = LogFactory.getLog(MapCollection_MetaData_Parser.class);
    private static final Class<?> DefaultCollection = HashMap.class;
    /*------------------------------------------------------------------------------*/
    @SuppressWarnings("unchecked")
    public Map<?, ?> parser(Object targetObject, Map_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        //1.创建类型对象。
        Class<?> mapType = MetaDataUtil.pass(data, context, log, DefaultCollection);
        Map<Object, Object> mapObject = (Map<Object, Object>) mapType.newInstance();
        log.debug("create Map value = {%0}, type = {%1}.", mapObject, mapType);
        //2.输出map值。
        Map<AbstractValueMetaData, AbstractValueMetaData> mData = data.getCollectionValue();
        int count = mData.size();
        int index = 0;
        if (mData != null)
            for (AbstractValueMetaData avmdK : mData.keySet()) {
                AbstractValueMetaData avmdV = mData.get(avmdK);
                //
                Object k = rootParser.parser(targetObject, avmdK, rootParser, context);
                Object v = rootParser.parser(targetObject, avmdV, rootParser, context);
                log.debug("list parser item {%0} of {%1} , k = {%2}, v = {%3}", index, count, k, v);
                mapObject.put(k, v);
                index++;
            }
        log.debug("finish parser List value = {%0}.", mapObject);
        return mapObject;
    }
};