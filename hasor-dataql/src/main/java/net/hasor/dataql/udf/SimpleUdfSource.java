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
package net.hasor.dataql.udf;
import net.hasor.dataql.QueryEngine;
import net.hasor.dataql.UDF;
import net.hasor.dataql.UdfSource;
import net.hasor.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 用于管理 UDF。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SimpleUdfSource extends ConcurrentHashMap<String, UDF> implements UdfSource {
    private String name = null;
    public SimpleUdfSource() {
        this(DefaultSource);
    }
    public SimpleUdfSource(Map<String, UDF> udfMap) {
        this(DefaultSource, udfMap);
    }
    public SimpleUdfSource(String name) {
        this(name, null);
    }
    public SimpleUdfSource(String name, Map<String, UDF> udfMap) {
        if (StringUtils.isBlank(name)) {
            name = DefaultSource;
        }
        this.name = name;
        if (udfMap != null) {
            super.putAll(udfMap);
        }
    }
    //
    @Override
    public String getName() {
        return this.name;
    }
    //
    @Override
    public UDF findUdf(String udfName, QueryEngine sourceEngine) throws Throwable {
        return super.get(udfName);
    }
    public void addUdf(String udfName, UDF udf) {
        super.put(udfName, udf);
    }
}