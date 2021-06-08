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
package net.hasor.db.dialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 默认 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSqlDialect implements ConditionSqlDialect, PageSqlDialect, InsertSqlDialect {
    public static SqlDialect DEFAULT = new DefaultSqlDialect();

    @Override
    public Set<String> keywords() {
        return Collections.emptySet();
    }

    @Override
    public String leftQualifier() {
        return "`";
    }

    @Override
    public String rightQualifier() {
        return "`";
    }

    @Override
    public String tableName(boolean useQualifier, TableDef tableDef) {
        if (StringUtils.isBlank(tableDef.getSchema())) {
            return tableDef.getTable();
        } else {
            return tableDef.getSchema() + "." + tableDef.getTable();
        }
    }

    @Override
    public String columnName(boolean useQualifier, TableDef tableDef, ColumnDef columnDef) {
        return columnDef.getName();
    }

    @Override
    public BoundSql countSql(BoundSql boundSql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportInsertIgnore(List<ColumnDef> primaryColumns) {
        return false;
    }

    @Override
    public String insertWithIgnore(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportInsertIgnoreFromSelect(List<ColumnDef> primaryColumns) {
        return false;
    }

    @Override
    public String insertIgnoreFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportInsertReplace(List<ColumnDef> primaryColumns) {
        return false;
    }

    @Override
    public String insertWithReplace(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportInsertReplaceFromSelect(List<ColumnDef> primaryColumns) {
        return false;
    }

    @Override
    public String insertWithReplaceFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        throw new UnsupportedOperationException();
    }
}
