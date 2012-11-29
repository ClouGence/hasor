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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.assembler.MetaDataUtil;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.hypha.define.ValueMetaData;
import org.more.hypha.define.List_ValueMetaData;
/**
 * 解析列表类型。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ListCollection_MetaData_Parser implements ValueMetaDataParser<List_ValueMetaData> {
    private static Log            log               = LogFactory.getLog(ListCollection_MetaData_Parser.class);
    private static final Class<?> DefaultCollection = ArrayList.class;
    /*------------------------------------------------------------------------------*/
    @SuppressWarnings("unchecked")
    public List<?> parser(Object targetObject, List_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        //1.创建类型对象。
        Class<?> listType = MetaDataUtil.pass(data, context, log, DefaultCollection);
        List<Object> listObject = (List<Object>) listType.newInstance();
        log.debug("create List value = {%0}, type = {%1}.", listObject, listType);
        //2.添加集合元素。
        List<ValueMetaData> mData = data.getCollectionValue();
        int count = mData.size();
        int index = 0;
        if (mData != null)
            for (ValueMetaData avmd : mData) {
                Object obj = rootParser.parser(targetObject, avmd, rootParser, context);
                log.debug("list parser item {%0} of {%1} , value = {%2}", index, count, obj);
                listObject.add(obj);
                index++;
            }
        log.debug("finish parser List value = {%0}.", listObject);
        return listObject;
    }
};