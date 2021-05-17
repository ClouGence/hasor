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
import net.hasor.db.metadata.TableDef;
import net.hasor.db.metadata.domain.postgres.*;
import net.hasor.db.metadata.domain.postgres.driver.PgServerVersion;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Postgres 元信息获取，参考资料：
 *   <li>https://www.postgresql.org/docs/13/information-schema.html</li>
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgresMetadataProvider extends AbstractMetadataProvider implements MetaDataService {
    private static final List<String> HIDE_SCHEMA = Arrays.asList("pg_toast", "pg_temp_1", "pg_toast_temp_1");
    private static final String       SCHEMA      = "select schema_name,schema_owner from information_schema.schemata";
    private static final String       TABLE       = ""//
            + "select t.*, c.comment from (\n"//
            + "    select schemaname as table_schema, matviewname as table_name, 'MATERIALIZED VIEW' as table_type, null as is_typed from pg_matviews\n"//
            + "    union\n"//
            + "    select table_schema, table_name, table_type, is_typed from information_schema.tables\n"//
            + ") t\n"//
            + "left join (\n"//
            + "    select n.nspname, c.relname, cast(obj_description(c.relfilenode, 'pg_class') as varchar) as comment from pg_class c\n"//
            + "    left join pg_namespace n on c.relnamespace = n.oid\n"//
            + ") as c on c.nspname = table_schema and c.relname = table_name";
    private static final String       COLUMNS     = ""//
            + "select * from (\n"//
            + "    select n.nspname                                                                                                                               as schema_name,\n"//
            + "           c.relname                                                                                                                               as table_name,\n"//
            + "           a.attname                                                                                                                               as column_name,\n"//
            + "           a.atttypid                                                                                                                              as type_oid,\n"//
            + "           a.atttypmod                                                                                                                             as type_mod,\n"//
            + "           case\n"//
            + "               when t.typtype = 'd' then format_type(t.typbasetype, NULL::integer)\n"//
            + "               else format_type(a.atttypid, NULL::integer) end                                                                                     as type_name,\n"//
            + "           (t.typelem <> 0::oid and t.typlen = '-1'::integer)                                                                                      as type_is_array,\n"//
            + "           t.typbasetype,\n"//
            + "           t.typtype,\n"//
            + "           a.attnotnull or (t.typtype = 'd' and t.typnotnull)                                                                                      as not_null,\n"//
            + "           case\n"//
            + "               when a.attidentity = any (array ['a'::\"char\", 'd'::\"char\"]) then 'YES'::text\n"//
            + "               else 'NO'::text\n"//
            + "               end                                                                                                                                 as is_identity,\n"//
            + "           a.attlen,\n"//
            + "           information_schema._pg_char_max_length(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*))         as character_maximum_length,\n"//
            + "           information_schema._pg_char_octet_length(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*))       as character_octet_length,\n"//
            + "           information_schema._pg_numeric_precision(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*))       as numeric_precision,\n"//
            + "           information_schema._pg_numeric_precision_radix(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*)) as numeric_precision_radix,\n"//
            + "           information_schema._pg_numeric_scale(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*))           as numeric_scale,\n"//
            + "           information_schema._pg_datetime_precision(information_schema._pg_truetypid(a.*, t.*), information_schema._pg_truetypmod(a.*, t.*))      as datetime_precision,\n"//
            + "           t.typtypmod,\n"//
            + "           row_number() over (partition by a.attrelid order by a.attnum)                                                                           as attnum,\n"//
            + "           pg_catalog.pg_get_expr(def.adbin, def.adrelid)                                                                                          as column_default,\n"//
            + "           dsc.description                                                                                                                         as comments\n"//
            + "    from pg_catalog.pg_namespace n\n"//
            + "             join pg_catalog.pg_class c on (c.relnamespace = n.oid)\n"//
            + "             join pg_catalog.pg_attribute a on (a.attrelid = c.oid)\n"//
            + "             left join pg_catalog.pg_type t on (a.atttypid = t.oid)\n"//
            + "             left join pg_catalog.pg_attrdef def on (a.attrelid = def.adrelid and a.attnum = def.adnum)\n"//
            + "             left join pg_catalog.pg_description dsc on (c.oid = dsc.objoid and a.attnum = dsc.objsubid)\n"//
            + "             left join pg_catalog.pg_class dc on (dc.oid = dsc.classoid and dc.relname = 'pg_class')\n"//
            + "             left join pg_catalog.pg_namespace dn on (dc.relnamespace = dn.oid and dn.nspname = 'pg_catalog')\n"//
            + "    where c.relkind in ('r', 'p', 'v', 'f', 'm')\n"//
            + "      and a.attnum > 0\n"//
            + "      and not a.attisdropped\n"//
            + "      and n.nspname = ?\n"//
            + "      and c.relname =?\n"//
            + ") c where true\n"//
            + "order by schema_name, c.table_name, attnum";//
    private              Long         serverVersionNumber;

    public PostgresMetadataProvider(Connection connection) {
        super(connection);
    }

    public PostgresMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getVersion() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select version()");
        }
    }

    public long getServerVersionNumber() throws SQLException {
        if (this.serverVersionNumber == null) {
            try (Connection conn = this.connectSupplier.eGet()) {
                this.serverVersionNumber = new JdbcTemplate(conn).queryForLong("select current_setting('server_version_num')");
            }
        }
        return this.serverVersionNumber;
    }

    @Override
    public String getCurrentCatalog() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select current_database()");
        }
    }

    @Override
    public String getCurrentSchema() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return new JdbcTemplate(conn).queryForString("select current_schema()");
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
        List<PostgresColumn> columns = this.getColumns(dbName, table);
        if (columns != null) {
            return columns.stream().collect(Collectors.toMap(PostgresColumn::getName, o -> o));
        } else {
            return Collections.emptyMap();
        }
    }

    public List<String> getCatalogs() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            List<String> mapList = new JdbcTemplate(conn).queryForList("select datname from pg_database where datistemplate = false and datallowconn = true", String.class);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList;
        }
    }

    public List<PostgresSchema> getSchemas() throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(SCHEMA);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(this::convertSchema).filter(s -> {
                return !HIDE_SCHEMA.contains(s.getSchema());
            }).collect(Collectors.toList());
        }
    }

    public PostgresSchema getSchema(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName) || HIDE_SCHEMA.contains(schemaName)) {
            return null;
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(SCHEMA + " where schema_name = ?", schemaName);
            if (mapList == null || mapList.isEmpty()) {
                return null;
            }
            return this.convertSchema(mapList.get(0));
        }
    }

    public Map<String, List<PostgresTable>> getTables(String[] schemaName) throws SQLException {
        List<String> schemaList = stringArray2List(schemaName);
        //
        String queryString;
        Object[] queryArgs;
        if (schemaList.isEmpty()) {
            queryString = TABLE;
            queryArgs = new Object[] {};
        } else {
            queryString = TABLE + " where table_schema in " + buildWhereIn(schemaList);
            queryArgs = schemaList.toArray();
        }
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, queryArgs);
            if (mapList == null) {
                return Collections.emptyMap();
            }
            Map<String, List<PostgresTable>> resultData = new HashMap<>();
            mapList.forEach(recordMap -> {
                String owner = safeToString(recordMap.get("table_schema"));
                List<PostgresTable> tableList = resultData.computeIfAbsent(owner, k -> new ArrayList<>());
                PostgresTable table = this.convertTable(recordMap);
                tableList.add(table);
            });
            return resultData;
        }
    }

    public List<PostgresTable> getAllTables() throws SQLException {
        String currentSchema = getCurrentSchema();
        return getAllTables(currentSchema);
    }

    public List<PostgresTable> getAllTables(String schemaName) throws SQLException {
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current schema is not set");
            }
        }
        //
        Map<String, List<PostgresTable>> tableMap = this.getTables(new String[] { schemaName });
        if (tableMap == null || tableMap.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<PostgresTable> tableList = tableMap.get(schemaName);
            if (tableList == null || tableList.isEmpty()) {
                return Collections.emptyList();
            } else {
                return tableList;
            }
        }
    }

    public List<PostgresTable> findTable(String schemaName, String[] tableName) throws SQLException {
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
        String queryString = TABLE + " where table_schema = ? and table_name in " + buildWhereIn(tableList);
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

    public PostgresTable getTable(String schemaName, String tableName) throws SQLException {
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
        String queryString = TABLE + " where table_schema = ? and table_name = ?";
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            return mapList.stream().map(this::convertTable).findFirst().orElse(null);
        }
    }

    public List<PostgresColumn> getColumns(String schemaName, String tableName) throws SQLException {
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
            columnList = new JdbcTemplate(conn).queryForList(COLUMNS, schemaName, tableName);
            if (columnList == null) {
                return Collections.emptyList();
            }
            // UK、PK
            String primaryUniqueKeyQuery = ""//
                    + "select tc.table_schema,tc.table_name,tc.constraint_type,kcu.column_name\n"//
                    + "from information_schema.table_constraints as tc\n"//
                    + "  join information_schema.key_column_usage as kcu on tc.constraint_name = kcu.constraint_name\n"//
                    + "  join information_schema.constraint_column_usage as ccu on ccu.constraint_name = tc.constraint_name\n"//
                    + "where constraint_type in ('PRIMARY KEY', 'UNIQUE')\n"//
                    + "and tc.table_schema = ? and tc.table_name = ?";
            primaryUniqueKeyList = new JdbcTemplate(conn).queryForList(primaryUniqueKeyQuery, schemaName, tableName);
        }
        List<String> primaryKeyColumnNameList = primaryUniqueKeyList.stream().filter(recordMap -> {
            String constraintType = safeToString(recordMap.get("constraint_type"));
            return "PRIMARY KEY".equals(constraintType);
        }).map(recordMap -> {
            return safeToString(recordMap.get("column_name"));
        }).collect(Collectors.toList());
        List<String> uniqueKeyColumnNameList = primaryUniqueKeyList.stream().filter(recordMap -> {
            String constraintType = safeToString(recordMap.get("constraint_type"));
            return "UNIQUE".equals(constraintType);
        }).map(recordMap -> {
            return safeToString(recordMap.get("column_name"));
        }).collect(Collectors.toList());
        //
        long serverVersionNumber = getServerVersionNumber();
        return columnList.stream().map(recordMap -> {
            PostgresColumn column = new PostgresColumn();
            column.setName(safeToString(recordMap.get("column_name")));
            column.setNullable(safeToBoolean(recordMap.get("not_null")));
            column.setColumnType(safeToString(recordMap.get("type_name")));
            column.setTypeOid(safeToLong(recordMap.get("type_oid")));
            column.setDataType(safeToString(recordMap.get("type_name")));
            if (safeToBoolean(recordMap.get("type_is_array"))) {
                String dataType = column.getDataType();
                if (dataType.endsWith("[]")) {
                    column.setElementType(dataType.substring(0, dataType.length() - 2));
                }
            }
            column.setSqlType(safeToPostgresTypes(serverVersionNumber, recordMap));
            column.setJdbcType(columnTypeMappingToJdbcType(column, recordMap));
            //
            column.setCharacterMaximumLength(safeToInteger(recordMap.get("character_maximum_length")));
            column.setCharacterOctetLength(safeToInteger(recordMap.get("character_octet_length")));
            column.setDefaultValue(safeToString(recordMap.get("column_default")));
            //
            column.setNumericPrecision(safeToInteger(recordMap.get("numeric_precision")));
            column.setNumericPrecisionRadix(safeToInteger(recordMap.get("numeric_precision_radix")));
            column.setNumericScale(safeToInteger(recordMap.get("numeric_scale")));
            column.setDatetimePrecision(safeToInteger(recordMap.get("datetime_precision")));
            //
            column.setIdentity(safeToBoolean(recordMap.get("is_identity")));
            //
            column.setPrimaryKey(primaryKeyColumnNameList.contains(column.getName()));
            column.setUniqueKey(uniqueKeyColumnNameList.contains(column.getName()));
            column.setComment(safeToString(recordMap.get("comments")));
            return column;
        }).collect(Collectors.toList());
    }

    public List<PostgresConstraint> getConstraint(String schemaName, String tableName) throws SQLException {
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
        String queryString = "select constraint_schema,constraint_name,constraint_type from information_schema.table_constraints" //
                + " where table_schema = ? and table_name = ?";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            return mapList.stream().map(recordMap -> {
                PostgresConstraint constraint = new PostgresConstraint();
                constraint.setSchema(safeToString(recordMap.get("constraint_schema")));
                constraint.setName(safeToString(recordMap.get("constraint_name")));
                String constraintTypeString = safeToString(recordMap.get("constraint_type"));
                constraint.setConstraintType(PostgresConstraintType.valueOfCode(constraintTypeString));
                return constraint;
            }).collect(Collectors.toList());
        }
    }

    public List<PostgresConstraint> getConstraint(String schemaName, String tableName, PostgresConstraintType... cType) throws SQLException {
        List<PostgresConstraint> constraintList = getConstraint(schemaName, tableName);
        if (constraintList == null || constraintList.isEmpty()) {
            return constraintList;
        }
        return constraintList.stream().filter(mySqlConstraint -> {
            for (PostgresConstraintType constraintType : cType) {
                if (constraintType == mySqlConstraint.getConstraintType()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public PostgresPrimaryKey getPrimaryKey(String schemaName, String tableName) throws SQLException {
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
                + "select tc.constraint_schema,tc.constraint_name,tc.constraint_type,kcu.column_name\n" //
                + "from information_schema.table_constraints as tc\n" //
                + "   join information_schema.key_column_usage as kcu on tc.constraint_name = kcu.constraint_name\n" //
                + "where constraint_type = 'PRIMARY KEY' and tc.table_schema = ? and tc.table_name = ?" //
                + "order by kcu.ordinal_position asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            Set<String> check = new HashSet<>();
            PostgresPrimaryKey primaryKey = null;
            for (Map<String, Object> ent : mapList) {
                if (primaryKey == null) {
                    primaryKey = new PostgresPrimaryKey();
                    primaryKey.setConstraintType(PostgresConstraintType.PrimaryKey);
                }
                primaryKey.setSchema(safeToString(ent.get("constraint_schema")));
                primaryKey.setName(safeToString(ent.get("constraint_name")));
                primaryKey.getColumns().add(safeToString(ent.get("column_name")));
                check.add(primaryKey.getSchema() + "," + primaryKey.getName());
                if (check.size() > 1) {
                    throw new SQLException("Data error encountered multiple primary keys " + StringUtils.join(check.toArray(), " -- "));
                }
            }
            return primaryKey;
        }
    }

    public List<PostgresUniqueKey> getUniqueKey(String schemaName, String tableName) throws SQLException {
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
                + "select tc.constraint_schema,tc.constraint_name,tc.constraint_type,kcu.column_name\n" //
                + "from information_schema.table_constraints as tc\n" //
                + "   join information_schema.key_column_usage as kcu on tc.constraint_name = kcu.constraint_name\n" //
                + "where constraint_type in ('PRIMARY KEY', 'UNIQUE') and tc.table_schema = ? and tc.table_name = ?" //
                + "order by kcu.ordinal_position asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return null;
            }
            Map<String, PostgresUniqueKey> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> ent : mapList) {
                final String constraintSchema = safeToString(ent.get("constraint_schema"));
                final String constraintName = safeToString(ent.get("constraint_name"));
                String constraintKey = constraintSchema + "," + constraintName;
                PostgresUniqueKey uniqueKey = groupByName.computeIfAbsent(constraintKey, k -> {
                    PostgresUniqueKey sqlUniqueKey = new PostgresUniqueKey();
                    sqlUniqueKey.setSchema(constraintSchema);
                    sqlUniqueKey.setName(constraintName);
                    //+ "
                    String constraintType = safeToString(ent.get("constraint_type"));
                    if ("PRIMARY KEY".equalsIgnoreCase(constraintType)) {
                        sqlUniqueKey.setConstraintType(PostgresConstraintType.PrimaryKey);
                    } else if ("UNIQUE".equalsIgnoreCase(constraintType)) {
                        sqlUniqueKey.setConstraintType(PostgresConstraintType.Unique);
                    } else {
                        throw new UnsupportedOperationException("It's not gonna happen.");
                    }
                    return sqlUniqueKey;
                });
                uniqueKey.getColumns().add(safeToString(ent.get("column_name")));
            }
            return new ArrayList<>(groupByName.values());
        }
    }

    public List<PostgresForeignKey> getForeignKey(String schemaName, String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(schemaName)) {
            schemaName = getCurrentSchema();
            if (StringUtils.isBlank(schemaName)) {
                throw new SQLException("no schema is specified and the current database is not set.");
            }
        }
        //
        String queryString = "" //
                + "select tc.constraint_schema,tc.constraint_name,tc.constraint_type,tc.table_schema,tc.table_name,kcu.column_name,\n" //
                + "   ccu.table_schema as foreign_table_schema,ccu.table_name as foreign_table_name,ccu.column_name as foreign_column_name,\n" //
                + "   rc.delete_rule,rc.update_rule,rc.match_option\n" //
                + "from (information_schema.table_constraints tc left join information_schema.referential_constraints rc on tc.constraint_schema = rc.constraint_schema and tc.constraint_name = rc.constraint_name)\n" //
                + "   join information_schema.key_column_usage as kcu on tc.constraint_name = kcu.constraint_name\n" //
                + "   join information_schema.constraint_column_usage as ccu on ccu.constraint_name = tc.constraint_name\n" //
                + "where constraint_type = 'FOREIGN KEY' and tc.table_schema = ? and tc.table_name = ?\n" //
                + "order by kcu.ordinal_position asc";
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            Map<String, PostgresForeignKey> groupByName = new LinkedHashMap<>();
            for (Map<String, Object> ent : mapList) {
                final String constraintSchema = safeToString(ent.get("constraint_schema"));
                final String constraintName = safeToString(ent.get("constraint_name"));
                String constraintKey = constraintSchema + "," + constraintName;
                //
                PostgresForeignKey foreignKey = groupByName.computeIfAbsent(constraintKey, k -> {
                    PostgresForeignKey sqlForeignKey = new PostgresForeignKey();
                    sqlForeignKey.setSchema(constraintSchema);
                    sqlForeignKey.setName(constraintName);
                    sqlForeignKey.setConstraintType(PostgresConstraintType.ForeignKey);
                    sqlForeignKey.setReferenceSchema(safeToString(ent.get("foreign_table_schema")));
                    sqlForeignKey.setReferenceTable(safeToString(ent.get("foreign_table_name")));
                    //
                    sqlForeignKey.setUpdateRule(PostgresForeignKeyRule.valueOfCode(safeToString(ent.get("update_rule"))));
                    sqlForeignKey.setDeleteRule(PostgresForeignKeyRule.valueOfCode(safeToString(ent.get("delete_rule"))));
                    sqlForeignKey.setMatchOption(PostgresForeignMatchOption.valueOfCode(safeToString(ent.get("match_option"))));
                    return sqlForeignKey;
                });
                //
                foreignKey.getColumns().add(safeToString(ent.get("column_name")));
                foreignKey.getReferenceMapping().put(safeToString(ent.get("column_name")), safeToString(ent.get("foreign_column_name")));
            }
            return new ArrayList<>(groupByName.values());
        }
    }
    //    public List<OracleIndex> getIndexes(String schemaName, String tableName) throws SQLException {
    //        if (StringUtils.isBlank(tableName)) {
    //            return Collections.emptyList();
    //        }
    //        if (StringUtils.isBlank(schemaName)) {
    //            schemaName = getCurrentSchema();
    //            if (StringUtils.isBlank(schemaName)) {
    //                throw new SQLException("no schema is specified and the current database is not set");
    //            }
    //        }
    //        //
    //        String queryString = ""//
    //                + "select IDX.OWNER,IDX.INDEX_NAME,IDX.INDEX_TYPE,CON.CONSTRAINT_TYPE,IDX.UNIQUENESS,IDX.GENERATED,DESCEND,PARTITIONED,TEMPORARY,COL.COLUMN_NAME,COL.DESCEND\n" //
    //                + "from DBA_INDEXES IDX\n" //
    //                + "left join DBA_IND_COLUMNS COL on IDX.OWNER = COL.INDEX_OWNER and IDX.INDEX_NAME = COL.INDEX_NAME\n" //
    //                + "left join DBA_CONSTRAINTS CON on IDX.OWNER = CON.INDEX_OWNER and IDX.INDEX_NAME = CON.INDEX_NAME\n" //
    //                + "where IDX.TABLE_OWNER = ? and IDX.TABLE_NAME = ?\n" //
    //                + "order by COL.COLUMN_POSITION asc";
    //        try (Connection conn = this.connectSupplier.get()) {
    //            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(queryString, schemaName, tableName);
    //            if (mapList == null) {
    //                return Collections.emptyList();
    //            }
    //            //+ "CONSTRAINT_TYPE
    //            Map<String, OracleIndex> groupByName = new LinkedHashMap<>();
    //            for (Map<String, Object> ent : mapList) {
    //                final String indexOwner = safeToString(ent.get("OWNER"));
    //                final String indexName = safeToString(ent.get("INDEX_NAME"));
    //                String indexKey = indexOwner + ":" + indexName;
    //                OracleIndex oracleIndex = groupByName.computeIfAbsent(indexKey, k -> {
    //                    OracleIndex oracleIndexEnt = new OracleIndex();
    //                    oracleIndexEnt.setSchema(indexOwner);
    //                    oracleIndexEnt.setName(indexName);
    //                    oracleIndexEnt.setIndexType(OracleIndexType.valueOfCode(safeToString(ent.get("INDEX_TYPE"))));
    //                    oracleIndexEnt.setPrimaryKey("P".equalsIgnoreCase(safeToString(ent.get("CONSTRAINT_TYPE"))));
    //                    oracleIndexEnt.setUnique("UNIQUE".equalsIgnoreCase(safeToString(ent.get("UNIQUENESS"))));
    //                    oracleIndexEnt.setGenerated("Y".equalsIgnoreCase(safeToString(ent.get("GENERATED"))));
    //                    oracleIndexEnt.setPartitioned("YES".equalsIgnoreCase(safeToString(ent.get("PARTITIONED"))));
    //                    oracleIndexEnt.setTemporary("Y".equalsIgnoreCase(safeToString(ent.get("TEMPORARY"))));
    //                    return oracleIndexEnt;
    //                });
    //                //+ "
    //                String columnName = safeToString(ent.get("COLUMN_NAME"));
    //                String columnDescend = safeToString(ent.get("DESCEND"));
    //                oracleIndex.getColumns().add(columnName);
    //                oracleIndex.getStorageType().put(columnName, columnDescend);
    //            }
    //            return new ArrayList<>(groupByName.values());
    //        }
    //    }
    //
    //    public List<OracleIndex> getIndexes(String schemaName, String tableName, OracleIndexType... indexTypes) throws SQLException {
    //        if (indexTypes == null || indexTypes.length == 0) {
    //            return Collections.emptyList();
    //        }
    //        List<OracleIndex> indexList = getIndexes(schemaName, tableName);
    //        if (indexList == null || indexList.isEmpty()) {
    //            return Collections.emptyList();
    //        }
    //        return indexList.stream().filter(indexItem -> {
    //            OracleIndexType indexTypeForItem = indexItem.getIndexType();
    //            for (OracleIndexType matchType : indexTypes) {
    //                if (indexTypeForItem == matchType) {
    //                    return true;
    //                }
    //            }
    //            return false;
    //        }).collect(Collectors.toList());
    //    }
    //
    //    public OracleIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
    //        List<OracleIndex> indexList = getIndexes(schemaName, tableName);
    //        if (indexList == null || indexList.isEmpty()) {
    //            return null;
    //        }
    //        return indexList.stream().filter(indexItem -> {
    //            return StringUtils.equals(indexItem.getName(), indexName);
    //        }).findFirst().orElse(null);
    //    }

    protected PostgresSchema convertSchema(Map<String, Object> recordMap) {
        PostgresSchema schema = new PostgresSchema();
        schema.setSchema(safeToString(recordMap.get("schema_name")));
        schema.setOwner(safeToString(recordMap.get("schema_owner")));
        return schema;
    }

    protected PostgresTable convertTable(Map<String, Object> recordMap) {
        PostgresTable table = new PostgresTable();
        table.setSchema(safeToString(recordMap.get("table_schema")));
        table.setTable(safeToString(recordMap.get("table_name")));
        table.setTableType(PostgresTableType.valueOfCode(safeToString(recordMap.get("table_type"))));
        table.setTyped("YES".equalsIgnoreCase(safeToString(recordMap.get("is_typed"))));
        table.setComment(safeToString(recordMap.get("comment")));
        return table;
    }

    private JDBCType columnTypeMappingToJdbcType(PostgresColumn column, Map<String, Object> recordMap) {
        if (recordMap == null) {
            return null;
        }
        String typType = safeToString(recordMap.get("typtype"));
        String typeName = safeToString(recordMap.get("type_name"));
        //
        if ("c".equals(typType)) {
            return JDBCType.STRUCT;
        } else if ("d".equals(typType)) {
            return JDBCType.DISTINCT;
        } else if ("e".equals(typType)) {
            return JDBCType.VARCHAR;
        }
        if (typeName.endsWith("[]")) {
            return JDBCType.ARRAY;
        }
        //
        PostgresTypes sqlType = column.getSqlType();
        if (sqlType != null && sqlType.toJDBCType() != null) {
            return sqlType.toJDBCType();
        }
        return JDBCType.OTHER;
    }

    private static final Map<String, String> aliasMap = new HashMap<>();

    static {
        aliasMap.put("serial2", "smallserial");
        aliasMap.put("serial4", "serial");
        aliasMap.put("serial8", "bigserial");
        aliasMap.put("int", "integer");
        aliasMap.put("int2", "smallint");
        aliasMap.put("int4", "integer");
        aliasMap.put("int8", "bigint");
        aliasMap.put("decimal", "numeric");
        aliasMap.put("float4", "real");
        aliasMap.put("float", "double precision");
        aliasMap.put("float8", "double precision");
        aliasMap.put("char", "character");
        aliasMap.put("varchar", "character varying");
        aliasMap.put("timestamp", "timestamp without time zone");
        aliasMap.put("timestamptz", "timestamp with time zone");
        aliasMap.put("time", "time without time zone");
        aliasMap.put("timetz", "time with time zone");
        aliasMap.put("varbit", "bit varying");
        aliasMap.put("bool", "boolean");
        //
        aliasMap.put("int[]", "integer[]");
        aliasMap.put("int2[]", "smallint[]");
        aliasMap.put("int4[]", "integer[]");
        aliasMap.put("int8[]", "bigint[]");
        aliasMap.put("decimal[]", "numeric[]");
        aliasMap.put("float4[]", "real[]");
        aliasMap.put("float[]", "double precision[]");
        aliasMap.put("float8[]", "double precision[]");
        aliasMap.put("char[]", "character[]");
        aliasMap.put("varchar[]", "character varying[]");
        aliasMap.put("timestamp[]", "timestamp without time zone[]");
        aliasMap.put("timestamptz[]", "timestamp with time zone[]");
        aliasMap.put("time[]", "time without time zone[]");
        aliasMap.put("timetz[]", "time with time zone[]");
        aliasMap.put("varbit[]", "bit varying[]");
        aliasMap.put("bool[]", "boolean[]");
    }

    protected PostgresTypes safeToPostgresTypes(long serverVersionNumber, Map<String, Object> record) {
        if (record == null) {
            return null;
        }
        Long typeOid = safeToLong(record.get("type_oid"));
        String defaultValue = safeToString(record.get("column_default"));
        String typeName = safeToString(record.get("type_name"));
        //
        PostgresTypes pgTypeEnum = PostgresTypes.valueOfTypeOid(typeOid.intValue());
        if (pgTypeEnum == null) {
            pgTypeEnum = PostgresTypes.valueOfTypeOid(typeOid);
            if (pgTypeEnum == null) {
                String name = typeName.toLowerCase();
                if (aliasMap.containsKey(name)) {
                    typeName = aliasMap.get(name);
                }
                pgTypeEnum = PostgresTypes.valueOfCode(typeName);
            }
        }
        //
        if (pgTypeEnum == null) {
            return null;
        }
        //
        if (defaultValue != null && defaultValue.contains("nextval(")) {
            boolean gte_9_2 = serverVersionNumber >= PgServerVersion.v9_2.getVersionNum();
            if (pgTypeEnum == PostgresTypes.INTEGER) {
                pgTypeEnum = PostgresTypes.SERIAL;
            } else if (pgTypeEnum == PostgresTypes.BIGINT) {
                pgTypeEnum = PostgresTypes.BIGSERIAL;
            } else if (pgTypeEnum == PostgresTypes.SMALLINT && gte_9_2) {
                pgTypeEnum = PostgresTypes.SMALLSERIAL;
            }
        }
        //
        return pgTypeEnum;
    }
}