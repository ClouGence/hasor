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
package net.hasor.dataql;
import java.util.Map;
/**
 * UDF数据源
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface UdfSource extends Map<String, UDF> {
    /**默认数据源*/
    public static final String DefaultSource = "default";

    /**数据源名称*/
    public String getName();

    /**获取UDF，不同于 get 方法。使用 find 可以在不加入到 Map 的情况下动态的查找 udf。*/
    public UDF findUdf(String udfName, QueryEngine sourceEngine) throws Throwable;

    /**添加 UDF 到 Map*/
    public void addUdf(String udfName, UDF udf);
}