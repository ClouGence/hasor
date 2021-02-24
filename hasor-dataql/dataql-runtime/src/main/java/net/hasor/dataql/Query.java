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
import net.hasor.dataql.runtime.QueryRuntimeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 查询
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface Query extends Hints, Cloneable {
    /** 添加全局变量 */
    public void addShareVar(String key, Object value);

    /** 添加全局变量 */
    public default void putShareVar(Map<String, Supplier<?>> shareVarMap) {
        if (shareVarMap == null) {
            return;
        }
        shareVarMap.forEach((key, valueSupplier) -> {
            addShareVar(key, valueSupplier.get());
        });
    }

    /** 执行查询 */
    public default QueryResult execute() throws QueryRuntimeException {
        return this.execute(symbol -> Collections.emptyMap());
    }

    /** 执行查询 */
    public default QueryResult execute(Map<String, ?> envData) throws QueryRuntimeException {
        return this.execute(symbol -> envData);
    }

    /** 执行查询 */
    public default QueryResult execute(Object[] envData) throws QueryRuntimeException {
        if (envData == null) {
            return this.execute(Collections.emptyMap());
        }
        Map<String, Object> objectMap = new HashMap<>();
        for (int i = 0; i < envData.length; i++) {
            objectMap.put("_" + i, envData[i]);
        }
        return this.execute(objectMap);
    }

    /** 执行查询 */
    public QueryResult execute(CustomizeScope customizeScope) throws QueryRuntimeException;

    /** 复制一个Query */
    public Query clone();
}
