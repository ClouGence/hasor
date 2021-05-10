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
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.SqlType;
import net.hasor.db.metadata.TableDef;
import net.hasor.db.metadata.domain.oracle.*;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static net.hasor.db.metadata.domain.oracle.OracleSqlTypes.TIMESTAMP;

/**
 * Oracle 元信息获取，参考资料：
 *   <li>https://docs.oracle.com/en/database/oracle/oracle-database/21/drdag/all_synonyms-drda-gateway.html#GUID-E814A6AC-5E00-4DB6-8170-DC147F7879F8</li>
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleMetadataProvider extends AbstractMetadataProvider implements MetaDataService {
    private static final String SCHEMA  = "select USERNAME,ACCOUNT_STATUS,LOCK_DATE,EXPIRY_DATE,DEFAULT_TABLESPACE,TEMPORARY_TABLESPACE,CREATED,PROFILE,AUTHENTICATION_TYPE,LAST_LOGIN from SYS.DBA_USERS";
    private static final String TABLE   = ""//
            + "select TAB.OWNER,TAB.TABLE_NAME,TABLESPACE_NAME,READ_ONLY,TAB.TABLE_TYPE,LOG_TABLE,COMMENTS from (\n"//
            + "  select OWNER,TABLE_NAME,TABLESPACE_NAME,READ_ONLY,'TABLE' TABLE_TYPE,LOG_TABLE from SYS.DBA_TABLES\n"//
            + "  left join SYS.DBA_MVIEW_LOGS on LOG_OWNER = OWNER and MASTER = TABLE_NAME\n"//
            + "  union all\n"//
            + "  select OWNER,VIEW_NAME, null TABLESPACE_NAME,READ_ONLY,'VIEW' TABLE_TYPE,null LOG_TABLE from SYS.DBA_VIEWS\n"//
            + ") TAB\n"//
            + "left join DBA_TAB_COMMENTS on TAB.OWNER = DBA_TAB_COMMENTS.OWNER and TAB.TABLE_NAME = DBA_TAB_COMMENTS.TABLE_NAME and TAB.TABLE_TYPE = DBA_TAB_COMMENTS.TABLE_TYPE";
    private static final String COLUMNS = ""//
            + "select COLS.COLUMN_NAME,DATA_TYPE,DATA_TYPE_OWNER,DATA_LENGTH,CHAR_LENGTH,DATA_PRECISION,DATA_SCALE,NULLABLE,DATA_DEFAULT,CHARACTER_SET_NAME,HIDDEN_COLUMN,VIRTUAL_COLUMN,IDENTITY_COLUMN,SENSITIVE_COLUMN,COMM.COMMENTS from DBA_TAB_COLS COLS\n"//
            + "left join DBA_COL_COMMENTS COMM on COLS.OWNER = COMM.OWNER and COLS.TABLE_NAME = COMM.TABLE_NAME and COLS.COLUMN_NAME = COMM.COLUMN_NAME\n";

    public OracleMetadataProvider(Connection connection) {
        super(connection);
    }

    public OracleMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select * from v$version");
        }
    }

    @Override
    public String getCurrentCatalog() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select NAME from v$database");
        }
    }

    @Override
    public String getCurrentSchema() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select SYS_CONTEXT('USERENV','CURRENT_SCHEMA') CURRENT_SCHEMA from dual");
        }
    }

    @Override
    public TableDef searchTable(String catalog, String schema, String table) throws SQLException {
        String dbName = StringUtils.isNotBlank(catalog) ? catalog : schema;
        return getTable(dbName, table);
    }

    @Override
    public Map<String, ColumnDef> getColumnMap(String catalog, String schema, String table) throws SQLException {
        String dbName = StringUtils.isNotBlank(catalog) ? catalog : schema;
        List<OracleColumn> columns = this.getColumns(dbName, table);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(OracleColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    public List<String> getTableSpace() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForList("select NAME from v$tablespace", String.class);
        }
    }

    public List<OracleSchema> getSchemas() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(SCHEMA);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertSchema).collect(Collectors.toList());
        }
    }

    public OracleSchema getSchema(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(SCHEMA + " where USERNAME = ?", schemaName);
            if (mapList == null || mapList.isEmpty()) {
                return null;
            }
            return this.convertSchema(mapList.get(0));
        }
    }

    public Map<String, List<OracleTable>> getTables(String[] schemaName) throws SQLException {
        List<String> schemaList = stringArray2List(schemaName);
        //
        String queryString;
        Object[] queryArgs;
        if (schemaList.isEmpty()) {
            queryString = TABLE;
            queryArgs = new Object[] {};
        } else {
            queryString = TABLE + " where TAB.OWNER in " + buildWhereIn(schemaList);
            queryArgs = schemaList.toArray();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<OracleTable>> resultData = new HashMap<>();
            mapList.forEach(recordMap -> {
                String owner = safeToString(recordMap.get("OWNER"));
                List<OracleTable> tableList = resultData.computeIfAbsent(owner, k -> new ArrayList<>());
                OracleTable table = this.convertTable(recordMap);
                tableList.add(table);
            });
            return resultData;
        }
    }

    public List<OracleTable> getAllTables() throws SQLException {
        String currentSchema = getCurrentSchema();
        return getAllTables(currentSchema);
    }

    public List<OracleTable> getAllTables(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current schema is not set");
            }
        }
        //
        Map<String, List<OracleTable>> tableMap = this.getTables(new String[] { schemaName });
        if (tableMap == null || tableMap.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<OracleTable> tableList = tableMap.get(schemaName);
            if (tableList == null || tableList.isEmpty()) {
                return Collections.emptyList();
            } else {
                return tableList;
            }
        }
    }

    public List<OracleTable> findTable(String schemaName, String[] tableName) throws SQLException {
        List<String> tableList = stringArray2List(tableName);
        if (tableList.isEmpty()) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current schema is not set");
            }
        }
        //
        String queryString = TABLE + " where TAB.OWNER = ? and TAB.TABLE_NAME in " + buildWhereIn(tableList);
        List<String> queryArgs = new ArrayList<>();
        queryArgs.add(schemaName);
        queryArgs.addAll(tableList);
        //
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs.toArray());
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertTable).collect(Collectors.toList());
        }
    }

    public OracleTable getTable(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current schema is not set");
            }
        }
        //
        String queryString = TABLE + " where TAB.OWNER = ? and TAB.TABLE_NAME = ?";
        Object[] queryArgs = new Object[] { schemaName, tableName };
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(this::convertTable).findFirst().orElse(null);
        }
    }

    public List<OracleColumn> getColumns(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current schema is not set");
            }
        }
        //
        List<Map<String, Object>> primaryUniqueKeyList = null;
        List<Map<String, Object>> columnList = null;
        try (Connection conn = this.connectSupplier.get()) {
            //COLUMNS
            String queryStringColumn = COLUMNS + "where COLS.DATA_TYPE_OWNER is NULL and COLS.OWNER = ? and COLS.TABLE_NAME = ?";
            columnList = new JdbcTemplate(conn).queryForList(queryStringColumn, schemaName, tableName);
            if (columnList == null) {
                return Collections.emptyList();
            }
            // UK、PK
            String primaryUniqueKeyQuery = ""//
                    + "select COLUMN_NAME,COLS.CONSTRAINT_NAME,CON.CONSTRAINT_TYPE from ALL_CONS_COLUMNS COLS\n"//
                    + "left join DBA_CONSTRAINTS CON on COLS.CONSTRAINT_NAME = CON.CONSTRAINT_NAME\n"//
                    + "where (CON.CONSTRAINT_TYPE = 'P' or CON.CONSTRAINT_TYPE = 'U') and COLS.OWNER = ? and COLS.TABLE_NAME = ?\n"//
                    + "order by COLS.POSITION asc";
            primaryUniqueKeyList = new JdbcTemplate(conn).queryForList(primaryUniqueKeyQuery, schemaName, tableName);
        }
        List<String> primaryKeyColumnNameList = primaryUniqueKeyList.stream().filter(recordMap -> {
            String constraintType = safeToString(recordMap.get("CONSTRAINT_TYPE"));
            return "P".equals(constraintType);
        }).map(recordMap -> {
            return safeToString(recordMap.get("COLUMN_NAME"));
        }).collect(Collectors.toList());
        List<String> uniqueKeyColumnNameList = primaryUniqueKeyList.stream().filter(recordMap -> {
            String constraintType = safeToString(recordMap.get("CONSTRAINT_TYPE"));
            return "U".equals(constraintType);
        }).map(recordMap -> {
            return safeToString(recordMap.get("COLUMN_NAME"));
        }).collect(Collectors.toList());
        //
        return columnList.stream().map(recordMap -> {
            OracleColumn column = new OracleColumn();
            column.setName(safeToString(recordMap.get("COLUMN_NAME")));
            column.setNullable("Y".equals(safeToString(recordMap.get("NULLABLE"))));
            column.setColumnType(safeToString(recordMap.get("DATA_TYPE")));
            column.setSqlType(safeToOracleTypes(recordMap.get("DATA_TYPE")));
            column.setColumnTypeOwner(safeToString(recordMap.get("DATA_TYPE_OWNER")));
            column.setJdbcType(columnTypeMappingToJdbcType(column.getSqlType(), column.getColumnType()));
            if (column.getJdbcType() == null && StringUtils.isNotBlank(column.getColumnTypeOwner())) {
                column.setJdbcType(JDBCType.STRUCT); // 有 Type Name 表示一定是用户创建的类型。
            }
            //
            column.setDataBytesLength(safeToLong(recordMap.get("DATA_LENGTH")));
            column.setDataCharLength(safeToLong(recordMap.get("CHAR_LENGTH")));
            column.setDataPrecision(safeToInteger(recordMap.get("DATA_PRECISION")));
            column.setDataScale(safeToInteger(recordMap.get("DATA_SCALE")));
            column.setDataDefault(safeToString(recordMap.get("DATA_DEFAULT")));
            column.setCharacterSetName(safeToString(recordMap.get("CHARACTER_SET_NAME")));
            column.setHidden("YES".equals(safeToString(recordMap.get("HIDDEN_COLUMN"))));
            column.setVirtual("YES".equals(safeToString(recordMap.get("VIRTUAL_COLUMN"))));
            column.setIdentity("YES".equals(safeToString(recordMap.get("IDENTITY_COLUMN"))));
            column.setSensitive("YES".equals(safeToString(recordMap.get("SENSITIVE_COLUMN"))));
            //
            column.setPrimaryKey(primaryKeyColumnNameList.contains(column.getName()));
            column.setUniqueKey(uniqueKeyColumnNameList.contains(column.getName()));
            column.setComment(safeToString(recordMap.get("COMMENTS")));
            return column;
        }).collect(Collectors.toList());
    }

    public List<OracleConstraint> getConstraint(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = "select OWNER,CONSTRAINT_NAME,CONSTRAINT_TYPE,STATUS,VALIDATED,GENERATED from DBA_CONSTRAINTS " //
                + "where OWNER = ? and TABLE_NAME = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(recordMap -> {
                OracleConstraint constraint = new OracleConstraint();
                constraint.setSchema(safeToString(recordMap.get("OWNER")));
                constraint.setName(safeToString(recordMap.get("CONSTRAINT_NAME")));
                String constraintTypeString = safeToString(recordMap.get("CONSTRAINT_TYPE"));
                constraint.setConstraintType(OracleConstraintType.valueOfCode(constraintTypeString));
                constraint.setEnabled("ENABLED".equalsIgnoreCase(safeToString(recordMap.get("STATUS"))));
                constraint.setValidated("VALIDATED".equalsIgnoreCase(safeToString(recordMap.get("VALIDATED"))));
                constraint.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
                return constraint;
            }).collect(Collectors.toList());
        }
    }

    public List<OracleConstraint> getConstraint(String schemaName, String tableName, OracleConstraintType... cType) throws SQLException {
        List<OracleConstraint> constraintList = getConstraint(schemaName, tableName);
        if (constraintList == null || constraintList.isEmpty()) {
            return constraintList;
        }
        return constraintList.stream().filter(mySqlConstraint -> {
            for (OracleConstraintType constraintType : cType) {
                if (constraintType == mySqlConstraint.getConstraintType()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public OraclePrimaryKey getPrimaryKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = "" //
                + "select CON.OWNER,CON.CONSTRAINT_NAME,CONSTRAINT_TYPE,STATUS,VALIDATED,GENERATED,COLUMN_NAME from DBA_CONS_COLUMNS CC\n" //
                + "left join DBA_CONSTRAINTS CON on CC.CONSTRAINT_NAME = CON.CONSTRAINT_NAME\n" //
                + "where CONSTRAINT_TYPE = 'P' and CC.OWNER = ? and CC.TABLE_NAME = ? order by POSITION asc"; //
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            Set<String> check = new HashSet<>();
            OraclePrimaryKey primaryKey = null;
            for (Map<String, Object> ent : mapList) {
                if (primaryKey == null) {
                    primaryKey = new OraclePrimaryKey();
                    primaryKey.setConstraintType(OracleConstraintType.PrimaryKey);
                    primaryKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(ent.get("STATUS"))));
                    primaryKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(ent.get("VALIDATED"))));
                    primaryKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(ent.get("GENERATED"))));
                }
                primaryKey.setSchema(safeToString(ent.get("OWNER")));
                primaryKey.setName(safeToString(ent.get("CONSTRAINT_NAME")));
                primaryKey.getColumns().add(safeToString(ent.get("COLUMN_NAME")));
                //
                check.add(primaryKey.getSchema() + "," + primaryKey.getName());
                if (check.size() > 1) {
                    throw new SQLException("Data error encountered multiple primary keys " + StringUtils.join(check.toArray(), " -- "));
                }
            }
            return primaryKey;
        }
    }

    public List<OracleUniqueKey> getUniqueKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = "" //
                + "select CON.OWNER,CON.CONSTRAINT_NAME,CONSTRAINT_TYPE,STATUS,VALIDATED,GENERATED,COLUMN_NAME from DBA_CONS_COLUMNS CC\n" //
                + "left join DBA_CONSTRAINTS CON on CC.CONSTRAINT_NAME = CON.CONSTRAINT_NAME\n" //
                + "where CONSTRAINT_TYPE in ('U','P') and CC.OWNER = ? and CC.TABLE_NAME = ? order by POSITION asc"; //
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            Map<String, OracleUniqueKey> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> ent : mapList) {
                String constraintName = safeToString(ent.get("CONSTRAINT_NAME"));
                OracleUniqueKey uniqueKey = groupByName.computeIfAbsent(constraintName, k -> {
                    OracleUniqueKey sqlUniqueKey = new OracleUniqueKey();
                    sqlUniqueKey.setSchema(safeToString(ent.get("OWNER")));
                    sqlUniqueKey.setName(constraintName);
                    sqlUniqueKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(ent.get("STATUS"))));
                    sqlUniqueKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(ent.get("VALIDATED"))));
                    sqlUniqueKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(ent.get("GENERATED"))));
                    //
                    String constraintType = safeToString(ent.get("CONSTRAINT_TYPE"));
                    if ("U".equalsIgnoreCase(constraintType)) {
                        sqlUniqueKey.setConstraintType(OracleConstraintType.Unique);
                    } else if ("P".equalsIgnoreCase(constraintType)) {
                        sqlUniqueKey.setConstraintType(OracleConstraintType.PrimaryKey);
                    } else {
                        throw new UnsupportedOperationException("It's not gonna happen.");
                    }
                    return sqlUniqueKey;
                });
                uniqueKey.getColumns().add(safeToString(ent.get("COLUMN_NAME")));
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<OracleForeignKey> getForeignKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = "" //
                + "select CON.OWNER,CON.CONSTRAINT_NAME,STATUS,VALIDATED,GENERATED,DELETE_RULE,\n" //
                + "       C2.OWNER TARGET_OWNER,C2.TABLE_NAME TARGET_TABLE,\n" //
                + "       C1.COLUMN_NAME SOURCE_COLUMN,C2.COLUMN_NAME TARGET_COLUMN\n" //
                + "from DBA_CONSTRAINTS CON,DBA_CONS_COLUMNS C1,DBA_CONS_COLUMNS C2\n" //
                + "where\n" //
                + "    CON.R_OWNER = C1.OWNER and CON.CONSTRAINT_NAME = C1.CONSTRAINT_NAME and\n" //
                + "    CON.R_OWNER = C2.OWNER and CON.R_CONSTRAINT_NAME = C2.CONSTRAINT_NAME and\n" //
                + "    C1.POSITION = C2.POSITION and\n" //
                + "    CONSTRAINT_TYPE = 'R' and CON.OWNER = ? and CON.TABLE_NAME = ? order by C1.POSITION";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            Map<String, OracleForeignKey> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> ent : mapList) {
                String constraintName = safeToString(ent.get("CONSTRAINT_NAME"));
                OracleForeignKey uniqueKey = groupByName.computeIfAbsent(constraintName, k -> {
                    OracleForeignKey sqlForeignKey = new OracleForeignKey();
                    sqlForeignKey.setSchema(safeToString(ent.get("OWNER")));
                    sqlForeignKey.setName(constraintName);
                    sqlForeignKey.setConstraintType(OracleConstraintType.ForeignKey);
                    sqlForeignKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(ent.get("STATUS"))));
                    sqlForeignKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(ent.get("VALIDATED"))));
                    sqlForeignKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(ent.get("GENERATED"))));
                    //
                    sqlForeignKey.setReferenceSchema(safeToString(ent.get("TARGET_OWNER")));
                    sqlForeignKey.setReferenceTable(safeToString(ent.get("TARGET_TABLE")));
                    sqlForeignKey.setDeleteRule(OracleForeignKeyRule.valueOfCode(safeToString(ent.get("DELETE_RULE"))));
                    return sqlForeignKey;
                });
                uniqueKey.getColumns().add(safeToString(ent.get("SOURCE_COLUMN")));
                uniqueKey.getReferenceMapping().put(safeToString(ent.get("SOURCE_COLUMN")), safeToString(ent.get("TARGET_COLUMN")));
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<OracleIndex> getIndexes(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set");
            }
        }
        //
        String queryString = ""//
                + "select IDX.OWNER,IDX.INDEX_NAME,IDX.INDEX_TYPE,CON.CONSTRAINT_TYPE,IDX.UNIQUENESS,IDX.GENERATED,DESCEND,PARTITIONED,TEMPORARY,COL.COLUMN_NAME,COL.DESCEND\n" //
                + "from DBA_INDEXES IDX\n" //
                + "left join DBA_IND_COLUMNS COL on IDX.OWNER = COL.INDEX_OWNER and IDX.INDEX_NAME = COL.INDEX_NAME\n" //
                + "left join DBA_CONSTRAINTS CON on IDX.OWNER = CON.INDEX_OWNER and IDX.INDEX_NAME = CON.INDEX_NAME\n" //
                + "where IDX.TABLE_OWNER = ? and IDX.TABLE_NAME = ?\n" //
                + "order by COL.COLUMN_POSITION asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            //CONSTRAINT_TYPE
            Map<String, OracleIndex> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> ent : mapList) {
                final String indexOwner = safeToString(ent.get("OWNER"));
                final String indexName = safeToString(ent.get("INDEX_NAME"));
                String indexKey = indexOwner + ":" + indexName;
                OracleIndex oracleIndex = groupByName.computeIfAbsent(indexKey, k -> {
                    OracleIndex oracleIndexEnt = new OracleIndex();
                    oracleIndexEnt.setSchema(indexOwner);
                    oracleIndexEnt.setName(indexName);
                    oracleIndexEnt.setIndexType(OracleIndexType.valueOfCode(safeToString(ent.get("INDEX_TYPE"))));
                    oracleIndexEnt.setPrimaryKey("P".equalsIgnoreCase(safeToString(ent.get("CONSTRAINT_TYPE"))));
                    oracleIndexEnt.setUnique("UNIQUE".equalsIgnoreCase(safeToString(ent.get("UNIQUENESS"))));
                    oracleIndexEnt.setGenerated("Y".equalsIgnoreCase(safeToString(ent.get("GENERATED"))));
                    oracleIndexEnt.setPartitioned("YES".equalsIgnoreCase(safeToString(ent.get("PARTITIONED"))));
                    oracleIndexEnt.setTemporary("Y".equalsIgnoreCase(safeToString(ent.get("TEMPORARY"))));
                    return oracleIndexEnt;
                });
                //
                String columnName = safeToString(ent.get("COLUMN_NAME"));
                String columnDescend = safeToString(ent.get("DESCEND"));
                oracleIndex.getColumns().add(columnName);
                oracleIndex.getStorageType().put(columnName, columnDescend);
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<OracleIndex> getIndexes(String schemaName, String tableName, OracleIndexType... indexTypes) throws SQLException {
        if (indexTypes == null || indexTypes.length == 0) {
            return Collections.emptyList();
        }
        List<OracleIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return Collections.emptyList();
        }
        return indexList.stream().filter(indexItem -> {
            OracleIndexType indexTypeForItem = indexItem.getIndexType();
            for (OracleIndexType matchType : indexTypes) {
                if (indexTypeForItem == matchType) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public OracleIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
        List<OracleIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }
        return indexList.stream().filter(indexItem -> {
            return StringUtils.equals(indexItem.getName(), indexName);
        }).findFirst().orElse(null);
    }

    protected OracleSchema convertSchema(Map<String, Object> recordMap) {
        OracleSchema schema = new OracleSchema();
        schema.setSchema(safeToString(recordMap.get("USERNAME")));
        schema.setStatus(OracleSchemaStatus.valueOfCode(safeToString(recordMap.get("ACCOUNT_STATUS"))));
        schema.setLockDate(safeToDate(recordMap.get("LOCK_DATE")));
        schema.setExpiryDate(safeToDate(recordMap.get("EXPIRY_DATE")));
        schema.setDefaultTablespace(safeToString(recordMap.get("DEFAULT_TABLESPACE")));
        schema.setTemporaryTablespace(safeToString(recordMap.get("TEMPORARY_TABLESPACE")));
        schema.setCreated(safeToDate(recordMap.get("CREATED")));
        schema.setProfile(safeToString(recordMap.get("PROFILE")));
        schema.setAuthenticationType(OracleSchemaAuthType.valueOfCode(safeToString(recordMap.get("AUTHENTICATION_TYPE"))));
        schema.setLastLogin(safeToDate(recordMap.get("LAST_LOGIN")));
        return schema;
    }

    protected OracleTable convertTable(Map<String, Object> recordMap) {
        OracleTable table = new OracleTable();
        table.setSchema(safeToString(recordMap.get("OWNER")));
        table.setTable(safeToString(recordMap.get("TABLE_NAME")));
        table.setTableSpace(safeToString(recordMap.get("TABLESPACE_NAME")));
        table.setReadOnly(safeToBoolean(recordMap.get("READ_ONLY")));
        table.setTableType(OracleTableType.valueOfCode(safeToString(recordMap.get("TABLE_TYPE"))));
        table.setMaterializedLog(safeToString(recordMap.get("LOG_TABLE")));
        table.setComments(safeToString(recordMap.get("COMMENTS")));
        return table;
    }

    protected JDBCType columnTypeMappingToJdbcType(SqlType sqlType, String columnType) {
        if (sqlType == null) {
            return null;
        }
        if (sqlType instanceof OracleSqlTypes) {
            switch ((OracleSqlTypes) sqlType) {
                case TIMESTAMP:
                case TIMESTAMP_WITH_TIME_ZONE:
                case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                case INTERVAL_YEAR_TO_MONTH:
                case INTERVAL_DAY_TO_SECOND:
                    return TIMESTAMP.toJDBCType();
                default:
                    return sqlType.toJDBCType();
            }
        } else {
            return sqlType.toJDBCType();
        }
    }

    protected SqlType safeToOracleTypes(Object obj) {
        if (obj == null) {
            return null;
        }
        return OracleSqlTypes.toOracleType(obj.toString());
    }
}
