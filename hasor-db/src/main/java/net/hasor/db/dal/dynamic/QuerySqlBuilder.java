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
package net.hasor.db.dal.dynamic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQL Build
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class QuerySqlBuilder implements DalBoundSql {
    private final StringBuilder queryString = new StringBuilder();
    private final List<SqlArg>  argList     = new ArrayList<>();

    public void appendSql(String sql, SqlArg... args) {
        this.queryString.append(sql);
        this.argList.addAll(Arrays.asList(args));
    }

    public void appendSql(String sql) {
        this.queryString.append(sql);
    }

    public boolean lastSpaceCharacter() {
        if (this.queryString.length() == 0) {
            return true;
        } else {
            char charAt = this.queryString.charAt(this.queryString.length() - 1);
            return charAt == ' ' || charAt == '\n' || charAt == '\t';
        }
    }

    public void appendArgs(List<SqlArg> originalArgList) {
        this.argList.addAll(originalArgList);
    }

    public void appendBuilder(DalBoundSql dalBoundSql) {
        if (dalBoundSql instanceof QuerySqlBuilder) {
            this.queryString.append(((QuerySqlBuilder) dalBoundSql).queryString);
            this.argList.addAll(dalBoundSql.getSqlArg());
        } else {
            this.queryString.append(dalBoundSql.getSqlString());
            List<SqlArg> sqlArgs = dalBoundSql.getSqlArg();
            this.argList.addAll(sqlArgs);
        }
    }

    @Override
    public List<SqlArg> getSqlArg() {
        return this.argList;
    }

    @Override
    public String getSqlString() {
        return this.queryString.toString();
    }

    @Override
    public Object[] getArgs() {
        return this.argList.stream().map(SqlArg::getValue).toArray();
    }
}
