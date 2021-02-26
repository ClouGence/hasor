/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.lambda.query;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaInsert;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaInsertWrapper<T> extends AbstractCompareQuery<T, LambdaInsert<T>> implements LambdaInsert<T> {
    public LambdaInsertWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        throw new UnsupportedOperationException();
    }

    @Override
    public long insert(T entity) throws SQLException {
        return 0;
    }

    @Override
    public long batchInsert(List<T> entity) throws SQLException {
        return 0;
    }

    @Override
    public <V> long insertFromQuery(LambdaQuery<V> lambdaQuery) throws SQLException {
        return 0;
    }

    @Override
    public <V> BoundSql buildInsertFromQuery(LambdaQuery<V> lambdaQuery) throws SQLException {
        return null;
    }

    @Override
    protected LambdaInsert<T> getSelf() {
        return null;
    }
}
