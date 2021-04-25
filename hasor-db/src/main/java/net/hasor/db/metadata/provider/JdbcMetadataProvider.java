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
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.TableDef;
import net.hasor.db.metadata.domain.jdbc.*;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 高效且完整的 MySQL 元信息获取，参考资料：https://dev.mysql.com/doc/refman/8.0/en/information-schema.html
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
        try (Connection conn = this.connectSupplier.get()) {
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

    public String getCurrentSchema() throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            return conn.getSchema();
        }
    }

    public String getCurrentCatalog() throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            return conn.getCatalog();
        }
    }

    public List<String> getCatalogs() throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getCatalogs()) {
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return rs.getString("TABLE_CAT");
                }).extractData(resultSet);
            }
        }
    }

    public List<JdbcSchema> getSchemas() throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getSchemas()) {
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    JdbcSchema jdbcSchema = new JdbcSchema();
                    jdbcSchema.setSchema(rs.getString("TABLE_SCHEM"));
                    jdbcSchema.setCatalog(rs.getString("TABLE_CATALOG"));
                    return jdbcSchema;
                }).extractData(resultSet);
            }
        }
    }

    public List<JdbcSchema> getSchemas(String catalog) throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getSchemas(catalog, null)) {
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    JdbcSchema jdbcSchema = new JdbcSchema();
                    jdbcSchema.setSchema(rs.getString("TABLE_SCHEM"));
                    jdbcSchema.setCatalog(rs.getString("TABLE_CATALOG"));
                    return jdbcSchema;
                }).extractData(resultSet);
            }
        }
    }

    public List<JdbcTable> getAllTables() throws SQLException {
        String catalog = null;
        String schema = null;
        try (Connection conn = this.connectSupplier.get()) {
            catalog = conn.getCatalog();
            schema = conn.getSchema();
        }
        return this.getAllTables(catalog, schema);
    }

    public List<JdbcTable> getAllTables(String catalog, String schemaName) throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getTables(catalog, schemaName, null, null)) {
                return new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertTable(rs);
                }).extractData(resultSet);
            }
        }
    }

    public JdbcTable getTable(String catalog, String schemaName, String table) throws SQLException {
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet resultSet = metaData.getTables(catalog, schemaName, table, null)) {
                List<JdbcTable> jdbcTables = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertTable(rs);
                }).extractData(resultSet);
                if (jdbcTables.isEmpty()) {
                    return null;
                } else {
                    return jdbcTables.get(0);
                }
            }
        }
    }

    protected JdbcTable convertTable(ResultSet rs) throws SQLException {
        JdbcTable jdbcSchema = new JdbcTable();
        jdbcSchema.setTableCatalog(rs.getString("TABLE_CAT"));
        jdbcSchema.setTableSchema(rs.getString("TABLE_SCHEM"));
        jdbcSchema.setTableName(rs.getString("TABLE_NAME"));
        jdbcSchema.setTableType(JdbcTableType.valueOfCode(safeToString(rs.getString("TABLE_TYPE"))));
        jdbcSchema.setRemarks(rs.getString("REMARKS"));
        //
        jdbcSchema.setTypeCatalog(rs.getString("TYPE_CAT"));
        jdbcSchema.setTypeSchema(rs.getString("TYPE_SCHEM"));
        jdbcSchema.setTypeName(rs.getString("TYPE_NAME"));
        jdbcSchema.setSelfReferencingColName(rs.getString("SELF_REFERENCING_COL_NAME"));
        jdbcSchema.setRefGeneration(rs.getString("REF_GENERATION"));
        return jdbcSchema;
    }

    protected JdbcColumn convertColumn(ResultSet rs, JdbcPrimaryKey primaryKey) throws SQLException {
        JdbcColumn jdbcColumn = new JdbcColumn();
        jdbcColumn.setTableCatalog(rs.getString("TABLE_CAT"));
        jdbcColumn.setTableSchema(rs.getString("TABLE_SCHEM"));
        jdbcColumn.setTableName(rs.getString("TABLE_NAME"));
        jdbcColumn.setColumnName(rs.getString("COLUMN_NAME"));
        //
        String isNullable = rs.getString("IS_NULLABLE");
        if ("YES".equals(isNullable)) {
            jdbcColumn.setNullable(true);
        } else if ("NO".equals(isNullable)) {
            jdbcColumn.setNullable(false);
        } else {
            jdbcColumn.setNullable(null);
        }
        jdbcColumn.setNullableType(JdbcNullableType.valueOfCode(rs.getInt("NULLABLE")));
        //
        jdbcColumn.setJdbcType(JDBCType.valueOf(rs.getInt("DATA_TYPE")));
        jdbcColumn.setColumnSize(rs.getInt("COLUMN_SIZE"));
        jdbcColumn.setComment(rs.getString("REMARKS"));
        jdbcColumn.setScopeCatalog(rs.getString("SCOPE_CATALOG"));
        jdbcColumn.setScopeSchema(rs.getString("SCOPE_SCHEMA"));
        jdbcColumn.setScopeTable(rs.getString("SCOPE_TABLE"));
        //
        String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
        if ("YES".equals(isAutoincrement)) {
            jdbcColumn.setAutoincrement(true);
        } else if ("NO".equals(isAutoincrement)) {
            jdbcColumn.setAutoincrement(false);
        } else {
            jdbcColumn.setAutoincrement(null);
        }
        String isGeneratedColumn = rs.getString("IS_GENERATEDCOLUMN");
        if ("YES".equals(isGeneratedColumn)) {
            jdbcColumn.setGeneratedColumn(true);
        } else if ("NO".equals(isGeneratedColumn)) {
            jdbcColumn.setGeneratedColumn(false);
        } else {
            jdbcColumn.setGeneratedColumn(null);
        }
        //
        jdbcColumn.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
        jdbcColumn.setNumberPrecRadix(rs.getInt("NUM_PREC_RADIX"));
        jdbcColumn.setColumnDef(rs.getString("COLUMN_DEF"));
        jdbcColumn.setCharOctetLength(rs.getInt("CHAR_OCTET_LENGTH"));
        jdbcColumn.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
        jdbcColumn.setSourceDataType(rs.getShort("SOURCE_DATA_TYPE"));
        //
        if (primaryKey != null) {
            List<String> pkColumns = primaryKey.getColumns();
            if (pkColumns.contains(jdbcColumn.getColumnName())) {
                jdbcColumn.setPrimaryKey(true);
            }
        }
        return jdbcColumn;
    }

    public List<JdbcColumn> getColumns(String catalog, String schema, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return Collections.emptyList();
        }
        JdbcPrimaryKey primaryKey = getPrimaryKey(catalog, schema, table);
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            //
            List<JdbcColumn> jdbcColumns = null;
            try (ResultSet resultSet = metaData.getColumns(catalog, schema, table, null)) {
                jdbcColumns = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
                    return convertColumn(rs, primaryKey);
                }).extractData(resultSet);
            }
            if (jdbcColumns == null) {
                return Collections.emptyList();
            } else {
                return jdbcColumns;
            }
        }
    }
    //    public List<JdbcConstraint> getConstraint(String catalog, String schemaName, String table) throws SQLException {
    //        try (Connection conn = this.connectSupplier.get()) {
    //            DatabaseMetaData metaData = conn.getMetaData();
    //            try (ResultSet resultSet = metaData.getCrossReference(catalog, schemaName, table, null)) {
    //                List<JdbcTable> jdbcTables = new RowMapperResultSetExtractor<>((rs, rowNum) -> {
    //                    return convertTable(rs);
    //                }).extractData(resultSet);
    //                if (jdbcTables.isEmpty()) {
    //                    return null;
    //                } else {
    //                    return jdbcTables.get(0);
    //                }
    //            }
    //        }
    //    }

    //
    //    public List<MySqlConstraint> getConstraint(String schemaName, String tableName, MySqlConstraintType... cType) throws SQLException {
    //        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName);
    //        if (constraintList == null || constraintList.isEmpty()) {
    //            return constraintList;
    //        }
    //        return constraintList.stream().filter(mySqlConstraint -> {
    //            for (MySqlConstraintType constraintType : cType) {
    //                if (constraintType == mySqlConstraint.getConstraintType()) {
    //                    return true;
    //                }
    //            }
    //            return false;
    //        }).collect(Collectors.toList());
    //    }
    //
    public JdbcPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, schema, table)) {
                List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(primaryKeys);
                if (mapList != null && !mapList.isEmpty()) {
                    JdbcPrimaryKey primaryKey = new JdbcPrimaryKey();
                    primaryKey.setName("PRIMARY");
                    primaryKey.setConstraintType(JdbcConstraintType.PrimaryKey);
                    //
                    mapList.sort((o1, o2) -> {
                        Integer o1KeySeq = safeToInteger(o1.get("KEY_SEQ"));
                        Integer o2KeySeq = safeToInteger(o2.get("KEY_SEQ"));
                        if (o1KeySeq != null && o2KeySeq != null) {
                            return Integer.compare(o1KeySeq, o2KeySeq);
                        } else {
                            return 0;
                        }
                    });
                    for (Map<String, Object> recordMap : mapList) {
                        primaryKey.setCatalog(safeToString(recordMap.get("TABLE_CAT")));
                        primaryKey.setSchema(safeToString(recordMap.get("TABLE_SCHEM")));
                        primaryKey.setTable(safeToString(recordMap.get("TABLE_NAME")));
                        primaryKey.getColumns().add(safeToString(recordMap.get("COLUMN_NAME")));
                    }
                    return primaryKey;
                }
            }
        }
        return null;
    }
 
    //
    //    public List<MySqlForeignKey> getForeignKey(String schemaName, String tableName) throws SQLException {
    //        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
    //            return Collections.emptyList();
    //        }
    //        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.ForeignKey);
    //        if (constraintList == null || constraintList.isEmpty()) {
    //            return Collections.emptyList();
    //        }
    //        Set<String> constraintSchemaList = constraintList.stream().map(MySqlConstraint::getSchema).collect(Collectors.toCollection(HashSet::new));
    //        String constraintSchemaWhereIn = buildWhereIn(constraintSchemaList);
    //        Set<String> constraintNameList = constraintList.stream().map(MySqlConstraint::getName).collect(Collectors.toCollection(HashSet::new));
    //        String constraintNameWhereIn = buildWhereIn(constraintNameList);
    //        String queryFkAttrs = "select CONSTRAINT_SCHEMA,CONSTRAINT_NAME,TABLE_NAME,UPDATE_RULE,DELETE_RULE,REFERENCED_TABLE_NAME from INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS " //
    //                + "where CONSTRAINT_SCHEMA in " + constraintSchemaWhereIn + " and CONSTRAINT_NAME in " + constraintNameWhereIn + " and TABLE_NAME = ?";
    //        ArrayList<String> queryAttrsParam = new ArrayList<>();
    //        queryAttrsParam.addAll(constraintSchemaList);
    //        queryAttrsParam.addAll(constraintNameList);
    //        queryAttrsParam.add(tableName);
    //        //
    //        try (Connection conn = this.connectSupplier.get()) {
    //            Map<String, MySqlForeignKey> fkMap = new LinkedHashMap<>();
    //            List<Map<String, Object>> fkAttrsMapList = new JdbcTemplate(conn).queryForList(queryFkAttrs, queryAttrsParam.toArray());
    //            if (fkAttrsMapList == null || fkAttrsMapList.isEmpty()) {
    //                return Collections.emptyList();
    //            }
    //            for (Map<String, Object> fkAttr : fkAttrsMapList) {
    //                String fkSchema = safeToString(fkAttr.get("CONSTRAINT_SCHEMA"));
    //                String fkName = safeToString(fkAttr.get("CONSTRAINT_NAME"));
    //                String fkKey = fkSchema + "." + fkName;
    //                MySqlForeignKey foreignKey = fkMap.computeIfAbsent(fkKey, k -> {
    //                    MySqlForeignKey sqlForeignKey = new MySqlForeignKey();
    //                    sqlForeignKey.setSchema(fkSchema);
    //                    sqlForeignKey.setName(fkName);
    //                    sqlForeignKey.setConstraintType(MySqlConstraintType.ForeignKey);
    //                    return sqlForeignKey;
    //                });
    //                foreignKey.setReferenceTable(safeToString(fkAttr.get("REFERENCED_TABLE_NAME")));
    //                foreignKey.setDeleteRule(MySqlForeignKeyRule.valueOfCode(safeToString(fkAttr.get("DELETE_RULE"))));
    //                foreignKey.setUpdateRule(MySqlForeignKeyRule.valueOfCode(safeToString(fkAttr.get("UPDATE_RULE"))));
    //            }
    //            // all fk columns and group by fk name
    //            // where c.TABLE_SCHEMA = 'devtester' and c.TABLE_NAME = 'proc_table_ref'
    //            String queryFkColumns = "select c.CONSTRAINT_SCHEMA,c.CONSTRAINT_NAME,c.COLUMN_NAME,c.REFERENCED_TABLE_SCHEMA,c.REFERENCED_TABLE_NAME,c.REFERENCED_COLUMN_NAME,s.INDEX_TYPE" //
    //                    + " from INFORMATION_SCHEMA.KEY_COLUMN_USAGE c left join INFORMATION_SCHEMA.STATISTICS s on s.TABLE_SCHEMA = c.TABLE_SCHEMA and s.TABLE_NAME = c.TABLE_NAME and s.INDEX_NAME = c.CONSTRAINT_NAME and s.COLUMN_NAME = c.COLUMN_NAME"//
    //                    + " where c.CONSTRAINT_SCHEMA in " + constraintSchemaWhereIn + " and c.CONSTRAINT_NAME in " + constraintNameWhereIn + " and c.TABLE_SCHEMA = ? and c.TABLE_NAME = ? order by c.POSITION_IN_UNIQUE_CONSTRAINT asc";
    //            ArrayList<String> queryFkColumnsParam = new ArrayList<>();
    //            queryFkColumnsParam.addAll(constraintSchemaList);
    //            queryFkColumnsParam.addAll(constraintNameList);
    //            queryFkColumnsParam.add(schemaName);
    //            queryFkColumnsParam.add(tableName);
    //            //
    //            List<Map<String, Object>> queryFkColumnList = new JdbcTemplate(conn).queryForList(queryFkColumns, queryFkColumnsParam.toArray());
    //            if (queryFkColumnList == null || queryFkColumnList.size() < fkAttrsMapList.size()) {
    //                throw new IllegalArgumentException("query fk result data error.");
    //            }
    //            for (Map<String, Object> columnData : queryFkColumnList) {
    //                String fkSchema = safeToString(columnData.get("CONSTRAINT_SCHEMA"));
    //                String fkName = safeToString(columnData.get("CONSTRAINT_NAME"));
    //                String fkKey = fkSchema + "." + fkName + "";
    //                MySqlForeignKey foreignKey = fkMap.get(fkKey);
    //                String columnName = safeToString(columnData.get("COLUMN_NAME"));
    //                String columnIndexType = safeToString(columnData.get("INDEX_TYPE"));
    //                String refColumn = safeToString(columnData.get("REFERENCED_COLUMN_NAME"));
    //                foreignKey.getFkColumn().add(columnName);
    //                foreignKey.getStorageType().put(columnName, columnIndexType);
    //                foreignKey.setReferenceSchema(safeToString(columnData.get("REFERENCED_TABLE_SCHEMA")));
    //                foreignKey.setReferenceTable(safeToString(columnData.get("REFERENCED_TABLE_NAME")));
    //                foreignKey.getReferenceMapping().put(columnName, refColumn);
    //            }
    //            return new ArrayList<>(fkMap.values());
    //        }
    //    }
    //
    //    public List<MySqlIndex> getIndexes(String schemaName, String tableName) throws SQLException {
    //        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
    //            return Collections.emptyList();
    //        }
    //        String queryString = "select TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME,INDEX_NAME,INDEX_TYPE,NON_UNIQUE,COLUMN_NAME FROM INFORMATION_SCHEMA.STATISTICS "//
    //                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? order by SEQ_IN_INDEX asc";
    //        try (Connection conn = this.connectSupplier.get()) {
    //            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
    //            if (mapList == null) {
    //                return Collections.emptyList();
    //            }
    //            List<MySqlConstraint> constraints = getConstraint(schemaName, tableName);
    //            Map<String, MySqlConstraint> constraintMap = constraints.stream().collect(Collectors.toMap(MySqlConstraint::getName, constraint -> constraint));
    //            Map<String, MySqlIndex> groupByName = new LinkedHashMap<>(); // indexName -> MySqlIndex
    //            // group by table
    //            for (Map<String, Object> indexColumn : mapList) {
    //                String indexName = safeToString(indexColumn.get("INDEX_NAME"));
    //                MySqlIndex indexMap = groupByName.computeIfAbsent(indexName, k -> {
    //                    MySqlIndexType indexType = null;
    //                    if (constraintMap.containsKey(indexName)) {
    //                        switch (constraintMap.get(indexName).getConstraintType()) {
    //                            case PrimaryKey:
    //                                indexType = MySqlIndexType.Primary;
    //                                break;
    //                            case Unique:
    //                                indexType = MySqlIndexType.Unique;
    //                                break;
    //                            case ForeignKey:
    //                                indexType = MySqlIndexType.Foreign;
    //                                break;
    //                        }
    //                    } else {
    //                        indexType = MySqlIndexType.Normal;
    //                    }
    //                    MySqlIndex mySqlIndex = new MySqlIndex();
    //                    mySqlIndex.setName(k);
    //                    mySqlIndex.setIndexEnum(indexType);
    //                    return mySqlIndex;
    //                });
    //                String columnName = safeToString(indexColumn.get("COLUMN_NAME"));
    //                String indexType = safeToString(indexColumn.get("INDEX_TYPE"));
    //                indexMap.getColumns().add(columnName);
    //                indexMap.getStorageType().put(columnName, indexType);
    //            }
    //            return new ArrayList<>(groupByName.values());
    //        }
    //    }
    //
    //    public List<MySqlIndex> getIndexes(String schemaName, String tableName, MySqlIndexType... indexTypes) throws SQLException {
    //        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
    //            return Collections.emptyList();
    //        }
    //        if (indexTypes == null || indexTypes.length == 0) {
    //            return Collections.emptyList();
    //        }
    //        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
    //        if (indexList == null || indexList.isEmpty()) {
    //            return Collections.emptyList();
    //        }
    //        return indexList.stream().filter(indexItem -> {
    //            MySqlIndexType indexTypeForItem = indexItem.getIndexEnum();
    //            for (MySqlIndexType matchType : indexTypes) {
    //                if (indexTypeForItem == matchType) {
    //                    return true;
    //                }
    //            }
    //            return false;
    //        }).collect(Collectors.toList());
    //    }
    //
    //    public MySqlIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
    //        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)) {
    //            return null;
    //        }
    //        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
    //        if (indexList == null || indexList.isEmpty()) {
    //            return null;
    //        }
    //        return indexList.stream().filter(indexItem -> {
    //            return StringUtils.equals(indexItem.getName(), indexName);
    //        }).findFirst().orElse(null);
    //    }
    //
    //
    @Override
    public Map<String, ColumnDef> getColumnMap(String schemaName, String tableName) throws SQLException {
        List<JdbcColumn> columns = this.getColumns(null, schemaName, tableName);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(JdbcColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public TableDef searchTable(String schemaName, String tableName) throws SQLException {
        return getTable(null, schemaName, tableName);
    }
}
