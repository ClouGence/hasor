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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Informix 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class InformixDialect implements SqlDialect {
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
    public BoundSql getPageSql(BoundSql boundSql, int start, int limit) {
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> newParam = new ArrayList<>();
        sqlBuilder.append("SELECT ");
        if (start > 0) {
            sqlBuilder.append(" SKIP ? ");
            newParam.add(start);
        }
        if (limit > 0) {
            sqlBuilder.append(" FIRST ? ");
            newParam.add(limit);
        }
        sqlBuilder.append(" * FROM ( ");
        sqlBuilder.append(boundSql.getSqlString());
        sqlBuilder.append(" ) TEMP_T ");
        //
        paramArrays.addAll(0, newParam);
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), paramArrays.toArray());
    }
}