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
package net.hasor.db.dialect;
import net.hasor.db.jdbc.lambda.segment.SqlLike;
import net.hasor.db.types.mapping.FieldInfo;
import net.hasor.db.types.mapping.TableInfo;
import net.hasor.utils.StringUtils;

/**
 * SQL 方言
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SqlDialect {
    /** 生成 select 时的列信息 */
    public String buildSelect(TableInfo tableInfo, FieldInfo fieldInfo);

    /** 生成 form 后面的表名 */
    public String buildTableName(TableInfo tableInfo);

    /** 生成 where 中用到的条件名（包括 group by、order by） */
    public String buildConditionName(TableInfo tableInfo, FieldInfo fieldInfo);

    public default String buildLike(SqlLike likeType, Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "%";
        }
        switch (likeType) {
            case LEFT:
                return "CONCAT('%', ? )";
            case RIGHT:
                return "CONCAT( ? ,'%')";
            default:
                return "CONCAT('%', ? ,'%')";
        }
    }

    /** 生成 count 查询 SQL */
    public default BoundSql getCountSql(String sqlString, Object[] args) {
        return new BoundSql.BoundSqlObj("SELECT COUNT(*) FROM (" + sqlString + ") as TEMP_T", args.clone());
    }

    /** 生成分页查询 SQL */
    public BoundSql getPageSql(String sqlString, Object[] args, int start, int limit);
}