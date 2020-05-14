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
package net.hasor.dataql.fx.db.dialect;
import net.hasor.dataql.fx.db.FxQuery;
import net.hasor.utils.ExceptionUtils;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.Map;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-13
 */
public class SqlServer2012Dialect extends AbstractDialect {
    protected Select parseSelect(String buildSqlString, FxQuery fxSql) {
        Select select = fxSql.attach(Select.class);
        if (select == null) {
            try {
                select = (Select) CCJSqlParserUtil.parse(buildSqlString);
                fxSql.attach(Select.class, select);
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        return select;
    }

    @Override
    public BoundSql getCountSql(FxQuery fxSql, Map<String, Object> paramMap) {
        String buildSqlString = fxSql.buildQueryString(paramMap);
        List<Object> paramArrays = fxSql.buildParameterSource(paramMap);
        //
        // .含有 order by 去掉它
        if (buildSqlString.toLowerCase().contains("order by")) {
            Select selectStatement = parseSelect(buildSqlString, fxSql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            if (plainSelect.getOrderByElements() != null) {
                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                plainSelect.setOrderByElements(null);
                buildSqlString = selectStatement.toString();
                plainSelect.setOrderByElements(orderByElements);
            }
        }
        // .拼 count 语句
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) FROM (");
        sqlBuilder.append(buildSqlString);
        sqlBuilder.append(") as TEMP_T");
        return new BoundSql(sqlBuilder.toString(), paramArrays.toArray());
    }

    @Override
    public BoundSql getPageSql(FxQuery fxSql, Map<String, Object> paramMap, int start, int limit) {
        String buildSqlString = fxSql.buildQueryString(paramMap);
        List<Object> paramArrays = fxSql.buildParameterSource(paramMap);
        //
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(buildSqlString);
        //
        if (buildSqlString.toLowerCase().contains("order by")) {
            Select selectStatement = parseSelect(buildSqlString, fxSql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            if (plainSelect.getOrderByElements() == null) {
                sqlBuilder.append(" ORDER BY CURRENT_TIMESTAMP");
            }
        } else {
            sqlBuilder.append(" ORDER BY CURRENT_TIMESTAMP");
        }
        //
        sqlBuilder.append(" offset ? rows fetch next ? rows only ");
        paramArrays.add(start);
        paramArrays.add(limit);
        //
        buildSqlString = sqlBuilder.toString();
        return new BoundSql(buildSqlString, paramArrays.toArray());
    }
}