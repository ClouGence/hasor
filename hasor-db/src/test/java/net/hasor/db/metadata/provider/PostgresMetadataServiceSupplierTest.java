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
import net.hasor.db.metadata.AbstractMetadataServiceSupplierTest;
import net.hasor.db.metadata.SqlType;
import net.hasor.db.metadata.domain.postgres.*;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgresMetadataServiceSupplierTest extends AbstractMetadataServiceSupplierTest<PostgresMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localPg();
    }

    @Override
    protected PostgresMetadataProvider initRepository(Connection con) {
        return new PostgresMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, PostgresMetadataProvider repository) throws SQLException, IOException {
        applySql("drop database if exists tester_db");
        applySql("create database tester_db");
        //
        applySql("drop schema if exists tester cascade");
        applySql("create schema tester");
        applySql("set search_path = \"tester\"");
        //
        applySql("drop materialized view tb_user_view_m");
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        applySql("drop table tb_postgre_types");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/postgre_script.sql");
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/all_types/tb_postgre_types.sql");
    }

    @Test
    public void getCatalogsTest() throws SQLException {
        List<String> catalogs = this.repository.getCatalogs();
        assert catalogs.size() >= 1;
        assert catalogs.contains("tester_db");
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<PostgresSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(PostgresSchema::getSchema).collect(Collectors.toList());
        assert collect.contains("tester");
        assert collect.contains("information_schema");
        assert collect.contains("public");
        assert collect.contains("pg_catalog");
    }

    @Test
    public void getSchemaTest() throws SQLException {
        PostgresSchema schema1 = this.repository.getSchema("abc");
        PostgresSchema schema2 = this.repository.getSchema("tester");
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        Map<String, List<PostgresTable>> tableList = this.repository.getTables(new String[] { "tester", "public" });
        assert tableList.size() >= 1;
        assert tableList.containsKey("tester");
        List<String> tableForTesterSchema = tableList.get("tester").stream().map(PostgresTable::getTable).collect(Collectors.toList());
        assert tableForTesterSchema.contains("proc_table");
        assert tableForTesterSchema.contains("proc_table_ref");
        assert tableForTesterSchema.contains("t1");
        assert tableForTesterSchema.contains("t3");
        assert tableForTesterSchema.contains("tb_user");
        assert tableForTesterSchema.contains("tb_user_view");
        assert tableForTesterSchema.contains("tb_postgre_types");
        assert tableForTesterSchema.contains("tb_user_view_m");
        assert tableForTesterSchema.size() == 8;
    }

    @Test
    public void findTables() throws SQLException {
        List<PostgresTable> tableList = this.repository.findTable("tester", new String[] { "proc_table_ref", "tb_user_view", "tb_user_view_m" });
        Map<String, PostgresTable> tableMap = tableList.stream().collect(Collectors.toMap(PostgresTable::getTable, o -> o));
        assert tableMap.size() == 3;
        assert tableMap.containsKey("proc_table_ref");
        assert tableMap.containsKey("tb_user_view");
        assert tableMap.containsKey("tb_user_view_m");
        assert tableMap.get("proc_table_ref").getTableType() == PostgresTableType.Table;
        assert tableMap.get("tb_user_view").getTableType() == PostgresTableType.View;
        assert tableMap.get("tb_user_view_m").getTableType() == PostgresTableType.Materialized;
    }

    @Test
    public void getTable() throws SQLException {
        PostgresTable tableObj1 = this.repository.getTable("tester", "proc_table_ref");
        PostgresTable tableObj2 = this.repository.getTable("tester", "tb_user_view");
        PostgresTable tableObj3 = this.repository.getTable("tester", "abc");
        PostgresTable tableObj4 = this.repository.getTable("tester", "tb_user");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == PostgresTableType.Table;
        assert tableObj2 != null;
        assert tableObj2.getTableType() == PostgresTableType.View;
        assert tableObj3 == null;
        assert tableObj4 != null;
        assert tableObj4.getTableType() == PostgresTableType.Table;
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<PostgresColumn> columnList = this.repository.getColumns("tester", "tb_postgre_types");
        Map<String, PostgresTypes> columnMap1 = columnList.stream().collect(Collectors.toMap(PostgresColumn::getName, PostgresColumn::getSqlType));
        Map<String, String> columnMap2 = columnList.stream().collect(Collectors.toMap(PostgresColumn::getName, PostgresColumn::getColumnType));
        Map<String, JDBCType> columnMap3 = columnList.stream().collect(Collectors.toMap(PostgresColumn::getName, PostgresColumn::getJdbcType));
        //
        List<SqlType> collect1 = columnList.stream().map(PostgresColumn::getSqlType).collect(Collectors.toList());
        assert !collect1.contains(null);
        assert collect1.size() == columnList.size();
        //
        Map<String, PostgresColumn> columnMap = columnList.stream().collect(Collectors.toMap(PostgresColumn::getName, c -> c));
        assert columnMap.containsKey("c_decimal");
        assert columnMap.containsKey("c_date");
        assert columnMap.containsKey("c_timestamp");
        assert columnMap.containsKey("c_numeric_p");
        assert columnMap.containsKey("c_text");
        assert columnMap.containsKey("c_char_n");
        assert columnMap.containsKey("c_character_varying");
        assert columnMap.containsKey("c_uuid");
        assert columnMap.containsKey("c_int4range");
        assert columnMap.containsKey("c_timestamp_n");
        //
        assert columnMap.get("c_decimal").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("c_date").getJdbcType() == JDBCType.DATE;
        assert columnMap.get("c_timestamp").getJdbcType() == JDBCType.TIMESTAMP;
        assert columnMap.get("c_numeric_p").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("c_text").getJdbcType() == JDBCType.VARCHAR;
        assert columnMap.get("c_char_n").getJdbcType() == JDBCType.CHAR;
        assert columnMap.get("c_character_varying").getJdbcType() == JDBCType.VARCHAR;
        assert columnMap.get("c_uuid").getJdbcType() == JDBCType.OTHER;
        assert columnMap.get("c_int4range").getJdbcType() == JDBCType.OTHER;
        assert columnMap.get("c_timestamp_n").getJdbcType() == JDBCType.TIMESTAMP;
    }

    @Test
    public void getColumns_2() throws SQLException {
        List<PostgresColumn> columnList = this.repository.getColumns("tester", "proc_table_ref");
        Map<String, PostgresColumn> columnMap = columnList.stream().collect(Collectors.toMap(PostgresColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("r_int").isPrimaryKey();
        assert !columnMap.get("r_int").isUniqueKey();
        assert !columnMap.get("r_k1").isPrimaryKey();
        assert !columnMap.get("r_k1").isUniqueKey();
        assert !columnMap.get("r_k2").isPrimaryKey();
        assert !columnMap.get("r_k2").isUniqueKey();
        assert !columnMap.get("r_name").isPrimaryKey();
        assert columnMap.get("r_name").isUniqueKey();
        assert !columnMap.get("r_index").isPrimaryKey();
        assert !columnMap.get("r_index").isUniqueKey();
        assert !columnMap.get("r_data").isPrimaryKey();
        assert !columnMap.get("r_data").isUniqueKey();
    }

    @Test
    public void getConstraint1() throws SQLException {
        List<PostgresConstraint> columnList = this.repository.getConstraint("tester", "proc_table_ref");
        Map<String, PostgresConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(PostgresConstraint::getName, PostgresConstraint::getConstraintType));
        Set<String> typeNameSet = columnList.stream().map(PostgresConstraint::getName).collect(Collectors.toSet());
        Set<PostgresConstraintType> typeEnumSet = columnList.stream().map(PostgresConstraint::getConstraintType).collect(Collectors.toSet());
        //
        assert typeMap.size() == 4;
        assert typeNameSet.contains("proc_table_ref_uk");
        assert typeNameSet.contains("ptr");
        assert typeNameSet.contains("proc_table_ref_pkey");
        //
        assert typeMap.get("proc_table_ref_uk") == PostgresConstraintType.Unique;
        assert typeMap.get("ptr") == PostgresConstraintType.ForeignKey;
        assert typeEnumSet.contains(PostgresConstraintType.PrimaryKey);
    }

    @Test
    public void getConstraint2() throws SQLException {
        List<PostgresConstraint> columnList = this.repository.getConstraint("tester", "proc_table_ref", PostgresConstraintType.Unique);
        Map<String, PostgresConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(PostgresConstraint::getName, PostgresConstraint::getConstraintType));
        Set<PostgresConstraintType> typeEnumSet = columnList.stream().map(PostgresConstraint::getConstraintType).collect(Collectors.toSet());
        //
        assert typeMap.size() == 1;
        assert !typeEnumSet.contains(PostgresConstraintType.PrimaryKey);
        assert typeMap.containsKey("proc_table_ref_uk");
        assert !typeMap.containsKey("ptr");
        assert typeMap.get("proc_table_ref_uk") == PostgresConstraintType.Unique;
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        PostgresPrimaryKey primaryKey = this.repository.getPrimaryKey("tester", "proc_table_ref");
        assert primaryKey.getConstraintType() == PostgresConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("proc_table_ref_pkey");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        PostgresPrimaryKey primaryKey = this.repository.getPrimaryKey("tester", "proc_table");
        assert primaryKey.getConstraintType() == PostgresConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("proc_table_pkey");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("c_id");
        assert primaryKey.getColumns().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        PostgresTable table = this.repository.getTable("tester", "t3");
        PostgresPrimaryKey primaryKey = this.repository.getPrimaryKey("tester", "t3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        List<PostgresUniqueKey> uniqueKeyList = this.repository.getUniqueKey("tester", "tb_user");
        Map<String, PostgresUniqueKey> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(PostgresUniqueKey::getName, u -> u));
        assert uniqueKeyMap.size() == 2;
        //
        assert uniqueKeyMap.containsKey("tb_user_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getConstraintType() == PostgresConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumns().size() == 1;
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumns().contains("useruuid");
        //
        assert uniqueKeyMap.containsKey("tb_user_email_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getConstraintType() == PostgresConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().size() == 2;
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().contains("useruuid");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().contains("email");
    }

    @Test
    public void getForeignKey() throws SQLException {
        List<PostgresForeignKey> foreignKeyList1 = this.repository.getForeignKey("tester", "tb_user");
        assert foreignKeyList1.size() == 0;
        List<PostgresForeignKey> foreignKeyList2 = this.repository.getForeignKey("tester", "proc_table_ref");
        assert foreignKeyList2.size() == 1;
        PostgresForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getConstraintType() == PostgresConstraintType.ForeignKey;
        assert foreignKey.getColumns().size() == 2;
        assert foreignKey.getColumns().get(0).equals("r_k1");
        assert foreignKey.getColumns().get(1).equals("r_k2");
        assert foreignKey.getName().equals("ptr");
        assert foreignKey.getReferenceSchema().equals("tester");
        assert foreignKey.getReferenceTable().equals("proc_table");
        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_name");
        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_id");
    }

    @Test
    public void getIndexes1() throws SQLException {
        List<PostgresIndex> indexList = this.repository.getIndexes("tester", "tb_user");
        Map<String, PostgresIndex> indexMap = indexList.stream().collect(Collectors.toMap(PostgresIndex::getName, i -> i));
        assert indexMap.size() == 3;
        //
        assert indexMap.containsKey("normal_index_tb_user");
        assert indexMap.get("normal_index_tb_user").getColumns().size() == 2;
        assert indexMap.get("normal_index_tb_user").getColumns().get(0).equals("loginpassword");
        assert indexMap.get("normal_index_tb_user").getColumns().get(1).equals("loginname");
        assert indexMap.get("normal_index_tb_user").getIndexType() == PostgresIndexType.Normal;
        //
        assert indexMap.containsKey("tb_user_email_useruuid_uindex");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().size() == 2;
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().get(0).equals("email");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().get(1).equals("useruuid");
        assert indexMap.get("tb_user_email_useruuid_uindex").getIndexType() == PostgresIndexType.Unique;
        //
        assert indexMap.containsKey("tb_user_useruuid_uindex");
        assert indexMap.get("tb_user_useruuid_uindex").getColumns().size() == 1;
        assert indexMap.get("tb_user_useruuid_uindex").getColumns().get(0).equals("useruuid");
        assert indexMap.get("tb_user_useruuid_uindex").getIndexType() == PostgresIndexType.Unique;
    }

    @Test
    public void getIndexes2() throws SQLException {
        List<PostgresIndex> indexList = this.repository.getIndexes("tester", "proc_table_ref");
        Map<String, PostgresIndex> indexMap = indexList.stream().collect(Collectors.toMap(PostgresIndex::getName, i -> i));
        //
        assert indexMap.size() == 3;
        assert indexMap.containsKey("proc_table_ref_uk");
        assert indexMap.containsKey("proc_table_ref_pkey");
        assert indexMap.containsKey("proc_table_ref_index");
        //
        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
        assert indexMap.get("proc_table_ref_uk").getIndexType() == PostgresIndexType.Unique;
        //
        assert indexMap.get("proc_table_ref_pkey").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_pkey").getColumns().get(0).equals("r_int");
        assert indexMap.get("proc_table_ref_pkey").getIndexType() == PostgresIndexType.Unique;
        //
        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
        assert indexMap.get("proc_table_ref_index").getIndexType() == PostgresIndexType.Normal;
    }

    @Test
    public void getIndexes3() throws SQLException {
        List<PostgresIndex> indexList = this.repository.getIndexes("tester", "proc_table_ref", new PostgresIndexType[] { PostgresIndexType.Normal });
        Map<String, PostgresIndex> indexMap = indexList.stream().collect(Collectors.toMap(PostgresIndex::getName, i -> i));
        assert indexMap.size() == 1;
        assert indexMap.containsKey("proc_table_ref_index");
    }

    @Test
    public void getIndexes4() throws SQLException {
        PostgresIndex index = this.repository.getIndexes("tester", "proc_table_ref", "proc_table_ref_uk");
        assert index.getName().equals("proc_table_ref_uk");
        assert index.getColumns().size() == 1;
        assert index.getColumns().get(0).equals("r_name");
        assert index.getIndexType() == PostgresIndexType.Unique;
    }
}