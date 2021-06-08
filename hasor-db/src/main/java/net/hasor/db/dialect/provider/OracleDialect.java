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
import net.hasor.db.dialect.PageSqlDialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Oracle 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleDialect extends AbstractDialect implements PageSqlDialect, InsertSqlDialect {
    @Override
    protected String keyWordsResource() {
        return "/META-INF/hasor-framework/db-keywords/oracle.keywords";
    }

    @Override
    protected String defaultQualifier() {
        return "\"";
    }

    @Override
    public BoundSql countSql(BoundSql boundSql) {
        String sqlBuilder = "SELECT COUNT(*) FROM (" + boundSql.getSqlString() + ") TEMP_T";
        return new BoundSql.BoundSqlObj(sqlBuilder, boundSql.getArgs());
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        String sqlString = boundSql.getSqlString();
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( ");
        sqlBuilder.append(sqlString);
        sqlBuilder.append(" ) TMP WHERE ROWNUM <= ? ) WHERE ROW_ID > ?");
        //
        paramArrays.add(start + limit);
        paramArrays.add(start);
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), paramArrays.toArray());
    }

    @Override
    public boolean supportInsertIgnore(List<ColumnDef> primaryColumns) {
        return !primaryColumns.isEmpty();
    }

    @Override
    public String insertWithIgnore(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        //        MERGE INTO DS_ENV TMP
        //        USING (SELECT 3            "ID",
        //                systimestamp GMT_CREATE,
        //                systimestamp GMT_MODIFIED,
        //                'abc'        OWNER_UID,
        //                'dev'        ENV_NAME,
        //                'dddddd'     DESCRIPTION
        //                FROM dual) SRC
        //        ON (TMP."ID" = SRC."ID")
        //        WHEN NOT MATCHED THEN
        //            INSERT ("ID", "GMT_CREATE", "GMT_MODIFIED", "OWNER_UID", "ENV_NAME", "DESCRIPTION")
        //            VALUES (SRC."ID", SRC."GMT_CREATE", SRC."GMT_MODIFIED", SRC."OWNER_UID", SRC."ENV_NAME", SRC."DESCRIPTION");
        List<ColumnDef> pkColumns = insertColumns.stream().filter(ColumnDef::isPrimaryKey).collect(Collectors.toList());
        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, tableDef, insertColumns, pkColumns);
        StringBuilder mergeWhenNotMatched = buildMergeInfoWhenNotMatched(useQualifier, insertColumns);
        return mergeBasic.toString() + " " + mergeWhenNotMatched.toString();
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
        return !primaryColumns.isEmpty();
    }

    @Override
    public String insertWithReplace(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        //        MERGE INTO DS_ENV TMP
        //        USING (SELECT 3            "ID",
        //                systimestamp GMT_CREATE,
        //                systimestamp GMT_MODIFIED,
        //                'abc'        OWNER_UID,
        //                'dev'        ENV_NAME,
        //                'dddddd'     DESCRIPTION
        //                FROM dual) SRC
        //        ON (TMP."ID" = SRC."ID")
        //        WHEN MATCHED THEN
        //                UPDATE
        //            SET "GMT_CREATE"   = SRC."GMT_CREATE",
        //                "GMT_MODIFIED" = SRC."GMT_MODIFIED",
        //                "OWNER_UID"    = SRC."OWNER_UID",
        //                "ENV_NAME"     = SRC."ENV_NAME",
        //                "DESCRIPTION"  = SRC."DESCRIPTION"
        List<ColumnDef> pkColumns = insertColumns.stream().filter(ColumnDef::isPrimaryKey).collect(Collectors.toList());
        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, tableDef, insertColumns, pkColumns);
        StringBuilder mergeWhenMatched = buildMergeInfoWhenMatched(useQualifier, insertColumns);
        return mergeBasic.toString() + " " + mergeWhenMatched.toString();
    }

    @Override
    public boolean supportInsertReplaceFromSelect(List<ColumnDef> primaryColumns) {
        return false;
    }

    @Override
    public String insertWithReplaceFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns) {
        throw new UnsupportedOperationException();
    }

    private StringBuilder buildMergeInfoBasic(boolean useQualifier, TableDef tableDef, List<ColumnDef> allColumns, List<ColumnDef> primaryColumns) {
        StringBuilder mergeBuilder = new StringBuilder();
        String finalTableName = tableName(useQualifier, tableDef);
        mergeBuilder.append("MERGE INTO " + finalTableName + " TMP USING( SELECT ");
        for (int i = 0; i < allColumns.size(); i++) {
            ColumnDef columnDef = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            mergeBuilder.append("? " + columnName(useQualifier, tableDef, columnDef));
        }
        mergeBuilder.append(" FROM dual) SRC ON (");
        for (int i = 0; i < primaryColumns.size(); i++) {
            if (i != 0) {
                mergeBuilder.append(" AND ");
            }
            String pkColumn = columnName(useQualifier, tableDef, primaryColumns.get(i));
            mergeBuilder.append("TMP." + pkColumn + " = SRC." + pkColumn);
        }
        mergeBuilder.append(")");
        return mergeBuilder;
    }

    private StringBuilder buildMergeInfoWhenNotMatched(boolean useQualifier, List<ColumnDef> allColumns) {
        String allColumnString = allColumns.stream().map(columnDef -> {
            return fmtName(useQualifier, columnDef.getName());
        }).reduce((s1, s2) -> s1 + "," + s2).orElse("");
        //
        StringBuilder mergeBuilder = new StringBuilder();
        mergeBuilder.append("WHEN NOT MATCHED THEN ");
        mergeBuilder.append("INSERT(" + allColumnString + ") ");
        mergeBuilder.append("VALUES( ");
        for (int i = 0; i < allColumns.size(); i++) {
            ColumnDef columnDef = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            mergeBuilder.append("SRC." + fmtName(useQualifier, columnDef.getName()));
        }
        mergeBuilder.append(")");
        //
        return mergeBuilder;
    }

    private StringBuilder buildMergeInfoWhenMatched(boolean useQualifier, List<ColumnDef> allColumns) {
        StringBuilder mergeBuilder = new StringBuilder();
        mergeBuilder.append("WHEN MATCHED THEN ");
        mergeBuilder.append("UPDATE SET ");
        for (int i = 0; i < allColumns.size(); i++) {
            ColumnDef columnDef = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            String columnName = fmtName(useQualifier, columnDef.getName());
            mergeBuilder.append(columnName + " = SRC." + columnName);
        }
        mergeBuilder.append(" ");
        return mergeBuilder;
    }
}
