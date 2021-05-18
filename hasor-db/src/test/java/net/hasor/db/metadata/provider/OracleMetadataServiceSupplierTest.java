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
import net.hasor.db.metadata.domain.oracle.*;
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
public class OracleMetadataServiceSupplierTest extends AbstractMetadataServiceSupplierTest<OracleMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localOracle();
    }

    @Override
    protected OracleMetadataProvider initRepository(Connection con) {
        return new OracleMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, OracleMetadataProvider repository) throws SQLException, IOException {
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
        assert columnMap.get("C_UROWID").getJdbcType() == JDBCType.ROWID;
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

    @Test
    public void getPrimaryKey1() throws SQLException {
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "PROC_TABLE_REF");
        assert primaryKey.getConstraintType() == OracleConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("SYS_");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("R_INT");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "PROC_TABLE");
        assert primaryKey.getConstraintType() == OracleConstraintType.PrimaryKey;
        assert primaryKey.getName().startsWith("SYS_");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("C_ID");
        assert primaryKey.getColumns().contains("C_NAME");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        OracleTable table = this.repository.getTable("SCOTT", "T3");
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "T3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "TB_USER");
        List<OracleUniqueKey> uniqueKeyList = this.repository.getUniqueKey("SCOTT", "TB_USER");
        Map<String, OracleUniqueKey> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(OracleUniqueKey::getName, u -> u));
        assert uniqueKeyMap.size() == 2;
        //
        assert uniqueKeyMap.containsKey(primaryKey.getName());
        assert uniqueKeyMap.get(primaryKey.getName()).getConstraintType() == OracleConstraintType.PrimaryKey;
        assert uniqueKeyMap.get(primaryKey.getName()).getColumns().size() == 1;
        assert uniqueKeyMap.get(primaryKey.getName()).getColumns().contains("USERUUID");
        //
        //        assert uniqueKeyMap.containsKey("TB_USER_USERUUID_UINDEX");
        //        assert uniqueKeyMap.get("TB_USER_USERUUID_UINDEX").getConstraintType() == OracleConstraintType.Unique;
        //        assert uniqueKeyMap.get("TB_USER_USERUUID_UINDEX").getColumns().size() == 1;
        //        assert uniqueKeyMap.get("TB_USER_USERUUID_UINDEX").getColumns().contains("USERUUID");
        //
        assert uniqueKeyMap.containsKey("TB_USER_EMAIL_USERUUID_UINDEX");
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getConstraintType() == OracleConstraintType.Unique;
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().size() == 2;
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().contains("USERUUID");
        assert uniqueKeyMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().contains("EMAIL");
    }

    @Test
    public void getForeignKey() throws SQLException {
        List<OracleForeignKey> foreignKeyList1 = this.repository.getForeignKey("SCOTT", "TB_USER");
        assert foreignKeyList1.size() == 0;
        List<OracleForeignKey> foreignKeyList2 = this.repository.getForeignKey("SCOTT", "PROC_TABLE_REF");
        assert foreignKeyList2.size() == 1;
        OracleForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getConstraintType() == OracleConstraintType.ForeignKey;
        assert foreignKey.getColumns().size() == 2;
        assert foreignKey.getColumns().get(0).equals("R_K2");
        assert foreignKey.getColumns().get(1).equals("R_K1");
        assert foreignKey.getName().equals("PTR");
        assert foreignKey.getReferenceSchema().equals("SCOTT");
        assert foreignKey.getReferenceTable().equals("PROC_TABLE");
        assert foreignKey.getReferenceMapping().get("R_K1").equals("C_NAME");
        assert foreignKey.getReferenceMapping().get("R_K2").equals("C_ID");
    }

    @Test
    public void getIndexes1() throws SQLException {
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "TB_USER");
        List<OracleIndex> indexList = this.repository.getIndexes("SCOTT", "TB_USER");
        Map<String, OracleIndex> indexMap = indexList.stream().collect(Collectors.toMap(OracleIndex::getName, i -> i));
        assert indexMap.size() == 3;
        //
        Set<String> indexNameSet = indexList.stream().map(OracleIndex::getName).collect(Collectors.toSet());
        assert indexNameSet.contains(primaryKey.getName());
        assert indexNameSet.contains("TB_USER_EMAIL_USERUUID_UINDEX");
        assert indexNameSet.contains("NORMAL_INDEX_TB_USER");
        assert indexMap.get(primaryKey.getName()).getColumns().size() == 1;
        assert indexMap.get(primaryKey.getName()).getColumns().get(0).equals("USERUUID");
        assert indexMap.get(primaryKey.getName()).isUnique();
        assert indexMap.get(primaryKey.getName()).isPrimaryKey();
        assert indexMap.get(primaryKey.getName()).getIndexType() == OracleIndexType.Normal;
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().size() == 2;
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().get(0).equals("EMAIL");
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getColumns().get(1).equals("USERUUID");
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").getIndexType() == OracleIndexType.Normal;
        assert indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").isUnique();
        assert !indexMap.get("TB_USER_EMAIL_USERUUID_UINDEX").isPrimaryKey();
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().size() == 2;
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().get(0).equals("LOGINPASSWORD");
        assert indexMap.get("NORMAL_INDEX_TB_USER").getColumns().get(1).equals("LOGINNAME");
        assert indexMap.get("NORMAL_INDEX_TB_USER").getIndexType() == OracleIndexType.Normal;
        assert !indexMap.get("NORMAL_INDEX_TB_USER").isUnique();
        assert !indexMap.get("NORMAL_INDEX_TB_USER").isPrimaryKey();
    }

    @Test
    public void getIndexes2() throws SQLException {
        OraclePrimaryKey primaryKey = this.repository.getPrimaryKey("SCOTT", "PROC_TABLE_REF");
        List<OracleIndex> indexList = this.repository.getIndexes("SCOTT", "PROC_TABLE_REF");
        Map<String, OracleIndex> indexMap = indexList.stream().collect(Collectors.toMap(OracleIndex::getName, i -> i));
        assert indexMap.size() == 3;
        assert indexMap.containsKey(primaryKey.getName());
        assert indexMap.containsKey("PROC_TABLE_REF_UK");
        assert indexMap.containsKey("PROC_TABLE_REF_INDEX");
        assert indexMap.get(primaryKey.getName()).getColumns().size() == 1;
        assert indexMap.get(primaryKey.getName()).getColumns().get(0).equals("R_INT");
        assert indexMap.get(primaryKey.getName()).getIndexType() == OracleIndexType.Normal;
        assert indexMap.get(primaryKey.getName()).isPrimaryKey();
        assert indexMap.get(primaryKey.getName()).isUnique();
        assert indexMap.get("PROC_TABLE_REF_UK").getColumns().size() == 1;
        assert indexMap.get("PROC_TABLE_REF_UK").getColumns().get(0).equals("R_NAME");
        assert indexMap.get("PROC_TABLE_REF_UK").getIndexType() == OracleIndexType.Normal;
        assert !indexMap.get("PROC_TABLE_REF_UK").isPrimaryKey();
        assert indexMap.get("PROC_TABLE_REF_UK").isUnique();
        assert indexMap.get("PROC_TABLE_REF_INDEX").getColumns().size() == 1;
        assert indexMap.get("PROC_TABLE_REF_INDEX").getColumns().get(0).equals("R_INDEX");
        assert indexMap.get("PROC_TABLE_REF_INDEX").getIndexType() == OracleIndexType.Normal;
        assert !indexMap.get("PROC_TABLE_REF_INDEX").isPrimaryKey();
        assert !indexMap.get("PROC_TABLE_REF_INDEX").isUnique();
    }

    @Test
    public void getIndexes3() throws SQLException {
        List<OracleIndex> indexList = this.repository.getIndexes("SCOTT", "PROC_TABLE_REF", OracleIndexType.Bitmap);
        Map<String, OracleIndex> indexMap = indexList.stream().collect(Collectors.toMap(OracleIndex::getName, i -> i));
        assert indexMap.size() == 0;
    }

    @Test
    public void getIndexes4() throws SQLException {
        OracleIndex index = this.repository.getIndexes("SCOTT", "PROC_TABLE_REF", "PROC_TABLE_REF_UK");
        assert index.getName().equals("PROC_TABLE_REF_UK");
        assert index.getColumns().size() == 1;
        assert index.getColumns().get(0).equals("R_NAME");
        assert index.isUnique();
        assert index.getIndexType() == OracleIndexType.Normal;
    }
}