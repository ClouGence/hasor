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
import net.hasor.db.types.mapping.FieldInfo;
import net.hasor.db.types.mapping.TableInfo;

/**
 * DB2 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class Db2Dialect implements SqlDialect {
    @Override
    public String buildSelect(TableInfo tableInfo, FieldInfo fieldInfo) {
        return "\"" + fieldInfo.getColumnName() + "\"";
    }

    @Override
    public String buildTableName(TableInfo tableInfo) {
        return "\"" + tableInfo.getTableName() + "\"";
    }

    @Override
    public String buildConditionName(TableInfo tableInfo, FieldInfo fieldInfo) {
        return "\"" + fieldInfo.getColumnName() + "\"";
    }

    @Override
    public BoundSql getPageSql(String sqlString, Object[] paramArray, int start, int limit) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS ROW_ID FROM ( ");
        sqlBuilder.append(sqlString);
        sqlBuilder.append(" ) AS TMP_PAGE) TMP_PAGE WHERE ROW_ID BETWEEN ? AND ?");
        //
        Object[] destArgs = new Object[paramArray.length + 2];
        System.arraycopy(paramArray, 0, destArgs, 0, paramArray.length);
        destArgs[paramArray.length] = start;
        destArgs[paramArray.length + 1] = limit;
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), destArgs);
    }
}