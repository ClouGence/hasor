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
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaDelete;

import java.sql.SQLException;

/**
 * 提供 lambda delete 能力。是 LambdaDelete 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaDeleteWrapper<T> extends AbstractQueryCompare<T, LambdaDelete<T>> implements LambdaDelete<T> {
    public LambdaDeleteWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        throw new UnsupportedOperationException();
    }

    @Override
    protected LambdaDelete<T> getSelf() {
        return this;
    }

    @Override
    public LambdaDelete<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    @Override
    public int doDelete() throws SQLException {
        return 0;
    }
}
