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
package net.hasor.db.lambda;
import net.hasor.db.lambda.LambdaOperations.LambdaQuery;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * lambda Insert 执行器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InsertExecute<T> extends BoundSqlBuilder {
    /** 执行插入，并且将返回的int结果相加。*/
    public default int executeSumResult() throws SQLException {
        int[] results = this.executeGetResult();
        int sumValue = 0;
        for (int result : results) {
            sumValue = sumValue + result;
        }
        return sumValue;
    }

    /** 执行插入，并返回所有结果*/
    public int[] executeGetResult() throws SQLException;

    /** 当插入遇到重复键，使用新值替代旧值（当 SQL 方言不支持时，会退化为 onDuplicateKeyBlock）*/
    public InsertExecute<T> onDuplicateKeyUpdate();

    /** 当插入遇到重复键时，忽略新值插入（当 SQL 方言不支持时，会退化为 onDuplicateKeyBlock）*/
    public InsertExecute<T> onDuplicateKeyIgnore();

    /** 当插入遇到重复键时，报错（默认）*/
    public InsertExecute<T> onDuplicateKeyBlock();

    /** 批量插入记录。 */
    public default InsertExecute<T> applyEntity(T entity) {
        return applyEntity(Collections.singletonList(entity));
    }

    /** 批量插入记录。 */
    public InsertExecute<T> applyEntity(List<T> entity);

    /** insert form select */
    public <V> InsertExecute<T> applyQueryAsInsert(LambdaQuery<V> lambdaQuery);

    /** insert form select */
    public <V> InsertExecute<T> applyQueryAsInsert(Class<V> exampleType, Consumer<LambdaQuery<V>> queryBuilderConsumer);

    /** 批量插入记录。 */
    public default InsertExecute<T> applyMap(Map<String, Object> dataMap) {
        return applyMap(Collections.singletonList(dataMap));
    }

    /** 批量插入记录。 */
    public InsertExecute<T> applyMap(List<Map<String, Object>> dataMapList);
}
