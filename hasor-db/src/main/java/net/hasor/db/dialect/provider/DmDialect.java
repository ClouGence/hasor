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
import net.hasor.db.dialect.SqlDialect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 达梦 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DmDialect extends AbstractDialect implements SqlDialect/* , InsertSqlDialect */ {
    @Override
    protected String keyWordsResource() {
        return "/META-INF/hasor-framework/db-keywords/dm.keywords";
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
        StringBuilder sqlBuilder = new StringBuilder(boundSql.getSqlString());
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        // DM7/
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
    //    @Override
    //    public boolean supportInsertIgnore(List<FieldInfo> pkFields) {
    //        return !pkFields.isEmpty();
    //    }
    //
    //    @Override
    //    public String insertWithIgnore(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
    //        //        MERGE INTO DS_ENV TMP
    //        //        USING (SELECT 3            "ID",
    //        //                systimestamp GMT_CREATE,
    //        //                systimestamp GMT_MODIFIED,
    //        //                'abc'        OWNER_UID,
    //        //                'dev'        ENV_NAME,
    //        //                'dddddd'     DESCRIPTION
    //        //                FROM dual) SRC
    //        //        ON (TMP."ID" = SRC."ID")
    //        //        WHEN NOT MATCHED THEN
    //        //            INSERT ("ID", "GMT_CREATE", "GMT_MODIFIED", "OWNER_UID", "ENV_NAME", "DESCRIPTION")
    //        //            VALUES (SRC."ID", SRC."GMT_CREATE", SRC."GMT_MODIFIED", SRC."OWNER_UID", SRC."ENV_NAME", SRC."DESCRIPTION");
    //        List<FieldInfo> pkColumns = insertFields.stream().filter(FieldInfo::isPrimary).collect(Collectors.toList());
    //        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, category, tableName, insertFields, pkColumns);
    //        StringBuilder mergeWhenNotMatched = buildMergeInfoWhenNotMatched(useQualifier, insertFields);
    //        return mergeBasic.toString() + " " + mergeWhenNotMatched.toString();
    //    }
    //
    //    @Override
    //    public boolean supportInsertReplace(List<FieldInfo> pkFields) {
    //        return !pkFields.isEmpty();
    //    }
    //
    //    @Override
    //    public String insertWithReplace(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields) {
    //        //        MERGE INTO DS_ENV TMP
    //        //        USING (SELECT 3            "ID",
    //        //                systimestamp GMT_CREATE,
    //        //                systimestamp GMT_MODIFIED,
    //        //                'abc'        OWNER_UID,
    //        //                'dev'        ENV_NAME,
    //        //                'dddddd'     DESCRIPTION
    //        //                FROM dual) SRC
    //        //        ON (TMP."ID" = SRC."ID")
    //        //        WHEN MATCHED THEN
    //        //                UPDATE
    //        //            SET "GMT_CREATE"   = SRC."GMT_CREATE",
    //        //                "GMT_MODIFIED" = SRC."GMT_MODIFIED",
    //        //                "OWNER_UID"    = SRC."OWNER_UID",
    //        //                "ENV_NAME"     = SRC."ENV_NAME",
    //        //                "DESCRIPTION"  = SRC."DESCRIPTION"
    //        List<FieldInfo> pkColumns = insertFields.stream().filter(FieldInfo::isPrimary).collect(Collectors.toList());
    //        StringBuilder mergeBasic = buildMergeInfoBasic(useQualifier, category, tableName, insertFields, pkColumns);
    //        StringBuilder mergeWhenMatched = buildMergeInfoWhenMatched(useQualifier, insertFields);
    //        return mergeBasic.toString() + " " + mergeWhenMatched.toString();
    //    }
    //
    //    private static StringBuilder buildMergeInfoBasic(boolean useQualifier, String category, String tableName, List<FieldInfo> allColumns, List<FieldInfo> pkColumns) {
    //        StringBuilder mergeBuilder = new StringBuilder();
    //        String finalTableName = fmtQualifier(useQualifier, category) + "." + fmtQualifier(useQualifier, tableName);
    //        mergeBuilder.append("MERGE INTO " + finalTableName + " TMP USING( SELECT ");
    //        for (int i = 0; i < allColumns.size(); i++) {
    //            FieldInfo fieldInfo = allColumns.get(i);
    //            if (i != 0) {
    //                mergeBuilder.append(",");
    //            }
    //            mergeBuilder.append("? " + fmtQualifier(useQualifier, fieldInfo.getColumnName()));
    //        }
    //        mergeBuilder.append(" FROM dual) SRC ON (");
    //        for (int i = 0; i < pkColumns.size(); i++) {
    //            if (i != 0) {
    //                mergeBuilder.append(" AND ");
    //            }
    //            String pkColumn = fmtQualifier(useQualifier, pkColumns.get(i).getColumnName());
    //            mergeBuilder.append("TMP." + pkColumn + " = SRC." + pkColumn);
    //        }
    //        mergeBuilder.append(") ");
    //        return mergeBuilder;
    //    }
    //
    //    private static StringBuilder buildMergeInfoWhenNotMatched(boolean useQualifier, List<FieldInfo> allColumns) {
    //        String allColumnString = allColumns.stream().map(fieldInfo -> {
    //            return fmtQualifier(useQualifier, fieldInfo.getColumnName());
    //        }).reduce((s1, s2) -> s1 + "," + s2).orElse("");
    //        //
    //        StringBuilder mergeBuilder = new StringBuilder();
    //        mergeBuilder.append("WHEN NOT MATCHED THEN ");
    //        mergeBuilder.append("INSERT(" + allColumnString + ") ");
    //        mergeBuilder.append("VALUES( ");
    //        for (int i = 0; i < allColumns.size(); i++) {
    //            FieldInfo column = allColumns.get(i);
    //            if (i != 0) {
    //                mergeBuilder.append(",");
    //            }
    //            mergeBuilder.append("SRC." + fmtQualifier(useQualifier, column.getColumnName()));
    //        }
    //        mergeBuilder.append(") ");
    //        //
    //        return mergeBuilder;
    //    }
    //
    //    private static StringBuilder buildMergeInfoWhenMatched(boolean useQualifier, List<FieldInfo> allColumns) {
    //        StringBuilder mergeBuilder = new StringBuilder();
    //        mergeBuilder.append("WHEN MATCHED THEN ");
    //        mergeBuilder.append("UPDATE SET ");
    //        for (int i = 0; i < allColumns.size(); i++) {
    //            FieldInfo column = allColumns.get(i);
    //            if (i != 0) {
    //                mergeBuilder.append(",");
    //            }
    //            String columnName = fmtQualifier(useQualifier, column.getColumnName());
    //            mergeBuilder.append(columnName + " = SRC." + columnName);
    //        }
    //        mergeBuilder.append(" ");
    //        return mergeBuilder;
    //    }
}
