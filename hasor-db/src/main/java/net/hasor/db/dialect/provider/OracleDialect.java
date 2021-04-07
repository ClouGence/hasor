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
import net.hasor.db.mapping.FieldInfo;
import net.hasor.utils.StringUtils;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Oracle 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleDialect implements SqlDialect, InsertSqlDialect {
    @Override
    public String tableName(boolean useQualifier, String category, String tableName) {
        if (StringUtils.isBlank(category)) {
            return fmtQualifier(useQualifier, tableName);
        } else {
            return fmtQualifier(useQualifier, category) + "." + fmtQualifier(useQualifier, tableName);
        }
    }

    @Override
    public String columnName(boolean useQualifier, String category, String tableName, String columnName, JDBCType jdbcType, Class<?> javaType) {
        return fmtQualifier(useQualifier, columnName);
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

    private static String fmtQualifier(boolean useQualifier, String fmtString) {
        String qualifier = useQualifier ? "\"" : "";
        return qualifier + fmtString + qualifier;
    }

    @Override
    public boolean supportInsertIgnore(List<FieldInfo> pkFields) {
        return !pkFields.isEmpty();
    }

    @Override
    public String insertWithIgnore(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
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
        List<FieldInfo> pkColumns = insertFields.stream().filter(FieldInfo::isPrimary).collect(Collectors.toList());
        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, category, tableName, insertFields, pkColumns);
        StringBuilder mergeWhenNotMatched = buildMergeInfoWhenNotMatched(useQualifier, insertFields);
        return mergeBasic.toString() + " " + mergeWhenNotMatched.toString();
    }

    @Override
    public boolean supportInsertIgnoreFromSelect(List<FieldInfo> pkFields) {
        return false;
    }

    @Override
    public String insertIgnoreFromSelect(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportInsertReplace(List<FieldInfo> pkFields) {
        return !pkFields.isEmpty();
    }

    @Override
    public String insertWithReplace(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
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
        List<FieldInfo> pkColumns = insertFields.stream().filter(FieldInfo::isPrimary).collect(Collectors.toList());
        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, category, tableName, insertFields, pkColumns);
        StringBuilder mergeWhenMatched = buildMergeInfoWhenMatched(useQualifier, insertFields);
        return mergeBasic.toString() + " " + mergeWhenMatched.toString();
    }

    @Override
    public boolean supportInsertReplaceFromSelect(List<FieldInfo> pkFields) {
        return false;
    }

    @Override
    public String insertWithReplaceFromSelect(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
        throw new UnsupportedOperationException();
    }

    private static StringBuilder buildMergeInfoBasic(boolean useQualifier, String category, String tableName, List<FieldInfo> allColumns, List<FieldInfo> pkColumns) {
        StringBuilder mergeBuilder = new StringBuilder();
        String finalTableName = fmtQualifier(useQualifier, tableName);
        if (StringUtils.isNotBlank(category)) {
            finalTableName = fmtQualifier(useQualifier, category) + "." + finalTableName;
        }
        mergeBuilder.append("MERGE INTO " + finalTableName + " TMP USING( SELECT ");
        for (int i = 0; i < allColumns.size(); i++) {
            FieldInfo fieldInfo = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            mergeBuilder.append("? " + fmtQualifier(useQualifier, fieldInfo.getColumnName()));
        }
        mergeBuilder.append(" FROM dual) SRC ON (");
        for (int i = 0; i < pkColumns.size(); i++) {
            if (i != 0) {
                mergeBuilder.append(" AND ");
            }
            String pkColumn = fmtQualifier(useQualifier, pkColumns.get(i).getColumnName());
            mergeBuilder.append("TMP." + pkColumn + " = SRC." + pkColumn);
        }
        mergeBuilder.append(")");
        return mergeBuilder;
    }

    private static StringBuilder buildMergeInfoWhenNotMatched(boolean useQualifier, List<FieldInfo> allColumns) {
        String allColumnString = allColumns.stream().map(fieldInfo -> {
            return fmtQualifier(useQualifier, fieldInfo.getColumnName());
        }).reduce((s1, s2) -> s1 + "," + s2).orElse("");
        //
        StringBuilder mergeBuilder = new StringBuilder();
        mergeBuilder.append("WHEN NOT MATCHED THEN ");
        mergeBuilder.append("INSERT(" + allColumnString + ") ");
        mergeBuilder.append("VALUES( ");
        for (int i = 0; i < allColumns.size(); i++) {
            FieldInfo column = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            mergeBuilder.append("SRC." + fmtQualifier(useQualifier, column.getColumnName()));
        }
        mergeBuilder.append(")");
        //
        return mergeBuilder;
    }

    private static StringBuilder buildMergeInfoWhenMatched(boolean useQualifier, List<FieldInfo> allColumns) {
        StringBuilder mergeBuilder = new StringBuilder();
        mergeBuilder.append("WHEN MATCHED THEN ");
        mergeBuilder.append("UPDATE SET ");
        for (int i = 0; i < allColumns.size(); i++) {
            FieldInfo column = allColumns.get(i);
            if (i != 0) {
                mergeBuilder.append(" , ");
            }
            String columnName = fmtQualifier(useQualifier, column.getColumnName());
            mergeBuilder.append(columnName + " = SRC." + columnName);
        }
        mergeBuilder.append(" ");
        return mergeBuilder;
    }
}
