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
import net.hasor.db.metadata.domain.mysql.*;
import net.hasor.db.metadata.domain.mysql.driver.MysqlType;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MySQL 元信息获取，参考资料：
 *   <li>https://dev.mysql.com/doc/refman/8.0/en/information-schema.html</li>
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlMetadataProvider extends AbstractMetadataProvider implements MetaDataService {
    private static final String TABLE = "select TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,TABLE_COLLATION,CREATE_TIME,UPDATE_TIME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES";

    public MySqlMetadataProvider(Connection connection) {
        super(connection);
    }

    public MySqlMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    public String getVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select version()");
        }
    }

    public CaseSensitivityType getPlain() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            //https://dev.mysql.com/doc/refman/5.7/en/identifier-case-sensitivity.html
            Map<String, Object> objectMap = new JdbcTemplate(conn).queryForMap("show global variables like 'lower_case_table_names'");
            if (objectMap == null || !objectMap.containsKey("Variable_name")) {
                return super.getPlain();
            }
            //
            Integer mode = safeToInteger(objectMap.get("Variable_name"));
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
        List<MySqlColumn> columns = this.getColumns(dbName, table);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(MySqlColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    public List<MySqlSchema> getSchemas() throws SQLException {
        String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertSchema).collect(Collectors.toList());
        }
    }

    public MySqlSchema getSchema(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            String queryString = "select SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME = ?";
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(this::convertSchema).findFirst().orElse(null);
        }
    }

    public Map<String, List<MySqlTable>> getTables(String[] schemaName) throws SQLException {
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
            return mapList.stream().map(this::convertTable).collect(Collectors.groupingBy(MySqlTable::getSchema));
        }
    }

    public List<MySqlTable> getAllTables() throws SQLException {
        String currentSchema = "MYSQL";
        try (Connection conn = this.connectSupplier.eGet()) {
            currentSchema = new JdbcTemplate(conn).queryForString("select database()");
        }
        return getAllTables(currentSchema);
    }

    public List<MySqlTable> getAllTables(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return Collections.emptyList();
        }
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

    public List<MySqlTable> findTable(String schemaName, String[] tableName) throws SQLException {
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

    public MySqlTable getTable(String schemaName, String tableName) throws SQLException {
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

    public List<MySqlColumn> getColumns(String schemaName, String tableName) throws SQLException {
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
            String queryStringColumn = "select TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_OCTET_LENGTH,NUMERIC_SCALE,NUMERIC_PRECISION,DATETIME_PRECISION,CHARACTER_SET_NAME,COLLATION_NAME,COLUMN_TYPE,COLUMN_DEFAULT,COLUMN_COMMENT from INFORMATION_SCHEMA.COLUMNS " //
                    + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
            columnList = new JdbcTemplate(conn).queryForList(queryStringColumn, schemaName, tableName);
            if (columnList == null) {
                return Collections.emptyList();
            }
            String queryStringPrimaryAndUnique = "select INDEX_NAME,COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                    + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = 'PRIMARY' or INDEX_NAME in (select CONSTRAINT_NAME from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where TABLE_SCHEMA = ? and TABLE_NAME = ? and CONSTRAINT_TYPE = 'UNIQUE') order by SEQ_IN_INDEX asc";
            primaryKeyList = new JdbcTemplate(conn).queryForList(queryStringPrimaryAndUnique, schemaName, tableName, schemaName, tableName);
        }
        List<String> primaryKeyColumnNameList = primaryKeyList.stream().filter(recordMap -> {
            String indexName = safeToString(recordMap.get("INDEX_NAME"));
            return "PRIMARY".equals(indexName);
        }).map(recordMap -> {
            return safeToString(recordMap.get("COLUMN_NAME"));
        }).collect(Collectors.toList());
        List<String> uniqueKeyColumnNameList = primaryKeyList.stream().filter(recordMap -> {
            String indexName = safeToString(recordMap.get("INDEX_NAME"));
            return !"PRIMARY".equals(indexName);
        }).map(recordMap -> {
            return safeToString(recordMap.get("COLUMN_NAME"));
        }).collect(Collectors.toList());
        //
        return columnList.stream().map(recordMap -> {
            return convertColumn(recordMap, primaryKeyColumnNameList, uniqueKeyColumnNameList);
        }).collect(Collectors.toList());
    }

    public List<MySqlConstraint> getConstraint(String schemaName, String tableName) throws SQLException {
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
        String queryString = "select CONSTRAINT_SCHEMA,CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME,CONSTRAINT_TYPE from INFORMATION_SCHEMA.TABLE_CONSTRAINTS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertConstraint).collect(Collectors.toList());
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
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.PrimaryKey);
        if (constraintList == null || constraintList.isEmpty()) {
            return null;
        }
        MySqlConstraint constraintPrimaryKey = constraintList.get(0);// pk have only ones
        String pkConstraintName = constraintPrimaryKey.getName();
        String queryString = "select INDEX_SCHEMA,INDEX_NAME,COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME = ? order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName, pkConstraintName);
            if (mapList == null) {
                return null;
            }
            //
            Map<String, Optional<MySqlPrimaryKey>> pkMap = mapList.stream().map(this::convertPrimaryKey).collect(Collectors.groupingBy(o -> {
                // group by (schema + name)
                return o.getSchema() + "," + o.getName();
            }, Collectors.reducing((pk1, pk2) -> {
                // reducing group by data in to one.
                pk1.getColumns().addAll(pk2.getColumns());
                pk1.getStorageType().putAll(pk2.getStorageType());
                return pk1;
            })));
            if (pkMap.size() > 1) {
                throw new SQLException("Data error encountered multiple primary keys '" + StringUtils.join(pkMap.keySet().toArray(), "','") + "'");
            }
            //
            Optional<MySqlPrimaryKey> primaryKeyOptional = pkMap.values().stream().findFirst().orElse(Optional.empty());
            return primaryKeyOptional.orElse(null);
        }
    }

    public List<MySqlUniqueKey> getUniqueKey(String schemaName, String tableName) throws SQLException {
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
        List<MySqlConstraint> constraintList = getConstraint(schemaName, tableName, MySqlConstraintType.Unique);
        if (constraintList == null || constraintList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> queryParam = new ArrayList<>();
        queryParam.add(schemaName);
        queryParam.add(tableName);
        queryParam.addAll(constraintList.stream().map(MySqlConstraint::getName).collect(Collectors.toList()));
        String queryString = "select INDEX_NAME,COLUMN_NAME,INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS " //
                + "where NON_UNIQUE = 0 and TABLE_SCHEMA = ? and TABLE_NAME = ? and INDEX_NAME in " + buildWhereIn(constraintList) + " order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryParam.toArray());
            if (mapList == null) {
                return Collections.emptyList();
            }
            //
            return mapList.stream().map(this::convertUniqueKey).collect(Collectors.groupingBy(o -> {
                // group by (schema + name)
                return o.getSchema() + "," + o.getName();
            }, Collectors.reducing((uk1, uk2) -> {
                // reducing group by data in to one.
                uk1.getColumns().addAll(uk2.getColumns());
                uk1.getStorageType().putAll(uk2.getStorageType());
                return uk1;
            }))).values().stream().map(o -> {
                return o.orElse(null);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    public List<MySqlForeignKey> getForeignKey(String schemaName, String tableName) throws SQLException {
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
        try (Connection conn = this.connectSupplier.eGet()) {
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
                foreignKey.getColumns().add(columnName);
                foreignKey.getStorageType().put(columnName, columnIndexType);
                foreignKey.setReferenceSchema(safeToString(columnData.get("REFERENCED_TABLE_SCHEMA")));
                foreignKey.setReferenceTable(safeToString(columnData.get("REFERENCED_TABLE_NAME")));
                foreignKey.getReferenceMapping().put(columnName, refColumn);
            }
            return new ArrayList<>(fkMap.values());
        }
    }

    public List<MySqlIndex> getIndexes(String schemaName, String tableName) throws SQLException {
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
        String queryString = "select TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME,INDEX_NAME,INDEX_TYPE,NON_UNIQUE,COLUMN_NAME FROM INFORMATION_SCHEMA.STATISTICS "//
                + "where TABLE_SCHEMA = ? and TABLE_NAME = ? order by SEQ_IN_INDEX asc";
        try (Connection conn = this.connectSupplier.eGet()) {
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
                    mySqlIndex.setIndexType(indexType);
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
        if (indexTypes == null || indexTypes.length == 0) {
            return Collections.emptyList();
        }
        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return Collections.emptyList();
        }
        return indexList.stream().filter(indexItem -> {
            MySqlIndexType indexTypeForItem = indexItem.getIndexType();
            for (MySqlIndexType matchType : indexTypes) {
                if (indexTypeForItem == matchType) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public MySqlIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
        List<MySqlIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }
        return indexList.stream().filter(indexItem -> {
            return StringUtils.equals(indexItem.getName(), indexName);
        }).findFirst().orElse(null);
    }

    protected MySqlSchema convertSchema(Map<String, Object> recordMap) {
        MySqlSchema schema = new MySqlSchema();
        schema.setName(safeToString(recordMap.get("SCHEMA_NAME")));
        schema.setDefaultCharacterSetName(safeToString(recordMap.get("DEFAULT_CHARACTER_SET_NAME")));
        schema.setDefaultCollationName(safeToString(recordMap.get("DEFAULT_COLLATION_NAME")));
        return schema;
    }

    protected MySqlTable convertTable(Map<String, Object> recordMap) {
        MySqlTable table = new MySqlTable();
        table.setCatalog(safeToString(recordMap.get("TABLE_CATALOG")));
        table.setSchema(safeToString(recordMap.get("TABLE_SCHEMA")));
        table.setTable(safeToString(recordMap.get("TABLE_NAME")));
        table.setTableType(MySqlTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
        table.setCollation(safeToString(recordMap.get("TABLE_COLLATION")));
        table.setCreateTime(safeToDate(recordMap.get("CREATE_TIME")));
        table.setUpdateTime(safeToDate(recordMap.get("UPDATE_TIME")));
        table.setComment(safeToString(recordMap.get("TABLE_COMMENT")));
        return table;
    }

    protected MySqlColumn convertColumn(Map<String, Object> recordMap, List<String> primaryKeyColumnList, List<String> uniqueKeyColumnList) {
        MySqlColumn column = new MySqlColumn();
        column.setName(safeToString(recordMap.get("COLUMN_NAME")));
        column.setNullable(safeToBoolean(recordMap.get("IS_NULLABLE")));
        column.setDataType(safeToString(recordMap.get("DATA_TYPE")));
        column.setColumnType(safeToString(recordMap.get("COLUMN_TYPE")));
        column.setSqlType(safeToMySqlTypes(recordMap.get("DATA_TYPE")));
        column.setJdbcType(columnTypeMappingToJdbcType(column.getSqlType(), column.getColumnType()));
        column.setDefaultCollationName(safeToString(recordMap.get("COLLATION_NAME")));
        column.setDefaultCharacterSetName(safeToString(recordMap.get("CHARACTER_SET_NAME")));
        column.setCharactersMaxLength(safeToLong(recordMap.get("CHARACTER_MAXIMUM_LENGTH")));
        column.setBytesMaxLength(safeToInteger(recordMap.get("CHARACTER_OCTET_LENGTH")));
        column.setDatetimePrecision(safeToInteger(recordMap.get("DATETIME_PRECISION")));
        column.setNumericPrecision(safeToInteger(recordMap.get("NUMERIC_PRECISION")));
        column.setNumericScale(safeToInteger(recordMap.get("NUMERIC_SCALE")));
        column.setDefaultValue(safeToString(recordMap.get("COLUMN_DEFAULT")));
        //
        column.setPrimaryKey(primaryKeyColumnList.contains(column.getName()));
        column.setUniqueKey(uniqueKeyColumnList.contains(column.getName()));
        column.setComment(safeToString(recordMap.get("COLUMN_COMMENT")));
        return column;
    }

    protected MySqlConstraint convertConstraint(Map<String, Object> recordMap) {
        String constraintSchema = safeToString(recordMap.get("CONSTRAINT_SCHEMA"));
        String constraintName = safeToString(recordMap.get("CONSTRAINT_NAME"));
        String constraintTypeString = safeToString(recordMap.get("CONSTRAINT_TYPE"));
        MySqlConstraint constraint = new MySqlConstraint();
        constraint.setSchema(constraintSchema);
        constraint.setName(constraintName);
        constraint.setConstraintType(MySqlConstraintType.valueOfCode(constraintTypeString));
        return constraint;
    }

    protected MySqlPrimaryKey convertPrimaryKey(Map<String, Object> recordMap) {
        MySqlPrimaryKey primaryKey = new MySqlPrimaryKey();
        primaryKey.setSchema(safeToString(recordMap.get("INDEX_SCHEMA")));
        primaryKey.setName(safeToString(recordMap.get("INDEX_NAME")));
        primaryKey.setConstraintType(MySqlConstraintType.PrimaryKey);
        //
        String cName = safeToString(recordMap.get("COLUMN_NAME"));
        String cType = safeToString(recordMap.get("INDEX_TYPE"));
        primaryKey.getColumns().add(cName);
        primaryKey.getStorageType().put(cName, cType);
        return primaryKey;
    }

    protected MySqlUniqueKey convertUniqueKey(Map<String, Object> recordMap) {
        MySqlUniqueKey uniqueKey = new MySqlUniqueKey();
        uniqueKey.setName(safeToString(recordMap.get("INDEX_NAME")));
        if ("PRIMARY".equals(safeToString(recordMap.get("INDEX_TYPE")))) {
            uniqueKey.setConstraintType(MySqlConstraintType.PrimaryKey);
        } else {
            uniqueKey.setConstraintType(MySqlConstraintType.Unique);
        }
        //
        String cName = safeToString(recordMap.get("COLUMN_NAME"));
        String cType = safeToString(recordMap.get("INDEX_TYPE"));
        uniqueKey.getColumns().add(cName);
        uniqueKey.getStorageType().put(cName, cType);
        return uniqueKey;
    }

    protected JDBCType columnTypeMappingToJdbcType(SqlType sqlType, String columnType) {
        if (sqlType instanceof MySqlTypes && StringUtils.isNotBlank(columnType)) {
            MysqlType mysqlType = MysqlType.getByName(columnType);
            try {
                return JDBCType.valueOf(mysqlType.getJdbcType());
            } catch (Exception e) {
                return null;
            }
        } else if (sqlType != null) {
            return sqlType.toJDBCType();
        } else {
            return null;
        }
    }

    protected MySqlTypes safeToMySqlTypes(Object obj) {
        String dat = (obj == null) ? null : obj.toString();
        for (MySqlTypes type : MySqlTypes.values()) {
            if (type.getCodeKey().equalsIgnoreCase(dat)) {
                return type;
            }
        }
        return null;
    }
}
