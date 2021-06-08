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
import net.hasor.db.dialect.PageSqlDialect;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SqlServer2005 的 SqlDialect 实现 (form mybatis-plus-extension-3.3.0.jar ,com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLServer2005Dialect)
 * @author hubin
 * @since 2016-11-10
 */
public class SqlServer2005Dialect extends SqlServerDialect implements PageSqlDialect {
    private static String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.indexOf("order by");
        if (orderByIndex != -1) {
            return sql.substring(orderByIndex);
        } else {
            return "";
        }
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        String sqlString = boundSql.getSqlString();
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(sqlString);
        String distinctStr = "";
        String loweredString = sqlString.toLowerCase();
        String sqlPartString = sqlString;
        if (loweredString.trim().toLowerCase().startsWith("select")) {
            int index = 6;
            if (loweredString.toLowerCase().startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);
        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (StringUtils.isBlank(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = start + 1;
        long secondParam = start + limit;
        sqlString = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " + //
                " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder + ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
                //FIX#299：原因：mysql中limit 10(offset,size) 是从第10开始（不包含10）,；而这里用的BETWEEN是两边都包含，所以改为offset+1
                firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        //
        paramArrays.add(firstParam);
        paramArrays.add(secondParam);
        return new BoundSql.BoundSqlObj(sqlString, paramArrays.toArray());
    }
}
