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
package net.hasor.db.metadata.jdbc;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.AbstractMetadataServiceSupplierTest;
import net.hasor.db.metadata.domain.jdbc.*;
import net.hasor.db.metadata.provider.JdbcMetadataProvider;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.DsUtils.MYSQL_SCHEMA_NAME;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class Jdbc4MySqlMetadataServiceSupplierTest extends AbstractMetadataServiceSupplierTest<JdbcMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localMySQL();
    }

    @Override
    protected JdbcMetadataProvider initRepository(Connection con) {
        return new JdbcMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, JdbcMetadataProvider repository) throws SQLException, IOException {
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/mysql_script.sql");
    }

    @Test
    public void getCatalogsTest() throws SQLException {
        List<String> catalogs = this.repository.getCatalogs();
        assert catalogs.contains("information_schema");
        assert catalogs.contains("mysql");
        assert catalogs.contains(MYSQL_SCHEMA_NAME);
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<JdbcSchema> schemas = this.repository.getSchemas();
        assert schemas.isEmpty();
    }

    @Test
    public void getTables() throws SQLException {
        List<JdbcTable> tableList = this.repository.findTables("information_schema", null, new String[] { "COLUMNS", "TABLES", "SCHEMATA" });
        assert tableList.size() == 3;
        List<String> tableNames = tableList.stream().map(JdbcTable::getTable).collect(Collectors.toList());
        assert tableNames.contains("COLUMNS");
        assert tableNames.contains("TABLES");
        assert tableNames.contains("SCHEMATA");
    }

    @Test
    public void getTable() throws SQLException {
        JdbcTable tableObj1 = this.repository.getTable("information_schema", null, "COLUMNS");
        JdbcTable tableObj2 = this.repository.getTable("information_schema", null, "ABC");
        JdbcTable tableObj3 = this.repository.getTable(MYSQL_SCHEMA_NAME, null, "t3");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == JdbcTableType.SystemView;
        assert tableObj2 == null;
        assert tableObj3 != null;
        assert tableObj3.getTableType() == JdbcTableType.Table;
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns("information_schema", null, "COLUMNS");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        assert columnMap.size() > 11;
        assert columnMap.containsKey("TABLE_NAME");
        assert columnMap.containsKey("TABLE_SCHEMA");
        assert columnMap.containsKey("TABLE_CATALOG");
        assert columnMap.containsKey("DATA_TYPE");
        assert columnMap.containsKey("NUMERIC_PRECISION");
        assert columnMap.containsKey("IS_NULLABLE");
        assert columnMap.containsKey("NUMERIC_SCALE");
        assert columnMap.containsKey("DATETIME_PRECISION");
        assert columnMap.containsKey("CHARACTER_MAXIMUM_LENGTH");
        assert columnMap.containsKey("CHARACTER_OCTET_LENGTH");
        assert columnMap.containsKey("COLUMN_TYPE");
    }

    @Test
    public void getColumns_2() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("r_int").isPrimaryKey();
        assert columnMap.get("r_int").isUniqueKey();
        assert !columnMap.get("r_k1").isPrimaryKey();
        assert !columnMap.get("r_k1").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("r_k2").isPrimaryKey();
        assert !columnMap.get("r_k2").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("r_name").isPrimaryKey();
        assert columnMap.get("r_name").isUniqueKey();
        assert !columnMap.get("r_index").isPrimaryKey();
        assert !columnMap.get("r_index").isUniqueKey();
        assert !columnMap.get("r_data").isPrimaryKey();
        assert !columnMap.get("r_data").isUniqueKey();
    }

    @Test
    public void getColumns_3() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns(MYSQL_SCHEMA_NAME, null, "tb_user");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        assert columnMap.size() == 7;
        assert columnMap.get("userUUID").isPrimaryKey();
        assert columnMap.get("userUUID").isUniqueKey();
        assert !columnMap.get("email").isPrimaryKey();
        assert columnMap.get("email").isUniqueKey();
    }

    @Test
    public void getConstraint1() throws SQLException {
        List<JdbcConstraint> columnList = this.repository.getConstraint(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
        assert typeMap.size() == 2;
        assert typeMap.containsKey("PRIMARY");
        assert typeMap.containsKey("ptr");
        assert typeMap.get("PRIMARY") == JdbcConstraintType.PrimaryKey;
        assert typeMap.get("ptr") == JdbcConstraintType.ForeignKey;
    }

    @Test
    public void getConstraint2() throws SQLException {
        List<JdbcConstraint> columnList = this.repository.getConstraint(MYSQL_SCHEMA_NAME, null, "proc_table_ref", JdbcConstraintType.ForeignKey);
        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
        assert typeMap.size() == 1;
        assert typeMap.containsKey("ptr");
        assert typeMap.get("ptr") == JdbcConstraintType.ForeignKey;
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "proc_table");
        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("c_id");
        assert primaryKey.getColumns().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        JdbcTable table = this.repository.getTable(MYSQL_SCHEMA_NAME, null, "t3");
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "t3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        List<JdbcIndex> uniqueKeyList = this.repository.getUniqueKey(MYSQL_SCHEMA_NAME, null, "tb_user");
        Map<String, JdbcIndex> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(JdbcIndex::getName, u -> u));
        assert uniqueKeyMap.size() == 3;
        assert uniqueKeyMap.containsKey("PRIMARY");
        assert uniqueKeyMap.containsKey("tb_user_userUUID_uindex");
        assert uniqueKeyMap.containsKey("tb_user_email_userUUID_uindex");
        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumns().size() == 1;
        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumns().contains("userUUID");
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().size() == 2;
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().contains("userUUID");
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().contains("email");
    }

    @Test
    public void getForeignKey() throws SQLException {
        List<JdbcForeignKey> foreignKeyList1 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, null, "tb_user");
        assert foreignKeyList1.size() == 0;
        List<JdbcForeignKey> foreignKeyList2 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
        assert foreignKeyList2.size() == 1;
        JdbcForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getConstraintType() == JdbcConstraintType.ForeignKey;
        assert foreignKey.getColumns().size() == 2;
        assert foreignKey.getColumns().get(0).equals("r_k1");
        assert foreignKey.getColumns().get(1).equals("r_k2");
        assert foreignKey.getName().equals("ptr");
        assert foreignKey.getReferenceCatalog().equals(MYSQL_SCHEMA_NAME);
        assert foreignKey.getReferenceTable().equals("proc_table");
        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_id");
        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_name");
    }

    @Test
    public void getIndexes1() throws SQLException {
        List<JdbcIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "tb_user");
        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
        assert indexMap.size() == 4;
        assert indexMap.containsKey("PRIMARY");
        assert indexMap.containsKey("tb_user_userUUID_uindex");
        assert indexMap.containsKey("tb_user_email_userUUID_uindex");
        assert indexMap.containsKey("normal_index_tb_user");
        assert indexMap.get("PRIMARY").getColumns().size() == 1;
        assert indexMap.get("PRIMARY").getColumns().get(0).equals("userUUID");
        assert indexMap.get("PRIMARY").isUnique();
        assert indexMap.get("tb_user_userUUID_uindex").getColumns().size() == 1;
        assert indexMap.get("tb_user_userUUID_uindex").getColumns().get(0).equals("userUUID");
        assert indexMap.get("tb_user_userUUID_uindex").isUnique();
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().size() == 2;
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(0).equals("email");
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(1).equals("userUUID");
        assert indexMap.get("tb_user_email_userUUID_uindex").isUnique();
        assert indexMap.get("normal_index_tb_user").getColumns().size() == 2;
        assert indexMap.get("normal_index_tb_user").getColumns().get(0).equals("loginPassword");
        assert indexMap.get("normal_index_tb_user").getColumns().get(1).equals("loginName");
        assert !indexMap.get("normal_index_tb_user").isUnique();
    }

    @Test
    public void getIndexes2() throws SQLException {
        List<JdbcIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
        assert indexMap.size() == 4;
        assert indexMap.containsKey("PRIMARY");
        assert indexMap.containsKey("proc_table_ref_uk");
        assert indexMap.containsKey("proc_table_ref_index");
        assert indexMap.containsKey("ptr");
        assert indexMap.get("PRIMARY").getColumns().size() == 1;
        assert indexMap.get("PRIMARY").getColumns().get(0).equals("r_int");
        assert indexMap.get("PRIMARY").isUnique();
        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
        assert indexMap.get("proc_table_ref_uk").isUnique();
        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
        assert !indexMap.get("proc_table_ref_index").isUnique();
        assert indexMap.get("ptr").getColumns().size() == 2;
        assert indexMap.get("ptr").getColumns().get(0).equals("r_k1");
        assert indexMap.get("ptr").getColumns().get(1).equals("r_k2");
        assert !indexMap.get("ptr").isUnique();//MySQL JDBC 驱动无法识别联合索引的唯一特性
    }

    @Test
    public void getIndexes4() throws SQLException {
        JdbcIndex index = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "proc_table_ref", "ptr");
        assert index.getName().equals("ptr");
        assert index.getColumns().size() == 2;
        assert index.getColumns().get(0).equals("r_k1");
        assert index.getColumns().get(1).equals("r_k2");
        assert !index.isUnique();
    }
}