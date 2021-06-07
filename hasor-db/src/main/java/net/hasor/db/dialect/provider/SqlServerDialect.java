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
package net.hasor.db.dialect.provider;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.utils.ExceptionUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.*;

/**
 * SqlServer 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlServerDialect extends AbstractDialect implements SqlDialect {
    private static final Map<String, Select> CACHE       = new WeakHashMap<>();
    private static final Object              LOCK_OBJECT = new Object();

    protected static Select parseSelect(String buildSqlString) {
        Select select = CACHE.get(buildSqlString);
        if (select != null) {
            return select;
        }
        synchronized (LOCK_OBJECT) {
            select = CACHE.get(buildSqlString);
            if (select != null) {
                return select;
            }
            try {
                select = (Select) CCJSqlParserUtil.parse(buildSqlString);
                CACHE.put(buildSqlString, select);
                return select;
            } catch (JSQLParserException e) {
                throw ExceptionUtils.toRuntime(e);
            }
        }
    }

    protected String leftQualifier() {
        return "[";
    }

    protected String rightQualifier() {
        return "]";
    }

    @Override
    public BoundSql countSql(BoundSql boundSql) {
        String sqlString = boundSql.getSqlString();
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        // .含有 order by 去掉它
        if (sqlString.toLowerCase().contains("order by")) {
            Select selectStatement = parseSelect(sqlString);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            if (plainSelect.getOrderByElements() != null) {
                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                plainSelect.setOrderByElements(null);
                sqlString = selectStatement.toString();
                plainSelect.setOrderByElements(orderByElements);
            }
        }
        // .拼 count 语句
        String sqlBuilder = "SELECT COUNT(*) FROM (" + sqlString + ") as TEMP_T";
        return new BoundSql.BoundSqlObj(sqlBuilder, paramArrays.toArray());
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        String sqlString = boundSql.getSqlString();
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(sqlString);
        //
        if (sqlString.toLowerCase().contains("order by")) {
            Select selectStatement = parseSelect(sqlString);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            if (plainSelect.getOrderByElements() == null) {
                sqlBuilder.append(" ORDER BY CURRENT_TIMESTAMP");
            }
        } else {
            sqlBuilder.append(" ORDER BY CURRENT_TIMESTAMP");
        }
        //
        sqlBuilder.append(" offset ? rows fetch next ? rows only");
        paramArrays.add(start);
        paramArrays.add(limit);
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), paramArrays.toArray());
    }
}
