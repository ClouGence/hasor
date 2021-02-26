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
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * lambda SQL 执行
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InsertExecute<T> {
    /** 参考的样本对象 */
    public Class<T> exampleType();

    /** 插入一条记录。 */
    public long insert(T entity) throws SQLException;

    /** 批量插入记录。 */
    public default long batchInsert(T... entity) throws SQLException {
        return batchInsert(Arrays.asList(entity));
    }

    /** 批量插入记录。 */
    public long batchInsert(List<T> entity) throws SQLException;

    /** insert form select */
    public <V> long insertFromQuery(LambdaQuery<V> lambdaQuery) throws SQLException;

    /** build insert form select sql*/
    public <V> BoundSql buildInsertFromQuery(LambdaQuery<V> lambdaQuery) throws SQLException;
}
