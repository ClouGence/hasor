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
import net.hasor.core.Settings;
import net.hasor.dataql.Hints;
import net.hasor.dataql.fx.db.dialect.MySqlDialect;
import net.hasor.dataql.fx.db.dialect.SqlPageDialect;
import net.hasor.dataql.fx.db.dialect.SqlPageDialect.BoundSql;
import net.hasor.dataql.fx.db.parser.FxSql;
import net.hasor.db.jdbc.ConnectionCallback;

import java.sql.SQLException;
import java.util.Map;

/**
 * 扩展了 SqlFragment 支持了 分页能力。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class SqlPageFragment extends SqlFragment {
    protected Object doFragment(final FxSql fxSql, final SqlMode sqlMode, final Hints hint, final Map<String, Object> paramMap) throws Throwable {
        if (SqlMode.Query != sqlMode) {
            return super.doFragment(fxSql, sqlMode, hint, paramMap);
        }
        //
        Settings settings = this.appContext.getEnvironment().getSettings();
        String sqlDialect = settings.getString("hasor.dataql-fx.sqlPageDialectSet.sqlDialect", "");
        SqlPageDialect pageDialect = new MySqlDialect();
        //
        return new SqlPageObject(new SqlPageQuery() {
            @Override
            public BoundSql getCountBoundSql() {
                return pageDialect.getCountSql(fxSql, paramMap);
            }

            @Override
            public BoundSql getPageBoundSql(int start, int limit) {
                if (limit < 0) {
                    String sqlString = fxSql.buildSqlString(paramMap);
                    Object[] paramArrays = fxSql.buildParameterSource(paramMap).toArray();
                    return new BoundSql(sqlString, paramArrays);
                }
                return pageDialect.getPageSql(fxSql, paramMap, start, limit);
            }

            @Override
            public <T> T doQuery(ConnectionCallback<T> connectionCallback) throws SQLException {
                return jdbcTemplate.execute(connectionCallback);
            }
        });
    }
}