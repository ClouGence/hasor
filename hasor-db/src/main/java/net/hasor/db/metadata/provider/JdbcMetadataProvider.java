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
import net.hasor.db.jdbc.extractor.ColumnMapResultSetExtractor;
import net.hasor.db.jdbc.extractor.RowMapperResultSetExtractor;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.TableDef;
import net.hasor.db.metadata.domain.jdbc.*;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于 JDBC 接口的元信息获取
 * @version : 2020-04-23
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcMetadataProvider extends AbstractMetadataProvider implements MetaDataService {
    public JdbcMetadataProvider(Connection connection) {
        super(connection);
    }

    public JdbcMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    public String getVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String productName = metaData.getDatabaseProductName();
            String productVersion = metaData.getDatabaseProductVersion();
            int productMajorVersion = metaData.getDatabaseMajorVersion();
            int productMinorVersion = metaData.getDatabaseMinorVersion();
            String dbmsVer = String.format("DBMS: %s (ver. %s, v%s.%s)", productName, productVersion, productMajorVersion, productMinorVersion);
            //
            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();
            int driverMajorVersion = metaData.getDriverMajorVersion();
            int driverMinorVersion = metaData.getDriverMinorVersion();
            String driverVer = String.format("Driver: %s (ver. %s, v%s.%s)", driverName, driverVersion, driverMajorVersion, driverMinorVersion);
            //
            int jdbcMajorVersion = metaData.getJDBCMajorVersion();
            int jdbcMinorVersion = metaData.getJDBCMinorVersion();
            String jdbcVer = String.format("JDBC (v%s.%s)", jdbcMajorVersion, jdbcMinorVersion);
            //
            return dbmsVer + "\n" + driverVer + "\n" + jdbcVer;
        }
    }

    public String getCurrentCatalog() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return conn.getCatalog();
        }
    }

    public String getCurrentSchema() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return conn.getSchema();
        }
    }

    @Override
    public TableDef searchTable(String catalog, String schema, String table) throws SQLException {
        return getTable(catalog, schema, table);
    }

    @Override
    public Map<String, ColumnDef> getColumnMap(String catalog, String schema, String table) throws SQLException {
        List<JdbcColumn> columns = this.getColumns(catalog, schema, table);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(JdbcColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    public List<String> getCatalogs() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getCatalogs()) {
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return rs.getString("TABLE_CAT");
                }).extractData(resultSet);
            }
        }
    }

    public List<JdbcSchema> getSchemas() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getSchemas()) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertSchema(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     */
    public List<JdbcSchema> getSchemas(String catalog) throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getSchemas(catalog, null)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertSchema(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
            }
        }
    }

    /**
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     */
    public JdbcSchema getSchemaByName(String catalog, String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getSchemas(catalog, schemaName)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                List<JdbcSchema> jdbcSchemas = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertSchema(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
                //
                return jdbcSchemas.stream().filter(jdbcSchema -> {
                    return StringUtils.equals(jdbcSchema.getSchema(), schemaName);
                }).findFirst().orElse(null);
            }
        }
    }

    public List<JdbcTable> getAllTables() throws SQLException {
        String catalog = null;
        String schema = null;
        try (Connection conn = this.connectSupplier.eGet()) {
            catalog = conn.getCatalog();
            schema = conn.getSchema();
        }
        return this.getAllTables(catalog, schema);
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     */
    public List<JdbcTable> getAllTables(String catalog, String schemaName) throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getTables(catalog, schemaName, null, null)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertTable(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     */
    public List<JdbcTable> findTables(String catalog, String schemaName, String[] tables) throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getTables(catalog, schemaName, null, null)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                List<JdbcTable> jdbcTables = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertTable(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
                if (jdbcTables == null) {
                    return Collections.emptyList();
                }
                //
                List<String> names = Arrays.asList(tables);
                return jdbcTables.stream().filter(jdbcTable -> {
                    return names.contains(jdbcTable.getTable());
                }).collect(Collectors.toList());
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public JdbcTable getTable(String catalog, String schemaName, String table) throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getTables(catalog, schemaName, table, null)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                List<JdbcTable> jdbcTables = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertTable(rowMapper.mapRow(rs, rowNum));
                }).extractData(resultSet);
                if (jdbcTables.isEmpty()) {
                    return null;
                } else {
                    return jdbcTables.get(0);
                }
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcColumn> getColumns(String catalog, String schemaName, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return Collections.emptyList();
        }
        JdbcPrimaryKey primaryKey = getPrimaryKey(catalog, schemaName, table);
        List<JdbcIndex> uniqueKey = getUniqueKey(catalog, schemaName, table);
        Set<String> uniqueColumns = uniqueKey.stream().flatMap((Function<JdbcIndex, Stream<String>>) jdbcIndex -> {
            return jdbcIndex.getColumns().stream();
        }).collect(Collectors.toSet());
        //
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            //
            List<JdbcColumn> jdbcColumns = null;
            try (ResultSet resultSet = metaData.getColumns(catalog, schemaName, table, null)) {
                final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
                jdbcColumns = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertColumn(rowMapper.mapRow(rs, rowNum), primaryKey, uniqueColumns);
                }).extractData(resultSet);
            }
            if (jdbcColumns == null) {
                return Collections.emptyList();
            } else {
                return jdbcColumns;
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database; "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public JdbcPrimaryKey getPrimaryKey(String catalog, String schemaName, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, schemaName, table)) {
                List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(primaryKeys);
                if (mapList == null || mapList.isEmpty()) {
                    return null;
                }
                //
                Map<String, Optional<JdbcPrimaryKey>> pkMap = mapList.stream().sorted((o1, o2) -> {
                    Integer o1KeySeq = safeToInteger(o1.get("KEY_SEQ"));
                    Integer o2KeySeq = safeToInteger(o2.get("KEY_SEQ"));
                    if (o1KeySeq != null && o2KeySeq != null) {
                        return Integer.compare(o1KeySeq, o2KeySeq);
                    } else {
                        return 0;
                    }
                }).map(this::convertPrimaryKey).collect(Collectors.groupingBy(o -> {
                    // group by (schema + name)
                    return o.getSchema() + "," + o.getName();
                }, Collectors.reducing((pk1, pk2) -> {
                    // reducing group by data in to one.
                    pk1.getColumns().addAll(pk2.getColumns());
                    return pk1;
                })));
                if (pkMap.size() > 1) {
                    throw new SQLException("Data error encountered multiple primary keys '" + StringUtils.join(pkMap.keySet().toArray(), "','") + "'");
                }
                //
                Optional<JdbcPrimaryKey> primaryKeyOptional = pkMap.values().stream().findFirst().orElse(Optional.empty());
                return primaryKeyOptional.orElse(null);
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcIndex> getIndexes(String catalog, String schemaName, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return Collections.emptyList();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet indexInfo = metaData.getIndexInfo(catalog, schemaName, table, false, false)) {
                List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(indexInfo);
                if (mapList == null || mapList.isEmpty()) {
                    return Collections.emptyList();
                }
                //
                return mapList.stream().filter(recordMap -> {
                    // Oracle 数据库使用 JDBC，可能出现一个 null 名字的索引。
                    return StringUtils.isNotBlank(safeToString(recordMap.get("INDEX_NAME")));
                }).sorted((o1, o2) -> {
                    // sort by ORDINAL_POSITION
                    Integer o1Index = safeToInteger(o1.get("ORDINAL_POSITION"));
                    Integer o2Index = safeToInteger(o2.get("ORDINAL_POSITION"));
                    if (o1Index != null && o2Index != null) {
                        return Integer.compare(o1Index, o2Index);
                    }
                    return 0;
                }).map(this::convertIndex).collect(Collectors.groupingBy(o -> {
                    // group by (tableName + indexName)
                    return o.getTableName() + "," + o.getName();
                }, Collectors.reducing((idx1, idx2) -> {
                    // reducing group by data in to one.
                    idx1.getColumns().addAll(idx2.getColumns());
                    idx1.getStorageType().putAll(idx2.getStorageType());
                    return idx1;
                }))).values().stream().map(o -> {
                    return o.orElse(null);
                }).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public JdbcIndex getIndexes(String catalog, String schemaName, String table, String indexName) throws SQLException {
        List<JdbcIndex> indexList = getIndexes(catalog, schemaName, table);
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }
        return indexList.stream().filter(indexItem -> {
            return StringUtils.equals(indexItem.getName(), indexName);
        }).findFirst().orElse(null);
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcIndex> getUniqueKey(String catalog, String schemaName, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return null;
        }
        List<JdbcIndex> indices = getIndexes(catalog, schemaName, table);
        if (indices == null || indices.isEmpty()) {
            return Collections.emptyList();
        }
        //
        return indices.stream().filter(JdbcIndex::isUnique).collect(Collectors.toList());
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcForeignKey> getForeignKey(String catalog, String schemaName, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return Collections.emptyList();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            DatabaseMetaData metaData = conn.getMetaData();
            // try (ResultSet exportedKeys = metaData.getExportedKeys(catalog, schemaName, table)) {
            //     List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(exportedKeys);
            // }
            try (ResultSet importedKeys = metaData.getImportedKeys(catalog, schemaName, table)) {
                List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(importedKeys);
                if (mapList == null || mapList.isEmpty()) {
                    return Collections.emptyList();
                }
                //
                return mapList.stream().sorted((o1, o2) -> {
                    Integer o1KeySeq = safeToInteger(o1.get("KEY_SEQ"));
                    Integer o2KeySeq = safeToInteger(o2.get("KEY_SEQ"));
                    if (o1KeySeq != null && o2KeySeq != null) {
                        return Integer.compare(o1KeySeq, o2KeySeq);
                    } else {
                        return 0;
                    }
                }).map(this::convertForeignKey).collect(Collectors.groupingBy(o -> {
                    // group by (schema + name)
                    return o.getSchema() + "," + o.getName();
                }, Collectors.reducing((fk1, fk2) -> {
                    // reducing group by data in to one.
                    fk1.getColumns().addAll(fk2.getColumns());
                    fk1.getReferenceMapping().putAll(fk2.getReferenceMapping());
                    return fk1;
                }))).values().stream().map(o -> {
                    return o.orElse(null);
                }).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcConstraint> getConstraint(String catalog, String schemaName, String table) throws SQLException {
        JdbcPrimaryKey primaryKey = getPrimaryKey(catalog, schemaName, table);
        List<JdbcForeignKey> foreignKey = getForeignKey(catalog, schemaName, table);
        List<JdbcConstraint> constraintList = new ArrayList<>();
        if (primaryKey != null) {
            constraintList.add(primaryKey);
        }
        constraintList.addAll(foreignKey);
        return constraintList;
    }

    /**
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow the search
     * @param schemaName a schema name; must match the schema name as it is stored in the database;
     *        "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow the search
     * @param table a table name; must match the table name as it is stored in the database
     */
    public List<JdbcConstraint> getConstraint(String catalog, String schemaName, String table, JdbcConstraintType... types) throws SQLException {
        List<JdbcConstraint> constraintList = getConstraint(catalog, schemaName, table);
        if (constraintList == null || constraintList.isEmpty()) {
            return constraintList;
        }
        return constraintList.stream().filter(constraint -> {
            for (JdbcConstraintType constraintType : types) {
                if (constraintType == constraint.getConstraintType()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    protected JdbcSchema convertSchema(Map<String, Object> recordMap) {
        JdbcSchema jdbcSchema = new JdbcSchema();
        jdbcSchema.setSchema(safeToString(recordMap.get("TABLE_SCHEM")));
        jdbcSchema.setCatalog(safeToString(recordMap.get("TABLE_CATALOG")));
        return jdbcSchema;
    }

    protected JdbcTable convertTable(Map<String, Object> rs) throws SQLException {
        JdbcTable jdbcSchema = new JdbcTable();
        jdbcSchema.setCatalog(safeToString(rs.get("TABLE_CAT")));
        jdbcSchema.setSchema(safeToString(rs.get("TABLE_SCHEM")));
        jdbcSchema.setTable(safeToString(rs.get("TABLE_NAME")));
        jdbcSchema.setTableTypeString(safeToString(rs.get("TABLE_TYPE")));
        jdbcSchema.setTableType(JdbcTableType.valueOfCode(jdbcSchema.getTableTypeString()));
        jdbcSchema.setComment(safeToString(rs.get("REMARKS")));
        //
        jdbcSchema.setTypeCatalog(safeToString(rs.get("TYPE_CAT")));
        jdbcSchema.setTypeSchema(safeToString(rs.get("TYPE_SCHEM")));
        jdbcSchema.setTypeName(safeToString(rs.get("TYPE_NAME")));
        jdbcSchema.setSelfReferencingColName(safeToString(rs.get("SELF_REFERENCING_COL_NAME")));
        jdbcSchema.setRefGeneration(safeToString(rs.get("REF_GENERATION")));
        return jdbcSchema;
    }

    protected JdbcColumn convertColumn(Map<String, Object> rs, JdbcPrimaryKey primaryKey, Set<String> uniqueKey) {
        JdbcColumn jdbcColumn = new JdbcColumn();
        jdbcColumn.setTableCatalog(safeToString(rs.get("TABLE_CAT")));
        jdbcColumn.setTableSchema(safeToString(rs.get("TABLE_SCHEM")));
        jdbcColumn.setTableName(safeToString(rs.get("TABLE_NAME")));
        jdbcColumn.setColumnName(safeToString(rs.get("COLUMN_NAME")));
        //
        String isNullable = safeToString(rs.get("IS_NULLABLE"));
        if ("YES".equals(isNullable)) {
            jdbcColumn.setNullable(true);
        } else if ("NO".equals(isNullable)) {
            jdbcColumn.setNullable(false);
        } else {
            jdbcColumn.setNullable(null);
        }
        jdbcColumn.setNullableType(JdbcNullableType.valueOfCode(safeToInteger(rs.get("NULLABLE"))));
        jdbcColumn.setColumnType(safeToString(rs.get("TYPE_NAME")));
        jdbcColumn.setJdbcNumber(safeToInteger(rs.get("DATA_TYPE")));
        JDBCType jdbcType = null;
        try {
            jdbcType = JDBCType.valueOf(safeToInteger(rs.get("DATA_TYPE")));
        } catch (Exception e) { /**/ }
        jdbcColumn.setJdbcType(jdbcType);
        jdbcColumn.setSqlType(JdbcSqlTypes.valueOfCode(jdbcColumn.getJdbcNumber()));
        //
        jdbcColumn.setColumnSize(safeToInteger(rs.get("COLUMN_SIZE")));
        jdbcColumn.setComment(safeToString(rs.get("REMARKS")));
        jdbcColumn.setScopeCatalog(safeToString(rs.get("SCOPE_CATALOG")));
        jdbcColumn.setScopeSchema(safeToString(rs.get("SCOPE_SCHEMA")));
        jdbcColumn.setScopeTable(safeToString(rs.get("SCOPE_TABLE")));
        //
        String isAutoincrement = safeToString(rs.get("IS_AUTOINCREMENT"));
        if ("YES".equals(isAutoincrement)) {
            jdbcColumn.setAutoincrement(true);
        } else if ("NO".equals(isAutoincrement)) {
            jdbcColumn.setAutoincrement(false);
        } else {
            jdbcColumn.setAutoincrement(null);
        }
        String isGeneratedColumn = safeToString(rs.get("IS_GENERATEDCOLUMN"));
        if ("YES".equals(isGeneratedColumn)) {
            jdbcColumn.setGeneratedColumn(true);
        } else if ("NO".equals(isGeneratedColumn)) {
            jdbcColumn.setGeneratedColumn(false);
        } else {
            jdbcColumn.setGeneratedColumn(null);
        }
        //
        jdbcColumn.setDecimalDigits(safeToInteger(rs.get("DECIMAL_DIGITS")));
        jdbcColumn.setNumberPrecRadix(safeToInteger(rs.get("NUM_PREC_RADIX")));
        jdbcColumn.setDefaultValue(safeToString(rs.get("COLUMN_DEF")));
        jdbcColumn.setCharOctetLength(safeToInteger(rs.get("CHAR_OCTET_LENGTH")));
        jdbcColumn.setOrdinalPosition(safeToInteger(rs.get("ORDINAL_POSITION")));
        jdbcColumn.setSourceDataType(safeToInteger(rs.get("SOURCE_DATA_TYPE")));
        //
        if (primaryKey != null) {
            List<String> pkColumns = primaryKey.getColumns();
            if (pkColumns.contains(jdbcColumn.getColumnName())) {
                jdbcColumn.setPrimaryKey(true);
            }
        }
        jdbcColumn.setUniqueKey(uniqueKey.contains(jdbcColumn.getColumnName()));
        return jdbcColumn;
    }

    protected JdbcPrimaryKey convertPrimaryKey(Map<String, Object> recordMap) {
        JdbcPrimaryKey primaryKey = new JdbcPrimaryKey();
        primaryKey.setCatalog(safeToString(recordMap.get("TABLE_CAT")));
        primaryKey.setSchema(safeToString(recordMap.get("TABLE_SCHEM")));
        primaryKey.setTable(safeToString(recordMap.get("TABLE_NAME")));
        primaryKey.setName(safeToString(recordMap.get("PK_NAME")));
        primaryKey.setConstraintType(JdbcConstraintType.PrimaryKey);
        //
        primaryKey.getColumns().add(safeToString(recordMap.get("COLUMN_NAME")));
        return primaryKey;
    }

    protected JdbcIndex convertIndex(Map<String, Object> recordMap) {
        JdbcIndex jdbcIndex = new JdbcIndex();
        jdbcIndex.setTableCatalog(safeToString(recordMap.get("TABLE_CAT")));
        jdbcIndex.setTableSchema(safeToString(recordMap.get("TABLE_SCHEM")));
        jdbcIndex.setTableName(safeToString(recordMap.get("TABLE_NAME")));
        jdbcIndex.setName(safeToString(recordMap.get("INDEX_NAME")));
        jdbcIndex.setUnique(!safeToBoolean(recordMap.get("NON_UNIQUE")));
        //
        jdbcIndex.setIndexType(JdbcIndexType.valueOfCode(safeToInteger(recordMap.get("TYPE"))));
        jdbcIndex.setIndexQualifier(safeToString(recordMap.get("INDEX_QUALIFIER")));
        jdbcIndex.setCardinality(safeToLong(recordMap.get("CARDINALITY")));
        jdbcIndex.setPages(safeToLong(recordMap.get("PAGES")));
        jdbcIndex.setFilterCondition(safeToString(recordMap.get("FILTER_CONDITION")));
        //
        String columnName = safeToString(recordMap.get("COLUMN_NAME"));
        String ascOrDesc = safeToString(recordMap.get("ASC_OR_DESC"));
        jdbcIndex.getColumns().add(columnName);
        jdbcIndex.getStorageType().put(columnName, ascOrDesc);
        return jdbcIndex;
    }

    protected JdbcForeignKey convertForeignKey(Map<String, Object> recordMap) {
        JdbcForeignKey foreignKey = new JdbcForeignKey();
        foreignKey.setCatalog(safeToString(recordMap.get("FKTABLE_CAT")));
        foreignKey.setSchema(safeToString(recordMap.get("FKTABLE_SCHEM")));
        foreignKey.setTable(safeToString(recordMap.get("FKTABLE_NAME")));
        foreignKey.setName(safeToString(recordMap.get("FK_NAME")));
        foreignKey.setConstraintType(JdbcConstraintType.ForeignKey);
        foreignKey.setReferenceCatalog(safeToString(recordMap.get("PKTABLE_CAT")));
        foreignKey.setReferenceSchema(safeToString(recordMap.get("PKTABLE_SCHEM")));
        foreignKey.setReferenceTable(safeToString(recordMap.get("PKTABLE_NAME")));
        //
        foreignKey.setUpdateRule(JdbcForeignKeyRule.valueOfCode(safeToInteger(recordMap.get("UPDATE_RULE"))));
        foreignKey.setDeleteRule(JdbcForeignKeyRule.valueOfCode(safeToInteger(recordMap.get("DELETE_RULE"))));
        foreignKey.setDeferrability(JdbcDeferrability.valueOfCode(safeToInteger(recordMap.get("DEFERRABILITY"))));
        //
        String pkColumnName = safeToString(recordMap.get("PKCOLUMN_NAME"));
        String fkColumnName = safeToString(recordMap.get("FKCOLUMN_NAME"));
        foreignKey.getColumns().add(fkColumnName);
        foreignKey.getReferenceMapping().put(fkColumnName, pkColumnName);
        return foreignKey;
    }
}
