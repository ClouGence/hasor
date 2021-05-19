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
public class Jdbc4OracleMetadataServiceSupplierTest extends AbstractMetadataServiceSupplierTest<JdbcMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localOracle();
    }

    @Override
    protected JdbcMetadataProvider initRepository(Connection con) {
        return new JdbcMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, JdbcMetadataProvider repository) throws SQLException, IOException {
        applySql("drop view tb_user_view");
        applySql("drop materialized view log on tb_user");
        //
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        applySql("drop table tb_oracle_types");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/oracle_script.sql");
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/all_types/tb_oracle_types.sql");
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
        assert tableObj1.getTableType() == JdbcTableType.Table;
        assert tableObj2 == null;
        assert tableObj3 != null;
        assert tableObj3.getTableType() == JdbcTableType.Table;
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
        assert columnMap.get("C_UROWID").getJdbcType() == JDBCType.ROWID;
        assert columnMap.get("C_INT").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_NUMBER").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("C_CHAR_NB").getJdbcType() == JDBCType.CHAR;
        assert columnMap.get("C_TIMESTAMP_N_Z").getJdbcType() == null;
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

    @Test
    public void getConstraint1() throws SQLException {
        List<JdbcConstraint> columnList = this.repository.getConstraint(null, "SCOTT", "PROC_TABLE_REF");
        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
        Set<String> typeNameSet = columnList.stream().map(JdbcConstraint::getName).collect(Collectors.toSet());
        Set<JdbcConstraintType> typeEnumSet = columnList.stream().map(JdbcConstraint::getConstraintType).collect(Collectors.toSet());
        //
        assert typeMap.size() == 2;
        assert typeNameSet.stream().anyMatch(s -> s.startsWith("SYS_"));
        assert typeNameSet.contains("PTR");
        //
        assert typeMap.get("PTR") == JdbcConstraintType.ForeignKey;
        assert typeEnumSet.contains(JdbcConstraintType.PrimaryKey);
    }

    @Test
    public void getConstraint2() throws SQLException {
        List<JdbcConstraint> columnList = this.repository.getConstraint(null, "SCOTT", "PROC_TABLE_REF", JdbcConstraintType.ForeignKey);
        Map<String, JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(JdbcConstraint::getName, JdbcConstraint::getConstraintType));
        assert typeMap.size() == 1;
        assert typeMap.containsKey("PTR");
        assert typeMap.get("PTR") == JdbcConstraintType.ForeignKey;
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "SCOTT", "PROC_TABLE_REF");
        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("SYS_");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("R_INT");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "SCOTT", "PROC_TABLE");
        assert primaryKey.getConstraintType() == JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("SYS_");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("C_ID");
        assert primaryKey.getColumns().contains("C_NAME");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        JdbcTable table = this.repository.getTable(null, "SCOTT", "T3");
        JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "SCOTT", "T3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        List<JdbcIndex> uniqueKeyList = this.repository.getUniqueKey(null, "SCOTT", "TB_USER");
        JdbcIndex pkIndex = uniqueKeyList.stream().filter(s -> s.getName().startsWith("SYS_")).findFirst().orElse(null);
        Map<String, JdbcIndex> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(JdbcIndex::getName, u -> u));
        //
        assert uniqueKeyMap.size() == 2;
        assert pkIndex != null && pkIndex.isUnique();
        //
        assert uniqueKeyMap.containsKey("TB_USER_EMAIL_USERUUID_UINDEX");
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().size() == 2;
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().contains("USERUUID");
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().contains("EMAIL");
        assert uniqueKeyMap.get(pkIndex.getName()).getColumns().size() == 1;
        assert uniqueKeyMap.get(pkIndex.getName()).getColumns().contains("USERUUID");
    }

    @Test
    public void getForeignKey() throws SQLException {
        List<JdbcForeignKey> foreignKeyList1 = this.repository.getForeignKey(null, "SCOTT", "TB_USER");
        assert foreignKeyList1.size() == 0;
        List<JdbcForeignKey> foreignKeyList2 = this.repository.getForeignKey(null, "SCOTT", "PROC_TABLE_REF");
        assert foreignKeyList2.size() == 1;
        JdbcForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getConstraintType() == JdbcConstraintType.ForeignKey;
        assert foreignKey.getColumns().size() == 2;
        assert foreignKey.getColumns().get(0).equals("R_K2");
        assert foreignKey.getColumns().get(1).equals("R_K1");
        assert foreignKey.getName().equals("PTR");
        assert foreignKey.getReferenceCatalog() == null;
        assert foreignKey.getReferenceTable().equals("PROC_TABLE");
        assert foreignKey.getReferenceMapping().get("R_K1").equals("C_NAME");
        assert foreignKey.getReferenceMapping().get("R_K2").equals("C_ID");
    }

    @Test
    public void getIndexes1() throws SQLException {
        List<JdbcIndex> indexList = this.repository.getIndexes(null, "SCOTT", "TB_USER");
        JdbcIndex pkIndex = indexList.stream().filter(s -> s.getName().startsWith("SYS_")).findFirst().orElse(null);
        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
        assert pkIndex != null;
        assert indexMap.size() == 3;
        assert indexMap.containsKey(pkIndex.getName());
        assert indexMap.containsKey("TB_USER_EMAIL_USERUUID_UINDEX");
        assert indexMap.containsKey("NORMAL_INDEX_TB_USER");
        assert indexMap.get(pkIndex.getName()).getColumns().size() == 1;
        assert indexMap.get(pkIndex.getName()).getColumns().get(0).equals("USERUUID");
        assert indexMap.get(pkIndex.getName()).isUnique();
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().size() == 2;
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().get(0).equals("EMAIL");
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().get(1).equals("USERUUID");
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").isUnique();
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().size() == 2;
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().get(0).equals("LOGINPASSWORD");
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().get(1).equals("LOGINNAME");
        assert !indexMap.get("NORMAL_INDEX_TB_USER").isUnique();
    }

    @Test
    public void getIndexes2() throws SQLException {
        List<JdbcIndex> indexList = this.repository.getIndexes(null, "SCOTT", "PROC_TABLE_REF");
        JdbcIndex pkIndex = indexList.stream().filter(s -> s.getName().startsWith("SYS_")).findFirst().orElse(null);
        Map<String, JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(JdbcIndex::getName, i -> i));
        //
        assert pkIndex != null;
        assert indexMap.size() == 3;
        assert indexMap.containsKey(pkIndex.getName());
        assert indexMap.containsKey("PROC_TABLE_REF_UK");
        assert indexMap.containsKey("PROC_TABLE_REF_INDEX");
        assert indexMap.get(pkIndex.getName()).getColumns().size() == 1;
        assert indexMap.get(pkIndex.getName()).getColumns().get(0).equals("R_INT");
        assert indexMap.get(pkIndex.getName()).isUnique();
        assert indexMap.get("PROC_TABLE_REF_UK").getColumns().size() == 1;
        assert indexMap.get("PROC_TABLE_REF_UK").getColumns().get(0).equals("R_NAME");
        assert indexMap.get("PROC_TABLE_REF_UK").isUnique();
        assert indexMap.get("PROC_TABLE_REF_INDEX").getColumns().size() == 1;
        assert indexMap.get("PROC_TABLE_REF_INDEX").getColumns().get(0).equals("R_INDEX");
        assert !indexMap.get("PROC_TABLE_REF_INDEX").isUnique();
    }

    @Test
    public void getIndexes4() throws SQLException {
        JdbcIndex index = this.repository.getIndexes(null, "SCOTT", "PROC_TABLE_REF", "PTR");
        assert index == null;
    }
}