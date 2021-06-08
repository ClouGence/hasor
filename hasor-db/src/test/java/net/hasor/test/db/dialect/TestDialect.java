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
package net.hasor.test.db.dialect;
import net.hasor.core.exts.aop.Aop;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.PageSqlDialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;

import java.util.Set;

@Aop(TestDialectAop.class)
public class TestDialect implements PageSqlDialect {
    @Override
    public Set<String> keywords() {
        return null;
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
        return null;
    }

    @Override
    public String columnName(boolean useQualifier, TableDef tableDef, ColumnDef columnDef) {
        return null;
    }

    @Override
    public BoundSql pageSql(BoundSql boundSql, int start, int limit) {
        return null;
    }
}
