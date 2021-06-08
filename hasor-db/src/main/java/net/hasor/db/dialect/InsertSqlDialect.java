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
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;

import java.util.List;

/**
 * SQL 插入数据方言。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InsertSqlDialect extends SqlDialect {
    /** 是否支持 insert ignore */
    public boolean supportInsertIgnore(List<ColumnDef> primaryColumns);

    public String insertWithIgnore(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns);

    /** 是否支持 insert ignore from select */
    public boolean supportInsertIgnoreFromSelect(List<ColumnDef> primaryColumns);

    public String insertIgnoreFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns);

    /** 是否支持 insert replace */
    public boolean supportInsertReplace(List<ColumnDef> primaryColumns);

    public String insertWithReplace(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns);

    /** 是否支持 insert replace from select */
    public boolean supportInsertReplaceFromSelect(List<ColumnDef> primaryColumns);

    public String insertWithReplaceFromSelect(boolean useQualifier, TableDef tableDef, List<ColumnDef> primaryColumns, List<ColumnDef> insertColumns);
}
