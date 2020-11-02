/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda.segment;
/**
 * SQL 查询相关的关键字。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public enum SqlKeyword implements Segment {
    AND("AND"),//
    OR("OR"),//
    IN("IN"),//
    NOT("NOT"),//
    LIKE("LIKE"),//
    EQ("="),//
    NE("<>"),//
    GT(">"),//
    GE(">="),//
    LT("<"),//
    LE("<="),//
    IS_NULL("IS NULL"),//
    IS_NOT_NULL("IS NOT NULL"),//
    BETWEEN("BETWEEN"),//
    //
    GROUP_BY("GROUP BY"),//
    HAVING("HAVING"),//
    ORDER_BY("ORDER BY"),//
    EXISTS("EXISTS"),//
    SELECT("SELECT"),//
    COLUMNS("*"),//
    FROM("FROM"),//
    WHERE("WHERE"),//
    LEFT("("),  //
    RIGHT(")")  //
    ;
    //
    private final String sqlString;

    SqlKeyword(String sqlString) {
        this.sqlString = sqlString;
    }

    @Override
    public String getSqlSegment() {
        return this.sqlString;
    }
}