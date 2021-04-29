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
package net.hasor.db.metadata;
import java.sql.SQLException;
import java.util.Map;

/**
 * 元信息服务
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface MetaDataService {
    /** 获取版本信息 */
    public String getVersion() throws SQLException;

    /** 默认情况下不使用 SQL 标识符包裹 大小写策略。 */
    public CaseSensitivityType getPlain() throws SQLException;

    /** 当使用 SQL 标识符包裹下 大小写策略。 */
    public CaseSensitivityType getDelimited() throws SQLException;

    /** 获取当前 Schema */
    public String getCurrentSchema() throws SQLException;

    /** 获取当前 Catalog */
    public String getCurrentCatalog() throws SQLException;

    /** 搜索表 */
    public TableDef searchTable(String catalog, String schema, String table) throws SQLException;

    public Map<String, ColumnDef> getColumnMap(String catalog, String schema, String table) throws SQLException;
}