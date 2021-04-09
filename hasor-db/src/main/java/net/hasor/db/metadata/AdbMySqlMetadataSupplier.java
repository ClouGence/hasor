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
package net.hasor.db.metadata;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.adb.mysql.*;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 高效且完整的 Adb For MySql 元信息获取，参考资料：
 *
 * https://dev.mysql.com/doc/refman/8.0/en/information-schema.html,
 * https://help.aliyun.com/document_detail/197326.html
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AdbMySqlMetadataSupplier extends AbstractMetadataSupplier {
    public AdbMySqlMetadataSupplier(Connection connection) {
        super(connection);
    }

    public AdbMySqlMetadataSupplier(DataSource dataSource) {
        super(dataSource);
    }

    public String getDbVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            Map<String, Object> mapObject = new JdbcTemplate(conn).queryForMap("select adb_version()");
            if (mapObject == null) {
                return null;
            } else {
                return mapObject.get("source_version").toString();
            }
        }
    }

    public List<AdbMySqlSchema> getSchemas() throws SQLException {
        String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA";
        try (Connection conn = this.connectSupplier.get()) {
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
        try (Connection conn = this.connectSupplier.get()) {
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

    public Map<String, List<AdbMySqlTable>> getTables(String... schemaName) throws SQLException {
        schemaName = (schemaName == null) ? new String[0] : schemaName;
        ArrayList<String> schemaList = new ArrayList<>();
        for (String schema : schemaName) {
            if (StringUtils.isNotBlank(schema)) {
                schemaList.add(schema);
            }
        }
        if (schemaList.isEmpty()) {
            return Collections.emptyMap();
        }
        if (schemaList.size() > 1000) {
            throw new IndexOutOfBoundsException("Batch query schema Batch size out of 1000");
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA in " + buildWhereIn(schemaList);
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaList.toArray());
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<AdbMySqlTable>> resultData = new HashMap<>();
            mapList.forEach(recordMap -> {
                String dbName = safeToString(recordMap.get("TABLE_SCHEMA"));
                List<AdbMySqlTable> tableList = resultData.computeIfAbsent(dbName, k -> new ArrayList<>());
                AdbMySqlTable table = new AdbMySqlTable();
                table.setTableName(safeToString(recordMap.get("TABLE_NAME")));
                table.setTableType(AdbMySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
                table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
                table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
                tableList.add(table);
            });
            return resultData;
        }
    }

    public List<AdbMySqlTable> getAllTables() throws SQLException {
        String currentSchema = "MYSQL";
        try (Connection conn = this.connectSupplier.get()) {
            currentSchema = new JdbcTemplate(conn).queryForString("select database()");
        }
        return getAllTables(currentSchema);
    }

    public List<AdbMySqlTable> getAllTables(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return Collections.emptyList();
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(recordMap -> {
                AdbMySqlTable table = new AdbMySqlTable();
                table.setTableName(safeToString(recordMap.get("TABLE_NAME")));
                table.setTableType(AdbMySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
                table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
                table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
                return table;
            }).collect(Collectors.toList());
        }
    }

    public List<AdbMySqlTable> findTable(String schemaName, String... tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return Collections.emptyList();
        }
        tableName = (tableName == null) ? new String[0] : tableName;
        ArrayList<String> tableNameList = new ArrayList<>();
        for (String table : tableName) {
            if (StringUtils.isNotBlank(table)) {
                tableNameList.add(table);
            }
        }
        if (tableNameList.isEmpty()) {
            return Collections.emptyList();
        }
        if (tableNameList.size() > 1000) {
            throw new IndexOutOfBoundsException("Batch query table Batch size out of 1000");
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME in " + buildWhereIn(tableNameList);
        tableNameList.add(0, schemaName);
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, tableNameList.toArray());
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(recordMap -> {
                AdbMySqlTable table = new AdbMySqlTable();
                table.setTableName(safeToString(recordMap.get("TABLE_NAME")));
                table.setTableType(AdbMySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
                table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
                table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
                return table;
            }).collect(Collectors.toList());
        }
    }

    public AdbMySqlTable getTable(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return null;
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(recordMap -> {
                AdbMySqlTable table = new AdbMySqlTable();
                table.setTableName(safeToString(recordMap.get("TABLE_NAME")));
                table.setTableType(AdbMySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
                table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
                table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
                return table;
            }).findFirst().orElse(null);
        }
    }

    public List<AdbMySqlColumn> getColumns(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> primaryKeyList = null;
        List<Map<String, Object>> columnList = null;
        try (Connection conn = this.connectSupplier.get()) {
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
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return null;
        }
        String queryString = "select COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = 'PRIMARY' order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.get()) {
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
