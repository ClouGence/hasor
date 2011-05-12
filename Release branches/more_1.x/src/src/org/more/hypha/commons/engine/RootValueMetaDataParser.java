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
package org.more.hypha.commons.engine;
import java.util.HashMap;
import java.util.Map;
import org.more.DoesSupportException;
import org.more.RepeateException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
/**
 * 属性元信息解析器的根，{@link ValueMetaDataParser}解析器入口。
 * @version 2011-1-21
 * @author 赵永春 (zyc@byshell.org)
 */
abstract class RootValueMetaDataParser implements ValueMetaDataParser<ValueMetaData> {
    private Map<String, ValueMetaDataParser<ValueMetaData>> metaDataParserMap = new HashMap<String, ValueMetaDataParser<ValueMetaData>>();
    //----------------------------------------------------------------------------------------------------------
    /**第二个参数无效，因为{@link RootValueMetaDataParser}就是根。*/
    public Object parser(ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser/*该参数无效*/, ApplicationContext context) throws Throwable {
        String metaDataType = data.getMetaDataType();
        if (this.metaDataParserMap.containsKey(metaDataType) == false)
            throw new DoesSupportException("不支持的ValueMetaData数据描述：" + metaDataType);
        return this.metaDataParserMap.get(metaDataType).parser(data, this, context);
    };
    public Class<?> parserType(ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        String metaDataType = data.getMetaDataType();
        if (this.metaDataParserMap.containsKey(metaDataType) == false)
            throw new DoesSupportException("不支持的ValueMetaData数据描述：" + metaDataType);
        return this.metaDataParserMap.get(metaDataType).parserType(data, this, context);
    };
    /**注册{@link ValueMetaDataParser}，如果注册的解析器出现重复则会引发{@link RepeateException}异常。*/
    public void addParser(String metaDataType, ValueMetaDataParser<ValueMetaData> parser) throws RepeateException {
        if (this.metaDataParserMap.containsKey(metaDataType) == false)
            this.metaDataParserMap.put(metaDataType, parser);
    };
    /**解除注册{@link ValueMetaDataParser}，如果要移除的解析器如果不存在也不会抛出异常。*/
    public void removeParser(String metaDataType) {
        if (this.metaDataParserMap.containsKey(metaDataType) == true)
            this.metaDataParserMap.remove(metaDataType);
    };
};