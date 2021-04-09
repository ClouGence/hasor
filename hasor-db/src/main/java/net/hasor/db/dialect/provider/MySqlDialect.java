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
package net.hasor.db.dialect.provider;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.InsertSqlDialect;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MySQL 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlDialect extends AbstractDialect implements SqlDialect, InsertSqlDialect {
    @Override
    protected String keyWordsResource() {
        return "/META-INF/hasor-framework/db-keywords/mysql.keywords";
    }

    @Override
    protected String defaultQualifier() {
        return "`";
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        StringBuilder sqlBuilder = new StringBuilder(boundSql.getSqlString());
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        if (start <= 0) {
            sqlBuilder.append(" LIMIT ?");
            paramArrays.add(limit);
        } else {
            sqlBuilder.append(" LIMIT ?, ?");
            paramArrays.add(start);
            paramArrays.add(limit);
        }
        //
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), paramArrays.toArray());
    }

    @Override
    public boolean supportInsertIgnore(List<ColumnDef> primaryColumns) {
        return true;
    }

    @Override
    public String insertWithIgnore(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        // insert ignore t(id, name) values (?, ?);
        String allColumns = buildAllColumns(useQualifier, tableDef, insertColumns);
        int fieldCount = insertColumns.size();
        return "INSERT IGNORE " + tableName(useQualifier, tableDef) + " ( " + allColumns + " ) VALUES ( " + StringUtils.repeat(",?", fieldCount).substring(1) + " )";
    }

    @Override
    public boolean supportInsertIgnoreFromSelect(List<ColumnDef> primaryColumns) {
        return true;
    }

    @Override
    public String insertIgnoreFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        // insert ignore t(id, name) select ...
        String allColumns = buildAllColumns(useQualifier, tableDef, insertColumns);
        return "INSERT IGNORE " + tableName(useQualifier, tableDef) + " ( " + allColumns + " )";
    }

    @Override
    public boolean supportInsertReplace(List<ColumnDef> primaryColumns) {
        return true;
    }

    @Override
    public String insertWithReplace(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        // replace into t(id, name) values (?, ?);
        String allColumns = buildAllColumns(useQualifier, tableDef, insertColumns);
        int fieldCount = insertColumns.size();
        return "REPLACE INTO " + tableName(useQualifier, tableDef) + " ( " + allColumns + " ) VALUES ( " + StringUtils.repeat(",?", fieldCount).substring(1) + " )";
    }

    @Override
    public boolean supportInsertReplaceFromSelect(List<ColumnDef> primaryColumns) {
        return true;
    }

    @Override
    public String insertWithReplaceFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        // replace into t(id, name) values (?, ?);
        String allColumns = buildAllColumns(useQualifier, tableDef, insertColumns);
        int fieldCount = insertColumns.size();
        return "REPLACE INTO " + tableName(useQualifier, tableDef) + " ( " + allColumns + " )";
    }

    private String buildAllColumns(boolean useQualifier, TableDef tableDef, List<ColumnDef> insertColumns) {
        return insertColumns.stream().map(fieldInfo -> {
            return columnName(useQualifier, tableDef, fieldInfo);
        }).reduce((s1, s2) -> {
            return s1 + " , " + s2;
        }).orElse("");
    }
}
