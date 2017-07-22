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
import net.hasor.dataql.UDF;

import java.util.HashMap;
import java.util.Map;
/**
 * 用于管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class UdfManager {
    private final Map<String, UDF> udfMap = new HashMap<String, UDF>();
    //
    //
    /** 添加 UDF */
    public void addUDF(String udfName, UDF udf) {
        if (this.udfMap.containsKey(udfName)) {
            throw new IllegalStateException("udf name ‘" + udfName + "’ already exist.");
        }
        this.udfMap.put(udfName, udf);
    }
    public UDF findUDF(String udfName) {
        if (this.udfMap.containsKey(udfName)) {
            return this.udfMap.get(udfName);
        }
        return null;
    }
}