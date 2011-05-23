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
import java.util.HashSet;
import java.util.Set;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.Set_ValueMetaData;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 解析{@link Set}类型数据
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class SetCollection_MetaData_Parser extends AbstractBase_Parser implements ValueMetaDataParser<Set_ValueMetaData> {
    private static ILog           log            = LogFactory.getLog(SetCollection_MetaData_Parser.class);
    private static final Class<?> DefaultSetType = HashSet.class;
    /*------------------------------------------------------------------------------*/
    public Set<Object> parser(Set_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<Set> setType = this.parserType(data, rootParser, context);
        Set<Object> setObject = setType.newInstance();
        log.debug("create Set value = {%0}, type = {%1}.", setObject, setType);
        //
        Set<AbstractValueMetaData> mData = data.getCollectionValue();
        int count = mData.size();
        int index = 0;
        if (mData != null)
            for (AbstractValueMetaData avmd : mData) {
                Object obj = rootParser.parser(avmd, rootParser, context);
                log.debug("set parser item {%0} of {%1} , value = {%2}", index, count, obj);
                setObject.add(obj);
                index++;
            }
        log.debug("finish parser Set value = {%0}.", setObject);
        return setObject;
    }
    public Class<Set> parserType(Set_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        Class<?> eType = super.getTypeForCache(data);
        if (eType == null) {
            String setTypeString = data.getCollectionType();
            if (eType != null)
                eType = context.getBeanClassLoader().loadClass(setTypeString);
            if (eType == null)
                eType = DefaultSetType;
            super.putTypeToCache(data, eType);
        }
        log.debug("parser Type = {%0}.", eType);
        return (Class<Set>) eType;
    }
};