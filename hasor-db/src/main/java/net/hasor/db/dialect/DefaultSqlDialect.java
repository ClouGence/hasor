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
import java.util.Set;

/**
 * 默认 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSqlDialect implements SqlDialect {
    @Override
    public Set<String> keywords() {
        return Collections.emptySet();
    }

    @Override
    public String tableName(boolean useQualifier, TableDef tableDef) {
        if (StringUtils.isBlank(tableDef.getCategory())) {
            return tableDef.getTableName();
        } else {
            return tableDef.getCategory() + "." + tableDef.getTableName();
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
}
