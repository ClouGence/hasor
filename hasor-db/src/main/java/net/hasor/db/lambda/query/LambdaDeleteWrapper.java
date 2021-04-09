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
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.DeleteExecute;
import net.hasor.db.lambda.LambdaOperations.LambdaDelete;
import net.hasor.db.lambda.segment.MergeSqlSegment;
import net.hasor.db.lambda.segment.Segment;
import net.hasor.db.metadata.TableDef;

import java.sql.SQLException;

import static net.hasor.db.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda delete 能力。是 LambdaDelete 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaDeleteWrapper<T> extends AbstractQueryCompare<T, LambdaDelete<T>> implements LambdaDelete<T> {
    private boolean allowEmptyWhere = false;

    public LambdaDeleteWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
    }

    @Override
    protected boolean supportPage() {
        return false;// delete is disable Page;
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
    public DeleteExecute<T> allowEmptyWhere() {
        this.allowEmptyWhere = true;
        return this;
    }

    @Override
    public BoundSql getOriginalBoundSql() {
        // must be clean , The rebuildSQL will reinitialize.
        this.queryParam.clear();
        //
        String sqlQuery = rebuildSql();
        Object[] args = this.queryParam.toArray().clone();
        return new BoundSql.BoundSqlObj(sqlQuery, args);
    }

    private String rebuildSql() {
        MergeSqlSegment sqlSegment = new MergeSqlSegment();
        sqlSegment.addSegment(DELETE);
        sqlSegment.addSegment(FROM);
        sqlSegment.addSegment(buildTabName(this.dialect()));
        if (!this.queryTemplate.isEmpty()) {
            sqlSegment.addSegment(WHERE);
            sqlSegment.addSegment(this.queryTemplate.sub(1));
        } else if (!this.allowEmptyWhere) {
            throw new UnsupportedOperationException("The dangerous DELETE operation, You must call `allowEmptyWhere()` to enable DELETE ALL.");
        }
        return sqlSegment.getSqlSegment();
    }

    private Segment buildTabName(SqlDialect dialect) {
        TableDef tableDef = super.getRowMapper().getTableInfo();
        if (tableDef == null) {
            throw new IllegalArgumentException("tableDef not found.");
        }
        return () -> dialect.tableName(isQualifier(), tableDef);
    }

    @Override
    public int doDelete() throws SQLException {
        BoundSql boundSql = getBoundSql();
        String sqlString = boundSql.getSqlString();
        return this.getJdbcTemplate().executeUpdate(sqlString, boundSql.getArgs());
    }
}
