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
import java.lang.reflect.Array;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.assembler.MetaDataUtil;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.Array_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 解析数组对象。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayCollection_MetaData_Parser implements ValueMetaDataParser<Array_ValueMetaData> {
    private static ILog           log               = LogFactory.getLog(ArrayCollection_MetaData_Parser.class);
    private static final Class<?> DefaultCollection = Object.class;
    /*------------------------------------------------------------------------------*/
    public Object parser(Object targetObject, Array_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        //1.创建类型对象。
        Class<?> arrayItemType = MetaDataUtil.pass(data, context, log, DefaultCollection);
        int initSize = data.getInitSize();
        Object array = Array.newInstance(arrayItemType, initSize);
        log.debug("create Array type = {%0}, length = {%1}.", arrayItemType, initSize);
        //2.添加集合元素。
        AbstractValueMetaData[] mData = data.getCollectionValue();
        int count = mData.length;
        if (mData != null)
            for (int i = 0; i < count; i++) {
                AbstractValueMetaData avmd = mData[i];
                Object obj = rootParser.parser(targetObject, avmd, rootParser, context);
                log.debug("set parser item {%0} of {%1} , value = {%2}", i, count, obj);
                Array.set(array, i, obj);
            }
        log.debug("finish parser Array...");
        return array;
    }
};