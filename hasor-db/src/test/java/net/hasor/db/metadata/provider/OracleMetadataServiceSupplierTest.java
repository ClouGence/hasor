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
import net.hasor.db.metadata.SqlType;
import net.hasor.db.metadata.domain.oracle.*;
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
import java.util.Set;
import java.util.stream.Collectors;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleMetadataServiceSupplierTest {
    private Connection             connection;
    private OracleMetadataProvider repository;

    @Before
    public void beforeTest() throws SQLException, IOException {
        this.connection = DsUtils.localOracle();
        this.repository = new OracleMetadataProvider(this.connection);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.connection);
        //
        List<OracleTable> allTables = this.repository.getAllTables();
        if (!allTables.isEmpty()) {
            Map<String, OracleTable> collect = allTables.stream().collect(Collectors.toMap(OracleTable::getTable, o -> o));
            //
            if (collect.containsKey("TB_USER_VIEW")) {
                jdbcTemplate.execute("drop view tb_user_view");
            }
            if (collect.containsKey("TB_USER")) {
                if (collect.get("TB_USER").getMaterializedLog() != null) {
                    jdbcTemplate.execute("drop materialized view log on tb_user");
                }
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
        List<OracleSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(OracleSchema::getSchema).collect(Collectors.toList());
        assert collect.contains("SYSTEM");
        assert collect.contains("SYS");
        assert collect.contains("SCOTT");
    }

    @Test
    public void getSchemaTest() throws SQLException {
        OracleSchema schema1 = this.repository.getSchema("abc");
        OracleSchema schema2 = this.repository.getSchema("SYSTEM");
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        Map<String, List<OracleTable>> tableList = this.repository.getTables(new String[] { "SCOTT", "SYSTEM" });
        assert tableList.size() == 2;
        assert tableList.containsKey("SCOTT");
        assert tableList.containsKey("SYSTEM");
        List<String> tableForInformationSchema = tableList.get("SCOTT").stream().map(OracleTable::getTable).collect(Collectors.toList());
        assert tableForInformationSchema.contains("PROC_TABLE_REF");
        assert tableForInformationSchema.contains("TB_USER");
        assert tableForInformationSchema.contains("T1");
        assert tableForInformationSchema.contains("T3");
        assert tableForInformationSchema.contains("TB_USER_VIEW");
        assert tableForInformationSchema.size() > 3;
    }

    @Test
    public void findTables() throws SQLException {
        List<OracleTable> tableList = this.repository.findTable("SCOTT", new String[] { "PROC_TABLE_REF", "TB_USER_VIEW" });
        Map<String, OracleTable> tableMap = tableList.stream().collect(Collectors.toMap(OracleTable::getTable, o -> o));
        assert tableMap.size() == 2;
        assert tableMap.containsKey("PROC_TABLE_REF");
        assert tableMap.containsKey("TB_USER_VIEW");
        assert tableMap.get("PROC_TABLE_REF").getTableType() == OracleTableType.Table;
        assert tableMap.get("TB_USER_VIEW").getTableType() == OracleTableType.View;
    }

    @Test
    public void getTable() throws SQLException {
        OracleTable tableObj1 = this.repository.getTable("SCOTT", "PROC_TABLE_REF");
        OracleTable tableObj2 = this.repository.getTable("SCOTT", "TB_USER_VIEW");
        OracleTable tableObj3 = this.repository.getTable("SCOTT", "abc");
        OracleTable tableObj4 = this.repository.getTable("SCOTT", "TB_USER");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == OracleTableType.Table;
        assert tableObj1.getMaterializedLog() == null;
        assert tableObj2 != null;
        assert tableObj2.getTableType() == OracleTableType.View;
        assert tableObj2.getMaterializedLog() == null;
        assert tableObj3 == null;
        assert tableObj4 != null;
        assert tableObj4.getTableType() == OracleTableType.Table;
        assert tableObj4.getMaterializedLog() != null;
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<OracleColumn> columnList = this.repository.getColumns("SCOTT", "TB_ORACLE_TYPES");
        Map<String, OracleColumn> columnMap = columnList.stream().collect(Collectors.toMap(OracleColumn::getName, c -> c));
        //
        List<SqlType> collect1 = columnList.stream().map(OracleColumn::getSqlType).collect(Collectors.toList());
        List<JDBCType> collect2 = columnList.stream().map(OracleColumn::getJdbcType).filter(Objects::nonNull).collect(Collectors.toList());
        assert !collect1.contains(null);
        assert collect1.size() == columnList.size();
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
        assert columnMap.get("C_DATE").getJdbcType() == JDBCType.DATE;
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
        List<OracleColumn> columnList = this.repository.getColumns("SCOTT", "PROC_TABLE_REF");
        Map<String, OracleColumn> columnMap = columnList.stream().collect(Collectors.toMap(OracleColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("R_INT").isPrimaryKey();
        assert !columnMap.get("R_INT").isUniqueKey();
        assert !columnMap.get("R_K1").isPrimaryKey();
        assert !columnMap.get("R_K1").isUniqueKey();
        assert !columnMap.get("R_K2").isPrimaryKey();
        assert !columnMap.get("R_K2").isUniqueKey();
        assert !columnMap.get("R_NAME").isPrimaryKey();
        assert columnMap.get("R_NAME").isUniqueKey();
        assert !columnMap.get("R_INDEX").isPrimaryKey();
        assert !columnMap.get("R_INDEX").isUniqueKey();
        assert !columnMap.get("R_DATA").isPrimaryKey();
        assert !columnMap.get("R_DATA").isUniqueKey();
    }

    @Test
    public void getConstraint1() throws SQLException {
        List<OracleConstraint> columnList = this.repository.getConstraint("SCOTT", "PROC_TABLE_REF");
        Map<String, OracleConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(OracleConstraint::getName, OracleConstraint::getConstraintType));
        Set<String> typeNameSet = columnList.stream().map(OracleConstraint::getName).collect(Collectors.toSet());
        Set<OracleConstraintType> typeEnumSet = columnList.stream().map(OracleConstraint::getConstraintType).collect(Collectors.toSet());
        //
        assert typeMap.size() == 4;
        assert typeNameSet.contains("PROC_TABLE_REF_UK");
        assert typeNameSet.contains("PTR");
        assert typeNameSet.stream().anyMatch(s -> s.startsWith("SYS_"));
        //
        assert typeMap.get("PROC_TABLE_REF_UK") == OracleConstraintType.Unique;
        assert typeMap.get("PTR") == OracleConstraintType.ForeignKey;
        assert typeEnumSet.contains(OracleConstraintType.PrimaryKey);
    }

    @Test
    public void getConstraint2() throws SQLException {
        List<OracleConstraint> columnList = this.repository.getConstraint("SCOTT", "PROC_TABLE_REF", OracleConstraintType.Unique);
        Map<String, OracleConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(OracleConstraint::getName, OracleConstraint::getConstraintType));
        Set<OracleConstraintType> typeEnumSet = columnList.stream().map(OracleConstraint::getConstraintType).collect(Collectors.toSet());
        //
        assert typeMap.size() == 1;
        assert !typeEnumSet.contains(OracleConstraintType.PrimaryKey);
        assert typeMap.containsKey("PROC_TABLE_REF_UK");
        assert !typeMap.containsKey("PTR");
        assert typeMap.get("PROC_TABLE_REF_UK") == OracleConstraintType.Unique;
    }
    //    @Test
    //    public void getPrimaryKey1() throws SQLException {
    //        MySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, "proc_table_ref");
    //        assert primaryKey.getConstraintType() == MySqlConstraintType.PrimaryKey;
    //        assert primaryKey.getName().equals("PRIMARY");
    //        assert primaryKey.getColumns().size() == 1;
    //        assert primaryKey.getColumns().contains("r_int");
    //    }
    //
    //    @Test
    //    public void getPrimaryKey2() throws SQLException {
    //        MySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, "proc_table");
    //        assert primaryKey.getConstraintType() == MySqlConstraintType.PrimaryKey;
    //        assert primaryKey.getName().equals("PRIMARY");
    //        assert primaryKey.getColumns().size() == 2;
    //        assert primaryKey.getColumns().contains("c_id");
    //        assert primaryKey.getColumns().contains("c_name");
    //    }
    //
    //    @Test
    //    public void getPrimaryKey3() throws SQLException {
    //        MySqlTable table = this.repository.getTable(MYSQL_SCHEMA_NAME, "t3");
    //        MySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(MYSQL_SCHEMA_NAME, "t3");
    //        assert table != null;
    //        assert primaryKey == null;
    //    }
    //
    //    @Test
    //    public void getUniqueKey() throws SQLException {
    //        List<MySqlUniqueKey> uniqueKeyList = this.repository.getUniqueKey(MYSQL_SCHEMA_NAME, "tb_user");
    //        Map<String, MySqlUniqueKey> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(MySqlUniqueKey::getName, u -> u));
    //        assert uniqueKeyMap.size() == 2;
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
    //        List<MySqlForeignKey> foreignKeyList1 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, "tb_user");
    //        assert foreignKeyList1.size() == 0;
    //        List<MySqlForeignKey> foreignKeyList2 = this.repository.getForeignKey(MYSQL_SCHEMA_NAME, "proc_table_ref");
    //        assert foreignKeyList2.size() == 1;
    //        MySqlForeignKey foreignKey = foreignKeyList2.get(0);
    //        assert foreignKey.getConstraintType() == MySqlConstraintType.ForeignKey;
    //        assert foreignKey.getFkColumn().size() == 2;
    //        assert foreignKey.getFkColumn().get(0).equals("r_k1");
    //        assert foreignKey.getFkColumn().get(1).equals("r_k2");
    //        assert foreignKey.getName().equals("ptr");
    //        assert foreignKey.getReferenceSchema().equals(MYSQL_SCHEMA_NAME);
    //        assert foreignKey.getReferenceTable().equals("proc_table");
    //        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_id");
    //        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_name");
    //    }
    //
    //    @Test
    //    public void getIndexes1() throws SQLException {
    //        List<MySqlIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, "tb_user");
    //        Map<String, MySqlIndex> indexMap = indexList.stream().collect(Collectors.toMap(MySqlIndex::getName, i -> i));
    //        assert indexMap.size() == 4;
    //        assert indexMap.containsKey("PRIMARY");
    //        assert indexMap.containsKey("tb_user_userUUID_uindex");
    //        assert indexMap.containsKey("tb_user_email_userUUID_uindex");
    //        assert indexMap.containsKey("normal_index_tb_user");
    //        assert indexMap.get("PRIMARY").getColumns().size() == 1;
    //        assert indexMap.get("PRIMARY").getColumns().get(0).equals("userUUID");
    //        assert indexMap.get("PRIMARY").getIndexType() == MySqlIndexType.Primary;
    //        assert indexMap.get("tb_user_userUUID_uindex").getColumns().size() == 1;
    //        assert indexMap.get("tb_user_userUUID_uindex").getColumns().get(0).equals("userUUID");
    //        assert indexMap.get("tb_user_userUUID_uindex").getIndexType() == MySqlIndexType.Unique;
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().size() == 2;
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(0).equals("email");
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getColumns().get(1).equals("userUUID");
    //        assert indexMap.get("tb_user_email_userUUID_uindex").getIndexType() == MySqlIndexType.Unique;
    //        assert indexMap.get("normal_index_tb_user").getColumns().size() == 2;
    //        assert indexMap.get("normal_index_tb_user").getColumns().get(0).equals("loginPassword");
    //        assert indexMap.get("normal_index_tb_user").getColumns().get(1).equals("loginName");
    //        assert indexMap.get("normal_index_tb_user").getIndexType() == MySqlIndexType.Normal;
    //    }
    //
    //    @Test
    //    public void getIndexes2() throws SQLException {
    //        List<MySqlIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, "proc_table_ref");
    //        Map<String, MySqlIndex> indexMap = indexList.stream().collect(Collectors.toMap(MySqlIndex::getName, i -> i));
    //        assert indexMap.size() == 4;
    //        assert indexMap.containsKey("PRIMARY");
    //        assert indexMap.containsKey("proc_table_ref_uk");
    //        assert indexMap.containsKey("proc_table_ref_index");
    //        assert indexMap.containsKey("ptr");
    //        assert indexMap.get("PRIMARY").getColumns().size() == 1;
    //        assert indexMap.get("PRIMARY").getColumns().get(0).equals("r_int");
    //        assert indexMap.get("PRIMARY").getIndexType() == MySqlIndexType.Primary;
    //        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
    //        assert indexMap.get("proc_table_ref_uk").getIndexType() == MySqlIndexType.Unique;
    //        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
    //        assert indexMap.get("proc_table_ref_index").getIndexType() == MySqlIndexType.Normal;
    //        assert indexMap.get("ptr").getColumns().size() == 2;
    //        assert indexMap.get("ptr").getColumns().get(0).equals("r_k1");
    //        assert indexMap.get("ptr").getColumns().get(1).equals("r_k2");
    //        assert indexMap.get("ptr").getIndexType() == MySqlIndexType.Foreign;
    //    }
    //
    //    @Test
    //    public void getIndexes3() throws SQLException {
    //        List<MySqlIndex> indexList = this.repository.getIndexes(MYSQL_SCHEMA_NAME, "proc_table_ref", MySqlIndexType.Normal, MySqlIndexType.Unique);
    //        Map<String, MySqlIndex> indexMap = indexList.stream().collect(Collectors.toMap(MySqlIndex::getName, i -> i));
    //        assert indexMap.size() == 2;
    //        assert indexMap.containsKey("proc_table_ref_uk");
    //        assert indexMap.containsKey("proc_table_ref_index");
    //        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
    //        assert indexMap.get("proc_table_ref_uk").getIndexType() == MySqlIndexType.Unique;
    //        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
    //        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
    //        assert indexMap.get("proc_table_ref_index").getIndexType() == MySqlIndexType.Normal;
    //    }
    //
    //    @Test
    //    public void getIndexes4() throws SQLException {
    //        MySqlIndex index = this.repository.getIndexes(MYSQL_SCHEMA_NAME, "proc_table_ref", "ptr");
    //        assert index.getName().equals("ptr");
    //        assert index.getColumns().size() == 2;
    //        assert index.getColumns().get(0).equals("r_k1");
    //        assert index.getColumns().get(1).equals("r_k2");
    //        assert index.getIndexType() == MySqlIndexType.Foreign;
    //    }
}