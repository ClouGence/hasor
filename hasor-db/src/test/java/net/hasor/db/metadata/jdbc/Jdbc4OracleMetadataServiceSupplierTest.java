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
import net.hasor.db.metadata.domain.jdbc.JdbcColumn;
import net.hasor.db.metadata.domain.jdbc.JdbcSchema;
import net.hasor.db.metadata.domain.jdbc.JdbcTable;
import net.hasor.db.metadata.provider.JdbcMetadataProvider;
import net.hasor.test.db.utils.DsUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class Jdbc4OracleMetadataServiceSupplierTest {
    private Connection           connection;
    private JdbcMetadataProvider repository;

    @Before
    public void beforeTest() throws SQLException, IOException {
        this.connection = DsUtils.localOracle();
        this.repository = new JdbcMetadataProvider(this.connection);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.connection);
        //
        List<JdbcTable> allTables = this.repository.getAllTables();
        if (!allTables.isEmpty()) {
            Map<String, JdbcTable> collect = allTables.stream().collect(Collectors.toMap(JdbcTable::getTable, o -> o));
            //
            if (collect.containsKey("TB_USER_VIEW")) {
                jdbcTemplate.execute("drop view tb_user_view");
            }
            if (collect.containsKey("TB_USER")) {
                try {
                    jdbcTemplate.execute("drop materialized view log on tb_user");
                } catch (Exception e) { /**/ }
                jdbcTemplate.execute("drop table tb_user");
            }
            if (collect.containsKey("PROC_TABLE_REF")) {
                jdbcTemplate.execute("drop table proc_table_ref");
            }
            if (collect.containsKey("PROC_TABLE")) {
                jdbcTemplate.execute("drop table proc_table");
            }
            if (collect.containsKey("T3")) {
                jdbcTemplate.execute("drop table t3");
            }
            if (collect.containsKey("T1")) {
                jdbcTemplate.execute("drop table t1");
            }
            if (collect.containsKey("TB_ORACLE_TYPES")) {
                jdbcTemplate.execute("drop table tb_oracle_types");
            }
        }
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/oracle_script.sql");
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/tb_oracle_types.sql");
    }

    @After
    public void afterTest() throws SQLException {
        this.connection.close();
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<JdbcSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(JdbcSchema::getSchema).collect(Collectors.toList());
        assert collect.contains("SYSTEM");
        assert collect.contains("SYS");
        assert collect.contains("SCOTT");
    }

    @Test
    public void getSchemaByName() throws SQLException {
        JdbcSchema schema1 = this.repository.getSchemaByName(null, "abc");
        JdbcSchema schema2 = this.repository.getSchemaByName(null, "SYSTEM");
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        List<JdbcTable> tableList = this.repository.findTables(null, "SCOTT", new String[] { "PROC_TABLE_REF", "PROC_TABLE", "T3" });
        assert tableList.size() == 3;
        List<String> tableNames = tableList.stream().map(JdbcTable::getTable).collect(Collectors.toList());
        assert tableNames.contains("PROC_TABLE_REF");
        assert tableNames.contains("PROC_TABLE");
        assert tableNames.contains("T3");
    }

    @Test
    public void getTable() throws SQLException {
        JdbcTable tableObj1 = this.repository.getTable(null, "SCOTT", "PROC_TABLE_REF");
        JdbcTable tableObj2 = this.repository.getTable(null, "SCOTT", "ABC");
        JdbcTable tableObj3 = this.repository.getTable(null, "SCOTT", "T3");
        assert tableObj1 != null;
        assert tableObj1.getTableType().equals("TABLE");
        assert tableObj2 == null;
        assert tableObj3 != null;
        assert tableObj3.getTableType().equals("TABLE");
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns(null, "SCOTT", "TB_ORACLE_TYPES");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        //
        List<JDBCType> collect2 = columnList.stream().map(JdbcColumn::getJdbcType).filter(Objects::nonNull).collect(Collectors.toList());
        assert collect2.size() < columnList.size();
        //
        assert columnMap.containsKey("C_DECIMAL");
        assert columnMap.containsKey("C_DATE");
        assert columnMap.containsKey("C_TIMESTAMP");
        assert columnMap.containsKey("C_NUMBER_N");
        assert columnMap.containsKey("C_NCLOB");
        assert columnMap.containsKey("C_VARCHAR2");
        assert columnMap.containsKey("C_NVARCHAR2");
        assert columnMap.containsKey("C_UROWID");
        assert columnMap.containsKey("C_INT");
        assert columnMap.containsKey("C_NUMBER");
        assert columnMap.containsKey("C_CHAR_NB");
        assert columnMap.containsKey("C_TIMESTAMP_N_Z");
        assert columnMap.containsKey("C_NATIONAL_CHARACTER_VARYING");
        //
        assert columnMap.get("C_DECIMAL").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_DATE").getJdbcType() == JDBCType.TIMESTAMP;
        assert columnMap.get("C_TIMESTAMP").getJdbcType() == JDBCType.TIMESTAMP;
        assert columnMap.get("C_NUMBER_N").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_NCLOB").getJdbcType() == JDBCType.NCLOB;
        assert columnMap.get("C_VARCHAR2").getJdbcType() == JDBCType.VARCHAR;
        assert columnMap.get("C_NVARCHAR2").getJdbcType() == JDBCType.NVARCHAR;
        assert columnMap.get("C_UROWID").getJdbcType() == null;
        assert columnMap.get("C_INT").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_NUMBER").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_CHAR_NB").getJdbcType() == JDBCType.CHAR;
        assert columnMap.get("C_TIMESTAMP_N_Z").getJdbcType() == JDBCType.TIMESTAMP;
        assert columnMap.get("C_NATIONAL_CHARACTER_VARYING").getJdbcType() == JDBCType.NVARCHAR;
    }

    @Test
    public void getColumns_2() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns(null, "SCOTT", "PROC_TABLE_REF");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("R_INT").isPrimaryKey();
        assert columnMap.get("R_INT").isUniqueKey();
        assert !columnMap.get("R_K1").isPrimaryKey();
        assert !columnMap.get("R_K1").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("R_K2").isPrimaryKey();
        assert !columnMap.get("R_K2").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("R_NAME").isPrimaryKey();
        assert columnMap.get("R_NAME").isUniqueKey();
        assert !columnMap.get("R_INDEX").isPrimaryKey();
        assert !columnMap.get("R_INDEX").isUniqueKey();
        assert !columnMap.get("R_DATA").isPrimaryKey();
        assert !columnMap.get("R_DATA").isUniqueKey();
    }

    @Test
    public void getColumns_3() throws SQLException {
        List<JdbcColumn> columnList = this.repository.getColumns(null, "SCOTT", "TB_USER");
        Map<String, JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(JdbcColumn::getName, c -> c));
        assert columnMap.size() == 7;
        assert columnMap.get("USERUUID").isPrimaryKey();
        assert columnMap.get("USERUUID").isUniqueKey();
        assert !columnMap.get("EMAIL").isPrimaryKey();
        assert columnMap.get("EMAIL").isUniqueKey();
    }
    //    @Test
    //    public void getConstraint1() throws SQLException {
    //        List<JdbcConstraint> columnList = this.repository.getConstraint(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
    //        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
    //        assert typeMap.size() == 2;
    //        assert typeMap.containsKey("PRIMARY");
    //        assert typeMap.containsKey("ptr");
    //        assert typeMap.get("PRIMARY") == JdbcConstraintType.PrimaryKey;
    //        assert typeMap.get("ptr") == JdbcConstraintType.ForeignKey;
    //    }
    //
    //    @Test
    //    public void getConstraint2() throws SQLException {
    //        List<JdbcConstraint> columnList = this.repository.getConstraint(MYSQL_SCHEMA_NAME, null, "proc_table_ref", JdbcConstraintType.ForeignKey);
    //        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
    //        assert typeMap.size() == 1;
    //        assert typeMap.containsKey("ptr");
    //        assert typeMap.get("ptr") == JdbcConstraintType.ForeignKey;
    //    }
    //
    //    @Test
    //    public void getPrimaryKey1() throws SQLException {
    //        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
    //        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
    //        assert primaryKey.getName().equals("PRIMARY");
    //        assert primaryKey.getColumns().size() == 1;
    //        assert primaryKey.getColumns().contains("r_int");
    //    }
    //
    //    @Test
    //    public void getPrimaryKey2() throws SQLException {
    //        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "proc_table");
    //        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
    //        assert primaryKey.getName().equals("PRIMARY");
    //        assert primaryKey.getColumns().size() == 2;
    //        assert primaryKey.getColumns().contains("c_id");
    //        assert primaryKey.getColumns().contains("c_name");
    //    }
    //
    //    @Test
    //    public void getPrimaryKey3() throws SQLException {
    //        JdbcTable table = this.repository.getTable(MYSQL_SCHEMA_NAME, null, "t3");
    //        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, null, "t3");
    //        assert table != null;
    //        assert primaryKey == null;
    //    }
    //
    //    @Test
    //    public void getUniqueKey() throws SQLException {
    //        List<JdbcIndex> uniqueKeyList = this.repository.getUniqueKey(MYSQL_SCHEMA_NAME, null, "tb_user");
    //        Map<String, JdbcIndex> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(JdbcIndex::getName, u -> u));
    //        assert uniqueKeyMap.size() == 3;
    //        assert uniqueKeyMap.containsKey("PRIMARY");
    //        assert uniqueKeyMap.containsKey("tb_user_userUUID_uindex");
    //        assert uniqueKeyMap.containsKey("tb_user_email_userUUID_uindex");
    //        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumns().size() == 1;
    //        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumns().contains("userUUID");
    //        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().size() == 2;
    //        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().contains("userUUID");
    //        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumns().contains("email");
    //    }
    //
    //    @Test
    //    public void getForeignKey() throws SQLException {
    //        List<JdbcForeignKey> foreignKeyList1 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, null, "tb_user");
    //        assert foreignKeyList1.size() == 0;
    //        List<JdbcForeignKey> foreignKeyList2 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
    //        assert foreignKeyList2.size() == 1;
    //        JdbcForeignKey foreignKey = foreignKeyList2.get(0);
    //        assert foreignKey.getConstraintType() == JdbcConstraintType.ForeignKey;
    //        assert foreignKey.getFkColumn().size() == 2;
    //        assert foreignKey.getFkColumn().get(0).equals("r_k1");
    //        assert foreignKey.getFkColumn().get(1).equals("r_k2");
    //        assert foreignKey.getName().equals("ptr");
    //        assert foreignKey.getReferenceCatalog().equals(MYSQL_SCHEMA_NAME);
    //        assert foreignKey.getReferenceTable().equals("proc_table");
    //        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_id");
    //        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_name");
    //    }
    //
    //    @Test
    //    public void getIndexes1() throws SQLException {
    //        List<JdbcIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "tb_user");
    //        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
    //        assert indexMap.size() == 4;
    //        assert indexMap.containsKey("PRIMARY");
    //        assert indexMap.containsKey("tb_user_userUUID_uindex");
    //        assert indexMap.containsKey("tb_user_email_userUUID_uindex");
    //        assert indexMap.containsKey("normal_index_tb_user");
    //        assert indexMap.get("PRIMARY").getColumns().size() == 1;
    //        assert indexMap.get("PRIMARY").getColumns().get(0).equals("userUUID");
    //        assert indexMap.get("PRIMARY").isUnique();
    //        assert indexMap.get("tb_user_userUUID_uindex").getColumns().size() == 1;
    //        assert indexMap.get("tb_user_userUUID_uindex").getColumns().get(0).equals("userUUID");
    //        assert indexMap.get("tb_user_userUUID_uindex").isUnique();
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().size() == 2;
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(0).equals("email");
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(1).equals("userUUID");
    //        assert indexMap.get("tb_user_email_userUUID_uindex").isUnique();
    //        assert indexMap.get("normal_index_tb_user").getColumns().size() == 2;
    //        assert indexMap.get("normal_index_tb_user").getColumns().get(0).equals("loginPassword");
    //        assert indexMap.get("normal_index_tb_user").getColumns().get(1).equals("loginName");
    //        assert !indexMap.get("normal_index_tb_user").isUnique();
    //    }
    //
    //    @Test
    //    public void getIndexes2() throws SQLException {
    //        List<JdbcIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "proc_table_ref");
    //        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
    //        assert indexMap.size() == 4;
    //        assert indexMap.containsKey("PRIMARY");
    //        assert indexMap.containsKey("proc_table_ref_uk");
    //        assert indexMap.containsKey("proc_table_ref_index");
    //        assert indexMap.containsKey("ptr");
    //        assert indexMap.get("PRIMARY").getColumns().size() == 1;
    //        assert indexMap.get("PRIMARY").getColumns().get(0).equals("r_int");
    //        assert indexMap.get("PRIMARY").isUnique();
    //        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
    //        assert indexMap.get("proc_table_ref_uk").isUnique();
    //        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
    //        assert !indexMap.get("proc_table_ref_index").isUnique();
    //        assert indexMap.get("ptr").getColumns().size() == 2;
    //        assert indexMap.get("ptr").getColumns().get(0).equals("r_k1");
    //        assert indexMap.get("ptr").getColumns().get(1).equals("r_k2");
    //        assert !indexMap.get("ptr").isUnique();//MySQL JDBC 驱动无法识别联合索引的唯一特性
    //    }
    //
    //    @Test
    //    public void getIndexes4() throws SQLException {
    //        JdbcIndex index = this.repository.getIndexes(MYSQL_SCHEMA_NAME, null, "proc_table_ref", "ptr");
    //        assert index.getName().equals("ptr");
    //        assert index.getColumns().size() == 2;
    //        assert index.getColumns().get(0).equals("r_k1");
    //        assert index.getColumns().get(1).equals("r_k2");
    //        assert !index.isUnique();
    //    }
}