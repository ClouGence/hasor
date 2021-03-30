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
import net.hasor.db.metadata.mysql.*;
import net.hasor.db.metadata.mysql.driver.MysqlType;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static net.hasor.db.metadata.SqlUtils.*;

/**
 * 高效且完整的 MySQL 元信息获取，参考资料：https://dev.mysql.com/doc/refman/8.0/en/information-schema.html
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlMetadataSupplier extends AbstractMetadataSupplier {
    public MySqlMetadataSupplier(Connection connection) {
        super(connection);
    }

    public MySqlMetadataSupplier(DataSource dataSource) {
        super(dataSource);
    }

    public List<MySqlVariable> getVariables(MySqlVariableScope scope) throws SQLException {
        String queryString = null;
        switch (scope) {
            case Global:
                queryString = "show global variables";
                break;
            case Session:
                queryString = "show session variables";
                break;
            case Default:
                queryString = "show variables";
                break;
            default:
                throw new IllegalArgumentException("arg scope error.");
        }
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(mysqlVar -> {
                MySqlVariable variable = new MySqlVariable();
                variable.setName(safeToString(mysqlVar.get("Variable_name")));
                variable.setValue(safeToString(mysqlVar.get("Value")));
                variable.setScope(scope);
                return variable;
            }).collect(Collectors.toList());
        }
    }

    public MySqlVariable getVariable(MySqlVariableScope scope, String varName) throws SQLException {
        if (StringUtils.isBlank(varName)) {
            return null;
        }
        varName = "%" + varName + "%";
        String queryString = null;
        switch (scope) {
            case Global: {
                queryString = "show global variables like ?";
                break;
            }
            case Session: {
                queryString = "show session variables like ?";
                break;
            }
            case Default:
                queryString = "show variables like ?";
                break;
            default:
                throw new IllegalArgumentException("arg scope error.");
        }
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, varName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(mysqlVar -> {
                MySqlVariable variable = new MySqlVariable();
                variable.setName(safeToString(mysqlVar.get("Variable_name")));
                variable.setValue(safeToString(mysqlVar.get("Value")));
                variable.setScope(scope);
                return variable;
            }).findFirst().orElse(null);
        }
    }

    public List<MySqlSchema> getSchemas() throws SQLException {
        String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(mysqlSchema -> {
                MySqlSchema schema = new MySqlSchema();
                schema.setName(safeToString(mysqlSchema.get("SCHEMA_NAME")));
                schema.setDefaultCharacterSetName(safeToString(mysqlSchema.get("DEFAULT_CHARACTER_SET_NAME")));
                schema.setDefaultCollationName(safeToString(mysqlSchema.get("DEFAULT_COLLATION_NAME")));
                return schema;
            }).collect(Collectors.toList());
        }
    }

    public MySqlSchema getSchema(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.get()) {
            String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME = ?";
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(mysqlSchema -> {
                MySqlSchema schema = new MySqlSchema();
                schema.setName(safeToString(mysqlSchema.get("SCHEMA_NAME")));
                schema.setDefaultCharacterSetName(safeToString(mysqlSchema.get("DEFAULT_CHARACTER_SET_NAME")));
                schema.setDefaultCollationName(safeToString(mysqlSchema.get("DEFAULT_COLLATION_NAME")));
                return schema;
            }).findFirst().orElse(null);
        }
    }

    public Map<String, List<MySqlTable>> getTables(String... schemaName) throws SQLException {
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
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA in " + buildWhereIn(schemaList);
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaList.toArray());
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<MySqlTable>> resultData = new HashMap<>();
            mapList.forEach(mysqlTable -> {
                String dbName = safeToString(mysqlTable.get("TABLE_SCHEMA"));
                List<MySqlTable> tableList = resultData.computeIfAbsent(dbName, k -> new ArrayList<>());
                MySqlTable table = new MySqlTable();
                table.setTableName(safeToString(mysqlTable.get("TABLE_NAME")));
                table.setTableType(MySqlTableType.valueOfCode(safeToString(mysqlTable.get("TABLE_TYPE"))));
                table.setCollation(safeToString(mysqlTable.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(mysqlTable.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(mysqlTable.get("UPDATE_TIME")));
                tableList.add(table);
            });
            return resultData;
        }
    }

    public Map<String, List<MySqlTable>> findTables(String schemaName, String... tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return Collections.emptyMap();
        }
        tableName = (tableName == null) ? new String[0] : tableName;
        ArrayList<String> tableNameList = new ArrayList<>();
        for (String table : tableName) {
            if (StringUtils.isNotBlank(table)) {
                tableNameList.add(table);
            }
        }
        if (tableNameList.isEmpty()) {
            return Collections.emptyMap();
        }
        if (tableNameList.size() > 1000) {
            throw new IndexOutOfBoundsException("Batch query table Batch size out of 1000");
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME in " + buildWhereIn(tableNameList);
        tableNameList.add(0, schemaName);
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, tableNameList.toArray());
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<MySqlTable>> resultData = new HashMap<>();
            mapList.forEach(mysqlTable -> {
                String dbName = safeToString(mysqlTable.get("TABLE_SCHEMA"));
                List<MySqlTable> tableList = resultData.computeIfAbsent(dbName, k -> new ArrayList<>());
                MySqlTable table = new MySqlTable();
                table.setTableName(safeToString(mysqlTable.get("TABLE_NAME")));
                table.setTableType(MySqlTableType.valueOfCode(safeToString(mysqlTable.get("TABLE_TYPE"))));
                table.setCollation(safeToString(mysqlTable.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(mysqlTable.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(mysqlTable.get("UPDATE_TIME")));
                tableList.add(table);
            });
            return resultData;
        }
    }

    public MySqlTable getTable(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return null;
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME from INFORMATION_SCHEMA.TABLES " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(mysqlTable -> {
                MySqlTable table = new MySqlTable();
                table.setTableName(safeToString(mysqlTable.get("TABLE_NAME")));
                table.setTableType(MySqlTableType.valueOfCode(safeToString(mysqlTable.get("TABLE_TYPE"))));
                table.setCollation(safeToString(mysqlTable.get("TABLE_COLLATION")));
                table.setCreateTime(safeToDate(mysqlTable.get("CREATE_TIME")));
                table.setUpdateTime(safeToDate(mysqlTable.get("UPDATE_TIME")));
                return table;
            }).findFirst().orElse(null);
        }
    }

    public List<MySqlColumn> getColumns(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        String queryString = "select TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_OCTET_LENGTH,NUMERIC_SCALE,NUMERIC_PRECISION,DATETIME_PRECISION,CHARACTER_SET_NAME,COLLATION_NAME,COLUMN_TYPE from INFORMATION_SCHEMA.COLUMNS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(mysqlVar -> {
                MySqlColumn column = new MySqlColumn();
                column.setName(safeToString(mysqlVar.get("COLUMN_NAME")));
                column.setNullable(safeToBoolean(mysqlVar.get("IS_NULLABLE")));
                column.setDataType(safeToString(mysqlVar.get("DATA_TYPE")));
                column.setColumnType(safeToString(mysqlVar.get("COLUMN_TYPE")));
                column.setSqlType(safeToMySqlTypes(mysqlVar.get("DATA_TYPE")));
                column.setJdbcType(columnTypeMappingToJdbcType(column.getColumnType()));
                column.setDefaultCollationName(safeToString(mysqlVar.get("COLLATION_NAME")));
                column.setDefaultCharacterSetName(safeToString(mysqlVar.get("CHARACTER_SET_NAME")));
                column.setCharactersMaxLength(safeToLong(mysqlVar.get("CHARACTER_MAXIMUM_LENGTH")));
                column.setBytesMaxLength(safeToInteger(mysqlVar.get("CHARACTER_OCTET_LENGTH")));
                column.setDatetimePrecision(safeToInteger(mysqlVar.get("DATETIME_PRECISION")));
                column.setNumericPrecision(safeToInteger(mysqlVar.get("NUMERIC_PRECISION")));
                column.setNumericScale(safeToInteger(mysqlVar.get("NUMERIC_SCALE")));
                return column;
            }).collect(Collectors.toList());
        }
    }

    public List<MySqlConstraint> getConstraint(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        String queryString = "select CONSTRAINT_SCHEMA,CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME,CONSTRAINT_TYPE from INFORMATION_SCHEMA.TABLE_CONSTRAINTS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(entry -> {
                String constraintSchema = safeToString(entry.get("CONSTRAINT_SCHEMA"));
                String constraintName = safeToString(entry.get("CONSTRAINT_NAME"));
                String constraintTypeString = safeToString(entry.get("CONSTRAINT_TYPE"));
                MySqlConstraint constraint = new MySqlConstraint();
                constraint.setSchema(constraintSchema);
                constraint.setName(constraintName);
                constraint.setConstraintType(MySqlConstraintType.valueOfCode(constraintTypeString));
                return constraint;
            }).collect(Collectors.toList());
        }
    }

    public List<MySqlConstraint> getConstraint(String schemaName, String tableName, MySqlConstraintType... cType) throws SQLException {
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName);
        if (constraintList == null || constraintList.isEmpty()) {
            return constraintList;
        }
        return constraintList.stream().filter(mySqlConstraint -> {
            for (MySqlConstraintType constraintType : cType) {
                if (constraintType == mySqlConstraint.getConstraintType()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public MySqlPrimaryKey getPrimaryKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return null;
        }
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.PrimaryKey);
        if (constraintList == null || constraintList.isEmpty()) {
            return null;
        }
        MySqlConstraint constraintPrimaryKey = constraintList.get(0);// pk have only ones
        String pkConstraintName = constraintPrimaryKey.getName();
        String queryString = "select COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = ? order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName, pkConstraintName);
            if (mapList == null) {
                return null;
            }
            MySqlPrimaryKey primaryKey = new MySqlPrimaryKey();
            primaryKey.setName("PRIMARY");
            primaryKey.setConstraintType(MySqlConstraintType.PrimaryKey);
            for (Map<String, Object> ent : mapList) {
                String cName = safeToString(ent.get("COLUMN_NAME"));
                String cType = safeToString(ent.get("INDEX_TYPE"));
                primaryKey.getColumns().add(cName);
                primaryKey.getStorageType().put(cName, cType);
            }
            return primaryKey;
        }
    }

    public List<MySqlUniqueKey> getUniqueKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.Unique);
        if (constraintList == null || constraintList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> queryParam = new ArrayList<>();
        queryParam.add(schemaName);
        queryParam.add(tableName);
        queryParam.addAll(constraintList.stream().map(MySqlConstraint::getName).collect(Collectors.toList()));
        String queryString = "select INDEX_NAME,COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME in " + buildWhereIn(constraintList) + " order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryParam.toArray());
            if (mapList == null) {
                return Collections.emptyList();
            }
            Map<String, MySqlUniqueKey> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> indexColumn : mapList) {
                String indexName = safeToString(indexColumn.get("INDEX_NAME"));
                String indexType = safeToString(indexColumn.get("INDEX_TYPE"));
                MySqlUniqueKey uniqueKey = groupByName.computeIfAbsent(indexName, k -> {
                    MySqlUniqueKey sqlUniqueKey = new MySqlUniqueKey();
                    sqlUniqueKey.setName(k);
                    sqlUniqueKey.setConstraintType(MySqlConstraintType.Unique);
                    return sqlUniqueKey;
                });
                String columnName = safeToString(indexColumn.get("COLUMN_NAME"));
                uniqueKey.getColumns().add(columnName);
                uniqueKey.getStorageType().put(columnName, indexType);
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<MySqlForeignKey> getForeignKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.ForeignKey);
        if (constraintList == null || constraintList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> constraintSchemaList = constraintList.stream().map(MySqlConstraint::getSchema).collect(Collectors.toCollection(HashSet::new));
        String constraintSchemaWhereIn = buildWhereIn(constraintSchemaList);
        Set<String> constraintNameList = constraintList.stream().map(MySqlConstraint::getName).collect(Collectors.toCollection(HashSet::new));
        String constraintNameWhereIn = buildWhereIn(constraintNameList);
        String queryFkAttrs = "select CONSTRAINT_SCHEMA,CONSTRAINT_NAME,TABLE_NAME,UPDATE_RULE,DELETE_RULE,REFERENCED_TABLE_NAME from INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS " //
                + "where CONSTRAINT_SCHEMA in " + constraintSchemaWhereIn + " and CONSTRAINT_NAME in " + constraintNameWhereIn + " and TABLE_NAME = ?";
        ArrayList<String> queryAttrsParam = new ArrayList<>();
        queryAttrsParam.addAll(constraintSchemaList);
        queryAttrsParam.addAll(constraintNameList);
        queryAttrsParam.add(tableName);
        //
        try (Connection conn = this.connectSupplier.get()) {
            Map<String, MySqlForeignKey> fkMap = new LinkedHashMap<>();
            List<Map<String, Object>> fkAttrsMapList = new JdbcTemplate(conn).queryForList(queryFkAttrs, queryAttrsParam.toArray());
            if (fkAttrsMapList == null || fkAttrsMapList.isEmpty()) {
                return Collections.emptyList();
            }
            for (Map<String, Object> fkAttr : fkAttrsMapList) {
                String fkSchema = safeToString(fkAttr.get("CONSTRAINT_SCHEMA"));
                String fkName = safeToString(fkAttr.get("CONSTRAINT_NAME"));
                String fkKey = fkSchema + "." + fkName;
                MySqlForeignKey foreignKey = fkMap.computeIfAbsent(fkKey, k -> {
                    MySqlForeignKey sqlForeignKey = new MySqlForeignKey();
                    sqlForeignKey.setSchema(fkSchema);
                    sqlForeignKey.setName(fkName);
                    sqlForeignKey.setConstraintType(MySqlConstraintType.ForeignKey);
                    return sqlForeignKey;
                });
                foreignKey.setReferenceTable(safeToString(fkAttr.get("REFERENCED_TABLE_NAME")));
                foreignKey.setDeleteRule(MySqlForeignKeyRule.valueOfCode(safeToString(fkAttr.get("DELETE_RULE"))));
                foreignKey.setUpdateRule(MySqlForeignKeyRule.valueOfCode(safeToString(fkAttr.get("UPDATE_RULE"))));
            }
            // all fk columns and group by fk name
            // where c.TABLE_SCHEMA = 'devtester' and c.TABLE_NAME = 'proc_table_ref'
            String queryFkColumns = "select c.CONSTRAINT_SCHEMA,c.CONSTRAINT_NAME,c.COLUMN_NAME,c.REFERENCED_TABLE_SCHEMA,c.REFERENCED_TABLE_NAME,c.REFERENCED_COLUMN_NAME,s.INDEX_TYPE" //
                    + " from INFORMATION_SCHEMA.KEY_COLUMN_USAGE c left join INFORMATION_SCHEMA.STATISTICS s on s.TABLE_SCHEMA = c.TABLE_SCHEMA and s.TABLE_NAME = c.TABLE_NAME and s.INDEX_NAME = c.CONSTRAINT_NAME and s.COLUMN_NAME = c.COLUMN_NAME"//
                    + " where c.CONSTRAINT_SCHEMA in " + constraintSchemaWhereIn + " and c.CONSTRAINT_NAME in " + constraintNameWhereIn + " and c.TABLE_SCHEMA = ? and c.TABLE_NAME = ? order by c.POSITION_IN_UNIQUE_CONSTRAINT asc";
            ArrayList<String> queryFkColumnsParam = new ArrayList<>();
            queryFkColumnsParam.addAll(constraintSchemaList);
            queryFkColumnsParam.addAll(constraintNameList);
            queryFkColumnsParam.add(schemaName);
            queryFkColumnsParam.add(tableName);
            //
            List<Map<String, Object>> queryFkColumnList = new JdbcTemplate(conn).queryForList(queryFkColumns, queryFkColumnsParam.toArray());
            if (queryFkColumnList == null || queryFkColumnList.size() < fkAttrsMapList.size()) {
                throw new IllegalArgumentException("query fk result data error.");
            }
            for (Map<String, Object> columnData : queryFkColumnList) {
                String fkSchema = safeToString(columnData.get("CONSTRAINT_SCHEMA"));
                String fkName = safeToString(columnData.get("CONSTRAINT_NAME"));
                String fkKey = fkSchema + "." + fkName + "";
                MySqlForeignKey foreignKey = fkMap.get(fkKey);
                String columnName = safeToString(columnData.get("COLUMN_NAME"));
                String columnIndexType = safeToString(columnData.get("INDEX_TYPE"));
                String refColumn = safeToString(columnData.get("REFERENCED_COLUMN_NAME"));
                foreignKey.getFkColumn().add(columnName);
                foreignKey.getStorageType().put(columnName, columnIndexType);
                foreignKey.setReferenceSchema(safeToString(columnData.get("REFERENCED_TABLE_SCHEMA")));
                foreignKey.setReferenceTable(safeToString(columnData.get("REFERENCED_TABLE_NAME")));
                foreignKey.getReferenceMapping().put(columnName, refColumn);
            }
            return new ArrayList<>(fkMap.values());
        }
    }

    public List<MySqlIndex> getIndexes(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        String queryString = "select TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME,INDEX_NAME,INDEX_TYPE,NON_UNIQUE,COLUMN_NAME FROM INFORMATION_SCHEMA.STATISTICS "//
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            List<MySqlConstraint> constraints = getConstraint(schemaName, tableName);
            Map<String, MySqlConstraint> constraintMap = constraints.stream().collect(Collectors.toMap(MySqlConstraint::getName, constraint -> constraint));
            Map<String, MySqlIndex> groupByName = new LinkedHashMap<>(); // indexName -> MySqlIndex
            // group by table
            for (Map<String, Object> indexColumn : mapList) {
                String indexName = safeToString(indexColumn.get("INDEX_NAME"));
                MySqlIndex indexMap = groupByName.computeIfAbsent(indexName, k -> {
                    MySqlIndexType indexType = null;
                    if (constraintMap.containsKey(indexName)) {
                        switch (constraintMap.get(indexName).getConstraintType()) {
                            case PrimaryKey:
                                indexType = MySqlIndexType.Primary;
                                break;
                            case Unique:
                                indexType = MySqlIndexType.Unique;
                                break;
                            case ForeignKey:
                                indexType = MySqlIndexType.Foreign;
                                break;
                        }
                    } else {
                        indexType = MySqlIndexType.Normal;
                    }
                    MySqlIndex mySqlIndex = new MySqlIndex();
                    mySqlIndex.setName(k);
                    mySqlIndex.setIndexEnum(indexType);
                    return mySqlIndex;
                });
                String columnName = safeToString(indexColumn.get("COLUMN_NAME"));
                String indexType = safeToString(indexColumn.get("INDEX_TYPE"));
                indexMap.getColumns().add(columnName);
                indexMap.getStorageType().put(columnName, indexType);
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<MySqlIndex> getIndexes(String schemaName, String tableName, MySqlIndexType... indexTypes) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (indexTypes == null || indexTypes.length == 0) {
            return Collections.emptyList();
        }
        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return Collections.emptyList();
        }
        return indexList.stream().filter(indexItem -> {
            MySqlIndexType indexTypeForItem = indexItem.getIndexEnum();
            for (MySqlIndexType matchType : indexTypes) {
                if (indexTypeForItem == matchType) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public MySqlIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)) {
            return null;
        }
        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }
        return indexList.stream().filter(indexItem -> {
            return StringUtils.equals(indexItem.getName(), indexName);
        }).findFirst().orElse(null);
    }

    protected static JDBCType columnTypeMappingToJdbcType(String columnType) {
        MysqlType mysqlType = MysqlType.getByName(columnType);
        return JDBCType.valueOf(mysqlType.getJdbcType());
    }
}
