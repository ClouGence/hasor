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
package net.hasor.db.lambda.query;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.mapping.FieldInfo;
import net.hasor.db.lambda.LambdaOperations.LambdaUpdate;
import net.hasor.db.lambda.UpdateExecute;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

/**
 * 提供 lambda update 能力。是 LambdaUpdate 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaUpdateWrapper<T> extends AbstractQueryCompare<T, LambdaUpdate<T>> implements LambdaUpdate<T> {
    public LambdaUpdateWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        throw new UnsupportedOperationException();
    }

    @Override
    protected LambdaUpdate<T> getSelf() {
        return this;
    }

    @Override
    public LambdaUpdate<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    @Override
    public int doUpdate() throws SQLException {
        return 0;
    }

    @Override
    public UpdateExecute<T> applyUpdateTo(T newValue) throws SQLException {
        return null;
    }

    @Override
    public UpdateExecute<T> applyUpdateTo(T newValue, String... columns) throws SQLException {
        return null;
    }

    @Override
    public UpdateExecute<T> applyUpdateTo(T newValue, SFunction<T> property) throws SQLException {
        return null;
    }

    @Override
    public UpdateExecute<T> applyUpdateTo(T newValue, List<SFunction<T>> propertyList) throws SQLException {
        return null;
    }

    @Override
    public UpdateExecute<T> applyUpdateTo(T newValue, Predicate<FieldInfo> tester) throws SQLException {
        return null;
    }
}
