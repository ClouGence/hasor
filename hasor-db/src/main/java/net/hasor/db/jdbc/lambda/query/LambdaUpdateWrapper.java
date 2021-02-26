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
import net.hasor.db.dal.orm.FieldInfo;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaUpdate;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaUpdateWrapper<T> extends AbstractCompareQuery<T, LambdaUpdate<T>> implements LambdaUpdate<T> {
    public LambdaUpdateWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete() throws SQLException {
        return 0;
    }

    @Override
    public int updateCount() throws SQLException {
        return 0;
    }

    @Override
    public long updateLargeCount() throws SQLException {
        return 0;
    }

    @Override
    public int updateTo(T newValue) throws SQLException {
        return 0;
    }

    @Override
    public int updateTo(T newValue, String... columns) {
        return 0;
    }

    @Override
    public int updateTo(T newValue, List<SFunction<T>> columns) {
        return 0;
    }

    @Override
    public int updateTo(T newValue, Predicate<FieldInfo> tester) {
        return 0;
    }

    @Override
    protected LambdaUpdate<T> getSelf() {
        return this;
    }
}
