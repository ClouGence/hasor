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
package net.hasor.dataql.fx.db.likemybatis;
import net.hasor.db.dal.dynamic.segment.DefaultSqlSegment;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.rule.ParameterSqlBuildRule.SqlArg;

import java.util.List;
import java.util.Map;

/**
 * @author jmxd
 * @version : 2020-05-18
 */
class MybatisSqlSegmentQuery extends DefaultSqlSegment {
    private final SqlNode sqlNode;

    public MybatisSqlSegmentQuery(SqlNode sqlNode) {
        this.sqlNode = sqlNode;
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) {
        String queryString = buildQueryString(builderContext.getContext());
        List<Object> args = buildParameterSource(builderContext.getContext());
        querySqlBuilder.appendSql(queryString, args.stream().map(SqlArg::new).toArray(SqlArg[]::new));
    }

    private String buildQueryString(Object context) {
        if (context instanceof Map) {
            return sqlNode.getSql((Map<String, Object>) context);
        } else {
            throw new IllegalArgumentException("context must be instance of Map");
        }
    }

    private List<Object> buildParameterSource(Object context) {
        return this.sqlNode.getParameters();
    }
}
