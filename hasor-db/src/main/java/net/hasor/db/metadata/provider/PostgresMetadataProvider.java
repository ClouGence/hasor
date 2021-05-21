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
            //            + "           case\n"//
            //            + "               when a.attidentity = any (array ['a'::\"char\", 'd'::\"char\"]) then 'YES'::text\n"//
            //            + "               else 'NO'::text\n"//
            //            + "               end                                                                                                                                 as is_identity,\n"//
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
    private static final String       PK          = ""//
            + "select result.table_schema, result.table_name, result.column_name, result.key_seq, result.pk_name\n"//
            + "from (select n.nspname                                        as table_schema,\n"//
            + "             ct.relname                                       as table_name,\n"//
            + "             a.attname                                        as column_name,\n"//
            + "             (information_schema._pg_expandarray(i.indkey)).n as key_seq,\n"//
            + "             ci.relname                                       as pk_name,\n"//
            + "             information_schema._pg_expandarray(i.indkey)     as keys,\n"//
            + "             a.attnum                                         as a_attnum\n"//
            + "      from pg_catalog.pg_class ct\n"//
            + "               join pg_catalog.pg_attribute a on (ct.oid = a.attrelid)\n"//
            + "               join pg_catalog.pg_namespace n on (ct.relnamespace = n.oid)\n"//
            + "               join pg_catalog.pg_index i on (a.attrelid = i.indrelid)\n"//
            + "               join pg_catalog.pg_class ci on (ci.oid = i.indexrelid)\n"//
            + "      where true\n"//
            + "        and n.nspname = ? and ct.relname = ? and i.indisprimary) result\n"//
            + "where result.a_attnum = (result.keys).x\n"//
            + "order by result.table_name, result.pk_name, result.key_seq";
    private static final String       FK          = ""//
            + "select pkic.relname as pk_name, pkn.nspname as pk_table_schema, pkc.relname as pk_table_name, pka.attname as pk_column_name,\n"//
            + "       con.conname  as fk_name, fkn.nspname as fk_table_schema, fkc.relname as fk_table_name, fka.attname as fk_column_name,\n"//
            + "       pos.n        as key_seq,\n"//
            + "       case con.confmatchtype when 'f' then 'FULL' when 'p' then 'PARTIAL' when 's' then 'NONE' else null end as match_option,\n"//
            + "       case con.confupdtype   when 'c' then 'CASCADE' when 'n' then 'SET NULL' when 'd' then 'SET DEFAULT' when 'r' then 'RESTRICT' when 'a' then 'NO ACTION' else null end as update_rule,\n"//
            + "       case con.confdeltype   when 'c' then 'CASCADE' when 'n' then 'SET NULL' when 'd' then 'SET DEFAULT' when 'r' then 'RESTRICT' when 'a' then 'NO ACTION' else null end as delete_rule\n"//
            + "from pg_catalog.pg_namespace pkn, pg_catalog.pg_class pkc, pg_catalog.pg_attribute pka,\n"//
            + "     pg_catalog.pg_namespace fkn, pg_catalog.pg_class fkc, pg_catalog.pg_attribute fka,\n"//
            + "     pg_catalog.pg_constraint con,\n"//
            + "     pg_catalog.generate_series(1, 32) pos(n),\n"//
            + "     pg_catalog.pg_class pkic\n"//
            + "where pkn.oid = pkc.relnamespace\n"//
            + "  and pkc.oid = pka.attrelid\n"//
            + "  and pka.attnum = con.confkey[pos.n]\n"//
            + "  and con.confrelid = pkc.oid\n"//
            + "  and fkn.oid = fkc.relnamespace\n"//
            + "  and fkc.oid = fka.attrelid\n"//
            + "  and fka.attnum = con.conkey[pos.n]\n"//
            + "  and con.conrelid = fkc.oid\n"//
            + "  and con.contype = 'f'\n"//
            + "  and (pkic.relkind = 'i' or pkic.relkind = 'i')\n"//
            + "  and pkic.oid = con.conindid\n"//
            + "  and fkn.nspname = ? and fkc.relname = ?\n"//
            + "order by pkn.nspname, pkc.relname, con.conname, pos.n";
    private static final String       UK_INDEX    = ""//
            + "select tmp.table_schema,\n"//
            + "       tmp.table_name,\n"//
            + "       tmp.non_unique,\n"//
            + "       tmp.index_name,\n"//
            + "       tmp.type,\n"//
            + "       tmp.ordinal_position,\n"//
            + "       trim(both '\"' from pg_catalog.pg_get_indexdef(tmp.ci_oid, tmp.ordinal_position, false)) as column_name,\n"//
            + "       case tmp.am_name when 'btree' then case tmp.i_indoption[tmp.ordinal_position - 1] & 1 when 1 then 'D' else 'A' end else null end as asc_or_desc,\n"//
            + "       tmp.cardinality,\n"//
            + "       tmp.pages,\n"//
            + "       tmp.filter_condition\n"//
            + "from (select n.nspname                                                                                    as table_schema,\n"//
            + "             ct.relname                                                                                   as table_name,\n"//
            + "             not i.indisunique                                                                            as non_unique,\n"//
            + "             ci.relname                                                                                   as index_name,\n"//
            + "             case i.indisclustered when true then 1 else case am.amname when 'hash' then 2 else 3 end end as type,\n"//
            + "             (information_schema._pg_expandarray(i.indkey)).n                                             as ordinal_position,\n"//
            + "             ci.reltuples                                                                                 as cardinality,\n"//
            + "             ci.relpages                                                                                  as pages,\n"//
            + "             pg_catalog.pg_get_expr(i.indpred, i.indrelid)                                                as filter_condition,\n"//
            + "             ci.oid                                                                                       as ci_oid,\n"//
            + "             i.indoption                                                                                  as i_indoption,\n"//
            + "             am.amname                                                                                    as am_name\n"//
            + "      from pg_catalog.pg_class ct\n"//
            + "               join pg_catalog.pg_namespace n on (ct.relnamespace = n.oid)\n"//
            + "               join pg_catalog.pg_index i on (ct.oid = i.indrelid)\n"//
            + "               join pg_catalog.pg_class ci on (ci.oid = i.indexrelid)\n"//
            + "               join pg_catalog.pg_am am on (ci.relam = am.oid)\n"//
            + "      where true and n.nspname = ? and ct.relname = ?) as tmp\n"//
            + "order by non_unique, type, index_name, ordinal_position";
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
            return mapList.stream().map(this::convertTable).collect(Collectors.groupingBy(PostgresTable::getSchema));
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
            return convertColumn(recordMap, primaryKeyColumnNameList, uniqueKeyColumnNameList, serverVersionNumber);
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
            return mapList.stream().map(this::convertConstraint).collect(Collectors.toList());
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
        try (Connection conn = this.connectSupplier.eGet()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(PK, schemaName, tableName);
            if (mapList == null || mapList.isEmpty()) {
                return null;
            }
            //
            Map<String, Optional<PostgresPrimaryKey>> pkMap = mapList.stream().sorted((o1, o2) -> {
                // sort by key_seq
                Integer o1Index = safeToInteger(o1.get("key_seq"));
                Integer o2Index = safeToInteger(o2.get("key_seq"));
                if (o1Index != null && o2Index != null) {
                    return Integer.compare(o1Index, o2Index);
                }
                return 0;
            }).map(this::convertPrimaryKey).collect(Collectors.groupingBy(o -> {
                // group by (schema + name)
                return o.getSchema() + "," + o.getName();
            }, Collectors.reducing((pk1, pk2) -> {
                // reducing group by data in to one.
                pk1.getColumns().addAll(pk2.getColumns());
                return pk1;
            })));
            //
            if (pkMap.size() > 1) {
                throw new SQLException("Data error encountered multiple primary keys '" + StringUtils.join(pkMap.keySet().toArray(), "','") + "'");
            }
            //
            Optional<PostgresPrimaryKey> primaryKeyOptional = pkMap.values().stream().findFirst().orElse(Optional.empty());
            return primaryKeyOptional.orElse(null);
        }
    }

    public List<PostgresUniqueKey> getUniqueKey(String schemaName, String tableName) throws SQLException {
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
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(UK_INDEX, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            //
            return mapList.stream().sorted((o1, o2) -> {
                // sort by ordinal_position
                Integer o1Index = safeToInteger(o1.get("ordinal_position"));
                Integer o2Index = safeToInteger(o2.get("ordinal_position"));
                if (o1Index != null && o2Index != null) {
                    return Integer.compare(o1Index, o2Index);
                }
                return 0;
            }).filter(recordMap -> {
                // ignore nonUnique
                return Boolean.TRUE.equals(!safeToBoolean(recordMap.get("non_unique")));
            }).map(this::convertUniqueKey).collect(Collectors.groupingBy(o -> {
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
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(FK, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            //
            return mapList.stream().sorted((o1, o2) -> {
                // sort by key_seq
                Integer o1Index = safeToInteger(o1.get("key_seq"));
                Integer o2Index = safeToInteger(o2.get("key_seq"));
                if (o1Index != null && o2Index != null) {
                    return Integer.compare(o1Index, o2Index);
                }
                return 0;
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

    public List<PostgresIndex> getIndexes(String schemaName, String tableName, PostgresIndexType[] indexTypes) throws SQLException {
        if (indexTypes == null || indexTypes.length == 0) {
            return Collections.emptyList();
        }
        List<PostgresIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return Collections.emptyList();
        }
        return indexList.stream().filter(indexItem -> {
            for (PostgresIndexType matchType : indexTypes) {
                if (indexItem.getIndexType() == matchType) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public PostgresIndex getIndexes(String schemaName, String tableName, String indexName) throws SQLException {
        List<PostgresIndex> indexList = getIndexes(schemaName, tableName);
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }
        return indexList.stream().filter(indexItem -> {
            return StringUtils.equals(indexItem.getName(), indexName);
        }).findFirst().orElse(null);
    }

    public List<PostgresIndex> getIndexes(String schemaName, String tableName) throws SQLException {
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
        try (Connection conn = this.connectSupplier.get()) {
            List<Map<String, Object>> mapList = new JdbcTemplate(conn).queryForList(UK_INDEX, schemaName, tableName);
            if (mapList == null) {
                return Collections.emptyList();
            }
            //
            return mapList.stream().sorted((o1, o2) -> {
                // sort by ordinal_position
                Integer o1Index = safeToInteger(o1.get("ordinal_position"));
                Integer o2Index = safeToInteger(o2.get("ordinal_position"));
                if (o1Index != null && o2Index != null) {
                    return Integer.compare(o1Index, o2Index);
                }
                return 0;
            }).map(this::convertIndex).collect(Collectors.groupingBy(o -> {
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

    protected PostgresColumn convertColumn(Map<String, Object> recordMap, List<String> primaryKeyColumnList, List<String> uniqueKeyColumnList, long serverVersionNumber) {
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
        column.setPrimaryKey(primaryKeyColumnList.contains(column.getName()));
        column.setUniqueKey(uniqueKeyColumnList.contains(column.getName()));
        column.setComment(safeToString(recordMap.get("comments")));
        return column;
    }

    protected PostgresConstraint convertConstraint(Map<String, Object> recordMap) {
        PostgresConstraint constraint = new PostgresConstraint();
        constraint.setSchema(safeToString(recordMap.get("constraint_schema")));
        constraint.setName(safeToString(recordMap.get("constraint_name")));
        String constraintTypeString = safeToString(recordMap.get("constraint_type"));
        constraint.setConstraintType(PostgresConstraintType.valueOfCode(constraintTypeString));
        return constraint;
    }

    protected PostgresPrimaryKey convertPrimaryKey(Map<String, Object> recordMap) {
        PostgresPrimaryKey primaryKey = new PostgresPrimaryKey();
        primaryKey.setConstraintType(PostgresConstraintType.PrimaryKey);
        primaryKey.setSchema(safeToString(recordMap.get("table_schema")));
        primaryKey.setName(safeToString(recordMap.get("pk_name")));
        //
        primaryKey.getColumns().add(safeToString(recordMap.get("column_name")));
        return primaryKey;
    }

    protected PostgresUniqueKey convertUniqueKey(Map<String, Object> recordMap) {
        PostgresUniqueKey uniqueKey = new PostgresUniqueKey();
        uniqueKey.setSchema(safeToString(recordMap.get("table_schema")));
        uniqueKey.setName(safeToString(recordMap.get("index_name")));
        uniqueKey.setConstraintType(PostgresConstraintType.Unique);
        //
        String columnName = safeToString(recordMap.get("column_name"));
        String ascOrDesc = safeToString(recordMap.get("asc_or_desc"));
        uniqueKey.getColumns().add(columnName);
        uniqueKey.getStorageType().put(columnName, ascOrDesc);
        return uniqueKey;
    }

    protected PostgresForeignKey convertForeignKey(Map<String, Object> recordMap) {
        PostgresForeignKey foreignKey = new PostgresForeignKey();
        foreignKey.setSchema(safeToString(recordMap.get("fk_table_schema")));
        foreignKey.setName(safeToString(recordMap.get("fk_name")));
        foreignKey.setReferenceSchema(safeToString(recordMap.get("pk_table_schema")));
        foreignKey.setReferenceTable(safeToString(recordMap.get("pk_table_name")));
        foreignKey.setConstraintType(PostgresConstraintType.ForeignKey);
        //
        foreignKey.setUpdateRule(PostgresForeignKeyRule.valueOfCode(safeToString(recordMap.get("update_rule"))));
        foreignKey.setDeleteRule(PostgresForeignKeyRule.valueOfCode(safeToString(recordMap.get("delete_rule"))));
        foreignKey.setMatchOption(PostgresForeignMatchOption.valueOfCode(safeToString(recordMap.get("match_option"))));
        //
        String pkColumnName = safeToString(recordMap.get("pk_column_name"));
        String fkColumnName = safeToString(recordMap.get("fk_column_name"));
        foreignKey.getColumns().add(fkColumnName);
        foreignKey.getReferenceMapping().put(fkColumnName, pkColumnName);
        return foreignKey;
    }

    protected PostgresIndex convertIndex(Map<String, Object> recordMap) {
        PostgresIndex pgIndex = new PostgresIndex();
        pgIndex.setSchema(safeToString(recordMap.get("table_schema")));
        pgIndex.setName(safeToString(recordMap.get("index_name")));
        if (safeToBoolean(recordMap.get("non_unique"))) {
            pgIndex.setIndexType(PostgresIndexType.Normal);
        } else {
            pgIndex.setIndexType(PostgresIndexType.Unique);
        }
        //
        String columnName = safeToString(recordMap.get("column_name"));
        String ascOrDesc = safeToString(recordMap.get("asc_or_desc"));
        pgIndex.getColumns().add(columnName);
        pgIndex.getStorageType().put(columnName, ascOrDesc);
        return pgIndex;
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

    protected JDBCType columnTypeMappingToJdbcType(PostgresColumn column, Map<String, Object> recordMap) {
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