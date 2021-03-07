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
import net.hasor.db.dialect.MultipleInsertSqlDialect;
import net.hasor.utils.StringUtils;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Oracle 的 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleDialect implements MultipleInsertSqlDialect {
    @Override
    public String tableName(String category, String tableName) {
        if (StringUtils.isBlank(category)) {
            return "\"" + tableName + "\"";
        } else {
            return "\"" + category + "\".\"" + tableName + "\"";
        }
    }

    @Override
    public String columnName(String category, String tableName, String columnName, JDBCType jdbcType, Class<?> javaType) {
        return "\"" + columnName + "\"";
    }

    @Override
    public BoundSql countSql(BoundSql boundSql) {
        String sqlBuilder = "SELECT COUNT(*) FROM (" + boundSql.getSqlString() + ") TEMP_T";
        return new BoundSql.BoundSqlObj(sqlBuilder, boundSql.getArgs());
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        List<Object> paramArrays = new ArrayList<>(Arrays.asList(boundSql.getArgs()));
        //
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( ");
        sqlBuilder.append(boundSql.getSqlString());
        sqlBuilder.append(" ) TMP WHERE ROWNUM <= ? ) WHERE ROW_ID > ?");
        //
        paramArrays.add(start + limit);
        paramArrays.add(start);
        return new BoundSql.BoundSqlObj(sqlBuilder.toString(), paramArrays.toArray());
    }

    // multipleRecordInsert
    //                     insert all
    //                       into jack_20170206_aa(a,b) values('4014033')
    //                       into jack_20170206_aa(a,b) values('4065304')
    //                       into jack_20170206_aa(a,b) values('4088136')
    //                       into jack_20170206_aa(a,b) values('4092405')
    //                     select 1 from dual;
    @Override
    public String multipleRecordInsertPrepare() {
        return "insert all";
    }

    @Override
    public String multipleRecordInsertSplitRecord() {
        return "";
    }

    @Override
    public String multipleRecordInsertBeforeValues(boolean firstRecord, String tableNameAndColumn) {
        return "into " + tableNameAndColumn + " values (";
    }

    @Override
    public String multipleRecordInsertAfterValues() {
        return ")";
    }

    @Override
    public String multipleRecordInsertFinish() {
        return "select 1 from dual";
    }
}
