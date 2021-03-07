/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * lambda Insert 执行器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InsertExecute<T> extends BoundSqlBuilder {
    /** 执行插入。*/
    public int doInsert() throws SQLException;

    /** 使用多 values 方式生成 SQL */
    public InsertExecute<T> useMultipleValues();

    /** 批量插入记录。 */
    public default InsertExecute<T> applyEntity(T entity) {
        return applyEntity(Collections.singletonList(entity));
    }

    /** 批量插入记录。 */
    public InsertExecute<T> applyEntity(List<T> entity);

    /** insert form select */
    public <V> InsertExecute<T> applyQueryAsInsert(LambdaQuery<V> lambdaQuery);

    /** 批量插入记录。 */
    public default InsertExecute<T> applyMap(Map<String, Object> dataMap) {
        return applyMap(Collections.singletonList(dataMap));
    }

    /** 批量插入记录。 */
    public InsertExecute<T> applyMap(List<Map<String, Object>> dataMapList);
}
