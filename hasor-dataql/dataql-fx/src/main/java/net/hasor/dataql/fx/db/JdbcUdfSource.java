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
package net.hasor.dataql.fx.db;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * Jdbc,相关的数据库函数库。函数库引入 <code>import 'net.hasor.dataql.fx.db.JdbcUdfSource' as jdbc;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
@Singleton
public class JdbcUdfSource implements UdfSourceAssembly {
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private JdbcTemplate jdbcTemplate;
    @Inject
    private SqlFragment  queryFragment;

    /** 提供一个 Udf Callback，用来为 DataQL 中的 lambda 查询提供事务能力 */
    public Object jdbcTran(final Hints hints, final Udf udf, final Propagation propagation) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, propagation);
    }

    /** 接受外部 SQL 字符串，并在事务模版中运行 */
    public Object execSQL(final Hints hints, String sqlString, final Propagation propagation) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return queryFragment.runFragment(hints, Collections.emptyMap(), sqlString);
        }, propagation);
    }
}