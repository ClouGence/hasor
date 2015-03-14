/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.orm.ar.dialect;
import net.hasor.db.orm.ar.SQLBuilder;
import net.hasor.db.orm.ar.dialect.hsql.HSQLBuilder;
import net.hasor.db.orm.ar.dialect.mysql.MySqlBuilder;
/**
 * 
 * @version : 2015年2月13日
 * @author 赵永春(zyc@hasor.net)
 */
public enum SQLBuilderEnum {
    /*MySQL方言*/
    MySql(MySqlBuilder.class),
    /*HSQL*/
    HSQL(HSQLBuilder.class), ;
    //    /*Oracle方言*/
    //    Oracle(OracleSqlBuilder.class);
    //
    //
    SQLBuilderEnum(Class<? extends SQLBuilder> sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }
    private Class<? extends SQLBuilder> sqlBuilder;
    public SQLBuilder createBuilder() {
        try {
            return this.sqlBuilder.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}