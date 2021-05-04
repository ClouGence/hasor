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
package net.hasor.db.metadata.provider;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.*;
import net.hasor.db.metadata.domain.adb.mysql.*;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adb For MySql 3.0 元信息获取，参考资料：
 *   <li>https://dev.mysql.com/doc/refman/8.0/en/information-schema.html</li>
 *   <li>https://help.aliyun.com/document_detail/197326.html</li>
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AdbMySqlMetadataProvider extends AbstractMetadataProvider implements MetaDataService {
    private static final String TABLE = "select TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,TABLES.CREATE_TIME,TABLES.UPDATE_TIME,TABLE_COMMENT, " //
            + "MV_NAME,FIRST_REFRESH_TIME,NEXT_REFRESH_TIME_FUNC,OWNER,QUERY_REWRITE_ENABLED,REFRESH_CONDITION,REFRESH_STATE " //
            + "from INFORMATION_SCHEMA.TABLES left join INFORMATION_SCHEMA.MV_INFO on TABLES.TABLE_NAME = MV_INFO.MV_NAME and TABLES.TABLE_SCHEMA = MV_INFO.MV_SCHEMA";

    public AdbMySqlMetadataProvider(Connection connection) {
        super(connection);
    }

    public AdbMySqlMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    public String getVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            Map<String, Object> mapObject = new JdbcTemplate(conn).queryForMap("select adb_version()");
            if (mapObject == null) {
                return null;
            } else {
                return mapObject.get("source_version").toString();
            }
        }
    }

    public CaseSensitivityType getPlain() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            //https://dev.mysql.com/doc/refman/5.7/en/identifier-case-sensitivity.html
            Map<String, Object> objectMap = new JdbcTemplate(conn).queryForMap("show global variables like 'lower_case_table_names'");
            if (objectMap == null || !objectMap.containsKey("variable_name")) {
                return super.getPlain();
            }
            //
            Integer mode = safeToInteger(objectMap.get("variable_name"));
            if (mode == null) {
                return super.getPlain();
            }
            switch (mode) {
                case 0://表名按你写的SQL大小写存储，大写就大写小写就小写，比较时大小写敏感。
                    return CaseSensitivityType.Exact;
                case 1://表名转小写后存储到硬盘，比较时大小写不敏感。
                case 2://表名按你写的SQL大小写存储，大写就大写小写就小写，比较时统一转小写比较。
                    return CaseSensitivityType.Lower;
                default:
                    return super.getPlain();
            }
        }
    }

    @Override
    public String getCurrentCatalog() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select database()");
        }
    }

    public String getCurrentSchema() throws SQLException {
        return null;
    }

    @Override
    public TableDef searchTable(String catalog, String schema, String table) throws SQLException {
        String dbName = StringUtils.isNotBlank(catalog) ? catalog : schema;
        return getTable(dbName, table);
    }

    @Override
    public Map<String, ColumnDef> getColumnMap(String catalog, String schema, String table) throws SQLException {
        String dbName = StringUtils.isNotBlank(catalog) ? catalog : schema;
        List<AdbMySqlColumn> columns = this.getColumns(dbName, table);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(AdbMySqlColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    public List<AdbMySqlSchema> getSchemas() throws SQLException {
        String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(recordMap -> {
                AdbMySqlSchema schema = new AdbMySqlSchema();
                schema.setName(safeToString(recordMap.get("SCHEMA_NAME")));
                schema.setDefaultCharacterSetName(safeToString(recordMap.get("DEFAULT_CHARACTER_SET_NAME")));
                schema.setDefaultCollationName(safeToString(recordMap.get("DEFAULT_COLLATION_NAME")));
                return schema;
            }).collect(Collectors.toList());
        }
    }

    public AdbMySqlSchema getSchema(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME = ?";
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(recordMap -> {
                AdbMySqlSchema schema = new AdbMySqlSchema();
                schema.setName(safeToString(recordMap.get("SCHEMA_NAME")));
                schema.setDefaultCharacterSetName(safeToString(recordMap.get("DEFAULT_CHARACTER_SET_NAME")));
                schema.setDefaultCollationName(safeToString(recordMap.get("DEFAULT_COLLATION_NAME")));
                return schema;
            }).findFirst().orElse(null);
        }
    }

    public Map<String, List<AdbMySqlTable>> getTables(String[] schemaName) throws SQLException {
        List<String> schemaList = stringArray2List(schemaName);
        //
        String queryString;
        Object[] queryArgs;
        if (schemaList.isEmpty()) {
            queryString = TABLE;
            queryArgs = new Object[] {};
        } else {
            queryString = TABLE + " where TABLE_SCHEMA in " + buildWhereIn(schemaList);
            queryArgs = schemaList.toArray();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<AdbMySqlTable>> resultData = new HashMap<>();
            mapList.forEach(recordMap -> {
                String dbName = safeToString(recordMap.get("TABLE_SCHEMA"));
                List<AdbMySqlTable> tableList = resultData.computeIfAbsent(dbName, k -> new ArrayList<>());
                AdbMySqlTable table = convertTable(recordMap);
                tableList.add(table);
            });
            return resultData;
        }
    }

    public List<AdbMySqlTable> getAllTables() throws SQLException {
        String currentSchema = "MYSQL";
        try (Connection conn = this.connectSupplier.eGet()) {
            currentSchema = new JdbcTemplate(conn).queryForString("select database()");
        }
        return getAllTables(currentSchema);
    }

    public List<AdbMySqlTable> getAllTables(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentCatalog();
        }
        //
        String queryString;
        Object[] queryArgs;
        if (StringUtils.isNotBlank(schemaName)) {
            queryString = TABLE + " where TABLE_SCHEMA = ?";
            queryArgs = new Object[] { schemaName };
        } else {
            queryString = TABLE;
            queryArgs = new Object[0];
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertTable).collect(Collectors.toList());
        }
    }

    public List<AdbMySqlTable> findTable(String schemaName, String[] tableName) throws SQLException {
        List<String> tableList = stringArray2List(tableName);
        if (tableList.isEmpty()) {
            return Collections.emptyList();
        }
        //
        String queryString;
        Object[] queryArgs;
        if (StringUtils.isBlank(schemaName)) {
            queryString = TABLE + " where TABLE_NAME in " + buildWhereIn(tableList);
            queryArgs = tableList.toArray();
        } else {
            queryString = TABLE + " where TABLE_SCHEMA = ? and TABLE_NAME in " + buildWhereIn(tableList);
            ArrayList<String> args = new ArrayList<>(tableList);
            args.add(0, schemaName);
            queryArgs = args.toArray();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertTable).collect(Collectors.toList());
        }
    }

    public AdbMySqlTable getTable(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentCatalog();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = TABLE + " where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        Object[] queryArgs = new Object[] { schemaName, tableName };
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(this::convertTable).findFirst().orElse(null);
        }
    }

    public List<AdbMySqlColumn> getColumns(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentCatalog();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        List<Map<String, Object>> primaryKeyList = null;
        List<Map<String, Object>> columnList = null;
        try (Connection conn = this.connectSupplier.eGet()) {
            String queryStringColumn = "select TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_OCTET_LENGTH,NUMERIC_SCALE,NUMERIC_PRECISION,DATETIME_PRECISION,CHARACTER_SET_NAME,COLLATION_NAME,COLUMN_TYPE,COLUMN_COMMENT from INFORMATION_SCHEMA.COLUMNS " //
                    + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
            columnList = new JdbcTemplate(conn).queryForList(queryStringColumn, schemaName, tableName);
            if (columnList == null) {
                return Collections.emptyList();
            }
            String queryStringPrimary = "select INDEX_NAME,COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                    + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = 'PRIMARY' order by SEQ_IN_INDEX asc";
            primaryKeyList = new JdbcTemplate(conn).queryForList(queryStringPrimary, schemaName, tableName);
        }
        List<String> primaryKeyColumnNameList = primaryKeyList.stream().filter(recordMap -> {
            String indexName = safeToString(recordMap.get("INDEX_NAME"));
            return "PRIMARY".equals(indexName);
        }).map(recordMap -> {
            return safeToString(recordMap.get("COLUMN_NAME"));
        }).collect(Collectors.toList());
        //
        return columnList.stream().map(recordMap -> {
            AdbMySqlColumn column = new AdbMySqlColumn();
            column.setName(safeToString(recordMap.get("COLUMN_NAME")));
            column.setNullable(safeToBoolean(recordMap.get("IS_NULLABLE")));
            column.setDataType(safeToString(recordMap.get("DATA_TYPE")));
            column.setColumnType(safeToString(recordMap.get("COLUMN_TYPE")));
            column.setSqlType(safeToAdbMySqlTypes(recordMap.get("DATA_TYPE")));
            column.setJdbcType(columnTypeMappingToJdbcType(column.getSqlType(), column.getColumnType()));
            column.setDefaultCollationName(safeToString(recordMap.get("COLLATION_NAME")));
            column.setDefaultCharacterSetName(safeToString(recordMap.get("CHARACTER_SET_NAME")));
            column.setCharactersMaxLength(safeToLong(recordMap.get("CHARACTER_MAXIMUM_LENGTH")));
            column.setBytesMaxLength(safeToInteger(recordMap.get("CHARACTER_OCTET_LENGTH")));
            column.setDatetimePrecision(safeToInteger(recordMap.get("DATETIME_PRECISION")));
            column.setNumericPrecision(safeToInteger(recordMap.get("NUMERIC_PRECISION")));
            column.setNumericScale(safeToInteger(recordMap.get("NUMERIC_SCALE")));
            column.setComment(safeToString(recordMap.get("COLUMN_COMMENT")));
            column.setPrimaryKey(primaryKeyColumnNameList.contains(column.getName()));
            return column;
        }).collect(Collectors.toList());
    }

    public AdbMySqlPrimaryKey getPrimaryKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentCatalog();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = "select COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = 'PRIMARY' order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            AdbMySqlPrimaryKey primaryKey = new AdbMySqlPrimaryKey();
            primaryKey.setSchema(schemaName);
            primaryKey.setName("PRIMARY");
            for (Map<String, Object> ent : mapList) {
                String cName = safeToString(ent.get("COLUMN_NAME"));
                String cType = safeToString(ent.get("INDEX_TYPE"));
                primaryKey.getColumns().add(cName);
                primaryKey.getStorageType().put(cName, cType);
            }
            return primaryKey;
        }
    }

    protected AdbMySqlTable convertTable(Map<String, Object> recordMap) {
        AdbMySqlTable table = null;
        if (recordMap.get("MV_NAME") != null) {
            table = new AdbMySqlMaterialized();
            ((AdbMySqlMaterialized) table).setFirstRefreshTime(safeToDate(recordMap.get("FIRST_REFRESH_TIME")));
            ((AdbMySqlMaterialized) table).setNextRefreshTimeFunc(safeToString(recordMap.get("NEXT_REFRESH_TIME_FUNC")));
            ((AdbMySqlMaterialized) table).setOwner(safeToString(recordMap.get("OWNER")));
            ((AdbMySqlMaterialized) table).setQueryRewriteEnabled(safeToString(recordMap.get("QUERY_REWRITE_ENABLED")));
            ((AdbMySqlMaterialized) table).setRefreshCondition(safeToString(recordMap.get("REFRESH_CONDITION")));
            ((AdbMySqlMaterialized) table).setRefreshState(safeToString(recordMap.get("REFRESH_STATE")));
            table.setTableType(AdbMySqlTableType.Materialized);
        } else {
            table = new AdbMySqlTable();
            AdbMySqlTableType tableType = AdbMySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE")));
            table.setTableType(tableType == AdbMySqlTableType.Materialized ? AdbMySqlTableType.Table : tableType);
        }
        table.setCatalog(safeToString(recordMap.get("TABLE_CATALOG")));
        table.setSchema(safeToString(recordMap.get("TABLE_SCHEMA")));
        table.setTable(safeToString(recordMap.get("TABLE_NAME")));
        table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
        table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
        table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
        table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
        return table;
    }

    protected JDBCType columnTypeMappingToJdbcType(SqlType sqlType, String columnType) {
        if (sqlType != null) {
            return sqlType.getJdbcType();
        } else {
            throw new UnsupportedOperationException("passer type failed. sqlType is null ,and columnType = " + columnType);
        }
    }

    protected SqlType safeToAdbMySqlTypes(Object obj) {
        String dat = (obj == null) ? null : obj.toString();
        for (AdbMySqlTypes type : AdbMySqlTypes.values()) {
            if (type.getCodeKey().equalsIgnoreCase(dat)) {
                return type;
            }
        }
        return null;
    }
}
