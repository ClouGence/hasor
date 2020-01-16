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
import net.hasor.dataql.runtime.InstructRuntimeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface Query extends Hints {
    /** 初始化编译变量 */
    public void setCompilerVar(String compilerVar, Object object);

    /** 执行查询 */
    public default QueryResult execute() throws InstructRuntimeException {
        return this.execute(symbol -> Collections.emptyMap());
    }

    /** 执行查询 */
    public default QueryResult execute(Map<String, ?> envData) throws InstructRuntimeException {
        return this.execute(symbol -> envData);
    }

    /** 执行查询 */
    public default QueryResult execute(Object[] envData) throws InstructRuntimeException {
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
    public QueryResult execute(CustomizeScope customizeScope) throws InstructRuntimeException;
}