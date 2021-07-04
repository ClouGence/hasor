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
            + "select TAB.OWNER,TAB.TABLE_NAME,TABLESPACE_NAME,READ_ONLY,TAB.TABLE_TYPE,LOG_TABLE,LOG_ROWIDS,LOG_PK,LOG_SEQ,COMMENTS from (\n"//
            + "  select OWNER,TABLE_NAME,TABLESPACE_NAME,READ_ONLY,'TABLE' TABLE_TYPE,LOG.LOG_TABLE,LOG.ROWIDS LOG_ROWIDS,LOG.PRIMARY_KEY LOG_PK,LOG.SEQUENCE LOG_SEQ from SYS.DBA_TABLES\n"//
            + "  left join SYS.DBA_MVIEW_LOGS LOG on LOG_OWNER = OWNER and MASTER = TABLE_NAME\n"//
            + "  union all\n"//
            + "  select OWNER,VIEW_NAME, null TABLESPACE_NAME,READ_ONLY,'VIEW' TABLE_TYPE,null LOG_TABLE,null LOG_ROWIDS,null LOG_PK,null LOG_SEQ from SYS.DBA_VIEWS\n"//
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
            List<Map<String, Object>> moreInfo = new JdbcTemplate(conn).queryForList("select PRODUCT,VERSION,STATUS FROM PRODUCT_COMPONENT_VERSION");
            String version = null;
            for (Map<String, Object> line : moreInfo) {
                String productStr = line.get("PRODUCT").toString();
                if (StringUtils.containsAny(productStr, "Oracle Database")) {
                    version = line.get("VERSION").toString();
                    break;
                }
            }
            if (StringUtils.isBlank(version)) {
                return new JdbcTemplate(conn).queryForString("select * from v$version");
            } else {
                return version;
            }
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
        if (StringUtils.isNotBlank(schema)) {
            schema = getCurrentSchema();
        }
        return getTable(schema, table);
    }

    @Override
    public Map<String, ColumnDef> getColumnMap(String catalog, String schema, String table) throws SQLException {
        List<OracleColumn> columns = this.getColumns(schema, table);
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
            // group by schema
            return mapList.stream().map(this::convertTable).collect(Collectors.groupingBy(OracleTable::getSchema));
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
            return convertColumn(recordMap, primaryKeyColumnNameList, uniqueKeyColumnNameList);
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
            return mapList.stream().map(this::convertConstraint).collect(Collectors.toList());
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
            //
            Map<String, Optional<OraclePrimaryKey>> pkMap = mapList.stream().map(this::convertPrimaryKey).collect(Collectors.groupingBy(o -> {
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
            Optional<OraclePrimaryKey> primaryKeyOptional = pkMap.values().stream().findFirst().orElse(Optional.empty());
            return primaryKeyOptional.orElse(null);
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
            //
            return mapList.stream().map(this::convertUniqueKey).collect(Collectors.groupingBy(o -> {
                // group by (schema + name)
                return o.getSchema() + "," + o.getName();
            }, Collectors.reducing((uk1, uk2) -> {
                // reducing group by data in to one.
                uk1.getColumns().addAll(uk2.getColumns());
                return uk1;
            }))).values().stream().map(o -> {
                return o.orElse(null);
            }).filter(Objects::nonNull).collect(Collectors.toList());
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
            //
            return mapList.stream().map(this::convertForeignKey).collect(Collectors.groupingBy(o -> {
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
            //
            return mapList.stream().map(this::convertIndex).collect(Collectors.groupingBy(o -> {
                // group by (schema + name)
                return o.getSchema() + "," + o.getName();
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
        //
        String logTable = safeToString(recordMap.get("LOG_TABLE"));
        if (StringUtils.isNotBlank(logTable)) {
            OracleMaterializedLog materializedLog = new OracleMaterializedLog();
            materializedLog.setLogTable(logTable);
            materializedLog.setLogRowIds("YES".equalsIgnoreCase(safeToString(recordMap.get("LOG_ROWIDS"))));
            materializedLog.setLogPk("YES".equalsIgnoreCase(safeToString(recordMap.get("LOG_PK"))));
            materializedLog.setLogSeq("YES".equalsIgnoreCase(safeToString(recordMap.get("LOG_SEQ"))));
            table.setMaterializedLog(materializedLog);
        }
        table.setComment(safeToString(recordMap.get("COMMENTS")));
        return table;
    }

    protected OracleColumn convertColumn(Map<String, Object> recordMap, List<String> primaryKeyColumnList, List<String> uniqueKeyColumnList) {
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
        column.setCharacterSetName(safeToString(recordMap.get("CHARACTER_SET_NAME")));
        column.setHidden("YES".equals(safeToString(recordMap.get("HIDDEN_COLUMN"))));
        column.setVirtual("YES".equals(safeToString(recordMap.get("VIRTUAL_COLUMN"))));
        column.setIdentity("YES".equals(safeToString(recordMap.get("IDENTITY_COLUMN"))));
        column.setSensitive("YES".equals(safeToString(recordMap.get("SENSITIVE_COLUMN"))));
        column.setDefaultValue(safeToString(recordMap.get("DATA_DEFAULT")));
        //
        column.setPrimaryKey(primaryKeyColumnList.contains(column.getName()));
        column.setUniqueKey(uniqueKeyColumnList.contains(column.getName()));
        column.setComment(safeToString(recordMap.get("COMMENTS")));
        return column;
    }

    protected OracleConstraint convertConstraint(Map<String, Object> recordMap) {
        OracleConstraint constraint = new OracleConstraint();
        constraint.setSchema(safeToString(recordMap.get("OWNER")));
        constraint.setName(safeToString(recordMap.get("CONSTRAINT_NAME")));
        String constraintTypeString = safeToString(recordMap.get("CONSTRAINT_TYPE"));
        constraint.setConstraintType(OracleConstraintType.valueOfCode(constraintTypeString));
        constraint.setEnabled("ENABLED".equalsIgnoreCase(safeToString(recordMap.get("STATUS"))));
        constraint.setValidated("VALIDATED".equalsIgnoreCase(safeToString(recordMap.get("VALIDATED"))));
        constraint.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
        return constraint;
    }

    protected OraclePrimaryKey convertPrimaryKey(Map<String, Object> recordMap) {
        OraclePrimaryKey primaryKey = new OraclePrimaryKey();
        primaryKey.setSchema(safeToString(recordMap.get("OWNER")));
        primaryKey.setName(safeToString(recordMap.get("CONSTRAINT_NAME")));
        primaryKey.setConstraintType(OracleConstraintType.PrimaryKey);
        primaryKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(recordMap.get("STATUS"))));
        primaryKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(recordMap.get("VALIDATED"))));
        primaryKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
        //
        primaryKey.getColumns().add(safeToString(recordMap.get("COLUMN_NAME")));
        return primaryKey;
    }

    protected OracleUniqueKey convertUniqueKey(Map<String, Object> recordMap) {
        OracleUniqueKey uniqueKey = new OracleUniqueKey();
        uniqueKey.setSchema(safeToString(recordMap.get("OWNER")));
        uniqueKey.setName(safeToString(recordMap.get("CONSTRAINT_NAME")));
        uniqueKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(recordMap.get("STATUS"))));
        uniqueKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(recordMap.get("VALIDATED"))));
        uniqueKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
        //
        String constraintType = safeToString(recordMap.get("CONSTRAINT_TYPE"));
        if ("U".equalsIgnoreCase(constraintType)) {
            uniqueKey.setConstraintType(OracleConstraintType.Unique);
        } else if ("P".equalsIgnoreCase(constraintType)) {
            uniqueKey.setConstraintType(OracleConstraintType.PrimaryKey);
        } else {
            throw new UnsupportedOperationException("It's not gonna happen.");
        }
        //
        uniqueKey.getColumns().add(safeToString(recordMap.get("COLUMN_NAME")));
        return uniqueKey;
    }

    protected OracleForeignKey convertForeignKey(Map<String, Object> recordMap) {
        OracleForeignKey foreignKey = new OracleForeignKey();
        foreignKey.setSchema(safeToString(recordMap.get("OWNER")));
        foreignKey.setName(safeToString(recordMap.get("CONSTRAINT_NAME")));
        foreignKey.setConstraintType(OracleConstraintType.ForeignKey);
        foreignKey.setEnabled("ENABLED".equalsIgnoreCase(safeToString(recordMap.get("STATUS"))));
        foreignKey.setValidated("VALIDATED".equalsIgnoreCase(safeToString(recordMap.get("VALIDATED"))));
        foreignKey.setGenerated("GENERATED NAME".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
        //
        foreignKey.setReferenceSchema(safeToString(recordMap.get("TARGET_OWNER")));
        foreignKey.setReferenceTable(safeToString(recordMap.get("TARGET_TABLE")));
        foreignKey.setDeleteRule(OracleForeignKeyRule.valueOfCode(safeToString(recordMap.get("DELETE_RULE"))));
        //
        foreignKey.getColumns().add(safeToString(recordMap.get("SOURCE_COLUMN")));
        foreignKey.getReferenceMapping().put(safeToString(recordMap.get("SOURCE_COLUMN")), safeToString(recordMap.get("TARGET_COLUMN")));
        return foreignKey;
    }

    protected OracleIndex convertIndex(Map<String, Object> recordMap) {
        OracleIndex index = new OracleIndex();
        index.setSchema(safeToString(recordMap.get("OWNER")));
        index.setName(safeToString(recordMap.get("INDEX_NAME")));
        index.setIndexType(OracleIndexType.valueOfCode(safeToString(recordMap.get("INDEX_TYPE"))));
        index.setPrimaryKey("P".equalsIgnoreCase(safeToString(recordMap.get("CONSTRAINT_TYPE"))));
        index.setUnique("UNIQUE".equalsIgnoreCase(safeToString(recordMap.get("UNIQUENESS"))));
        index.setGenerated("Y".equalsIgnoreCase(safeToString(recordMap.get("GENERATED"))));
        index.setPartitioned("YES".equalsIgnoreCase(safeToString(recordMap.get("PARTITIONED"))));
        index.setTemporary("Y".equalsIgnoreCase(safeToString(recordMap.get("TEMPORARY"))));
        //
        String columnName = safeToString(recordMap.get("COLUMN_NAME"));
        String columnDescend = safeToString(recordMap.get("DESCEND"));
        index.getColumns().add(columnName);
        index.getStorageType().put(columnName, columnDescend);
        return index;
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

    protected OracleSqlTypes safeToOracleTypes(Object obj) {
        if (obj == null) {
            return null;
        }
        return OracleSqlTypes.toOracleType(obj.toString());
    }
}
