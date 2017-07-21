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
package net.hasor.dataql.runtime;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.compiler.QueryType;

import java.util.HashMap;
import java.util.Map;
/**
 * DataQL 运行时。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryRuntime extends ClassLoader implements Option {
    private final Option option = new OptionSet();
    private final Map<String, UDF> udfMap;
    //
    public QueryRuntime() {
        this.udfMap = new HashMap<String, UDF>();
    }
    //
    /** 添加 UDF */
    public void addShareUDF(String udfName, UDF udf) {
        if (this.udfMap.containsKey(udfName)) {
            throw new IllegalStateException("udf name ‘" + udfName + "’ already exist.");
        }
        this.udfMap.put(udfName, udf);
    }
    UDF findUDF(String udfName) {
        return this.udfMap.get(udfName);
    }
    //
    /** 根据编译出来的 QueryType，创建对应的执行引擎。 */
    public QueryEngine createEngine(QueryType queryType) {
        return new QueryEngine(this, queryType);
    }
    //
    //
    public Class<?> loadType(String type) throws ClassNotFoundException {
        return super.loadClass(type);
    }
    @Override
    public String[] getOptionNames() {
        return this.option.getOptionNames();
    }
    @Override
    public Object getOption(String optionKey) {
        return this.option.getOption(optionKey);
    }
    @Override
    public void removeOption(String optionKey) {
        this.option.removeOption(optionKey);
    }
    @Override
    public void setOption(String optionKey, String value) {
        this.option.setOption(optionKey, value);
    }
    @Override
    public void setOption(String optionKey, Number value) {
        this.option.setOption(optionKey, value);
    }
    @Override
    public void setOption(String optionKey, boolean value) {
        this.option.setOption(optionKey, value);
    }
}