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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
    //    @Test
    //    public void getSchemaTest() throws SQLException {
    //        JdbcSchema schema1 = this.repository.getSchema("abc");
    //        JdbcSchema schema2 = this.repository.getSchema("SYSTEM");
    //        assert schema1 == null;
    //        assert schema2 != null;
    //    }
    //
    //    @Test
    //    public void getTables() throws SQLException {
    //        Map<String, List<OracleTable>> tableList = this.repository.getTables(new String[] { "SCOTT", "SYSTEM" });
    //        assert tableList.size() == 2;
    //        assert tableList.containsKey("SCOTT");
    //        assert tableList.containsKey("SYSTEM");
    //        List<String> tableForInformationSchema = tableList.get("SCOTT").stream().map(OracleTable::getTable).collect(Collectors.toList());
    //        assert tableForInformationSchema.contains("PROC_TABLE_REF");
    //        assert tableForInformationSchema.contains("TB_USER");
    //        assert tableForInformationSchema.contains("T1");
    //        assert tableForInformationSchema.contains("T3");
    //        assert tableForInformationSchema.contains("TB_USER_VIEW");
    //        assert tableForInformationSchema.size() > 3;
    //    }
    //
    //    @Test
    //    public void findTables() throws SQLException {
    //        List<OracleTable> tableList = this.repository.findTable("SCOTT", new String[] { "PROC_TABLE_REF", "TB_USER_VIEW" });
    //        Map<String, OracleTable> tableMap = tableList.stream().collect(Collectors.toMap(OracleTable::getTable, o -> o));
    //        assert tableMap.size() == 2;
    //        assert tableMap.containsKey("PROC_TABLE_REF");
    //        assert tableMap.containsKey("TB_USER_VIEW");
    //        assert tableMap.get("PROC_TABLE_REF").getTableType() == OracleTableType.Table;
    //        assert tableMap.get("TB_USER_VIEW").getTableType() == OracleTableType.View;
    //    }
    //
    //    @Test
    //    public void getTable() throws SQLException {
    //        OracleTable tableObj1 = this.repository.getTable("SCOTT", "PROC_TABLE_REF");
    //        OracleTable tableObj2 = this.repository.getTable("SCOTT", "TB_USER_VIEW");
    //        OracleTable tableObj3 = this.repository.getTable("SCOTT", "abc");
    //        OracleTable tableObj4 = this.repository.getTable("SCOTT", "TB_USER");
    //        assert tableObj1 != null;
    //        assert tableObj1.getTableType() == OracleTableType.Table;
    //        assert tableObj1.getMaterializedLog() == null;
    //        assert tableObj2 != null;
    //        assert tableObj2.getTableType() == OracleTableType.View;
    //        assert tableObj2.getMaterializedLog() == null;
    //        assert tableObj3 == null;
    //        assert tableObj4 != null;
    //        assert tableObj4.getTableType() == OracleTableType.Table;
    //        assert tableObj4.getMaterializedLog() != null;
    //    }
    //
    //    @Test
    //    public void getColumns_1() throws SQLException {
    //        List<OracleColumn> columnList = this.repository.getColumns("SCOTT", "TB_ORACLE_TYPES");
    //        Map<String, OracleColumn> columnMap = columnList.stream().collect(Collectors.toMap(OracleColumn::getName, c -> c));
    //        //
    //        List<SqlType> collect1 = columnList.stream().map(OracleColumn::getSqlType).collect(Collectors.toList());
    //        List<JDBCType> collect2 = columnList.stream().map(OracleColumn::getJdbcType).filter(Objects::nonNull).collect(Collectors.toList());
    //        assert !collect1.contains(null);
    //        assert collect1.size() == columnList.size();
    //        assert collect2.size() < columnList.size();
    //        //
    //        assert columnMap.containsKey("C_DECIMAL");
    //        assert columnMap.containsKey("C_DATE");
    //        assert columnMap.containsKey("C_TIMESTAMP");
    //        assert columnMap.containsKey("C_NUMBER_N");
    //        assert columnMap.containsKey("C_NCLOB");
    //        assert columnMap.containsKey("C_VARCHAR2");
    //        assert columnMap.containsKey("C_NVARCHAR2");
    //        assert columnMap.containsKey("C_UROWID");
    //        assert columnMap.containsKey("C_INT");
    //        assert columnMap.containsKey("C_NUMBER");
    //        assert columnMap.containsKey("C_CHAR_NB");
    //        assert columnMap.containsKey("C_TIMESTAMP_N_Z");
    //        assert columnMap.containsKey("C_NATIONAL_CHARACTER_VARYING");
    //        //
    //        assert columnMap.get("C_DECIMAL").getJdbcType() == JDBCType.NUMERIC;
    //        assert columnMap.get("C_DATE").getJdbcType() == JDBCType.DATE;
    //        assert columnMap.get("C_TIMESTAMP").getJdbcType() == JDBCType.TIMESTAMP;
    //        assert columnMap.get("C_NUMBER_N").getJdbcType() == JDBCType.NUMERIC;
    //        assert columnMap.get("C_NCLOB").getJdbcType() == JDBCType.NCLOB;
    //        assert columnMap.get("C_VARCHAR2").getJdbcType() == JDBCType.VARCHAR;
    //        assert columnMap.get("C_NVARCHAR2").getJdbcType() == JDBCType.NVARCHAR;
    //        assert columnMap.get("C_UROWID").getJdbcType() == null;
    //        assert columnMap.get("C_INT").getJdbcType() == JDBCType.NUMERIC;
    //        assert columnMap.get("C_NUMBER").getJdbcType() == JDBCType.NUMERIC;
    //        assert columnMap.get("C_CHAR_NB").getJdbcType() == JDBCType.CHAR;
    //        assert columnMap.get("C_TIMESTAMP_N_Z").getJdbcType() == JDBCType.TIMESTAMP;
    //        assert columnMap.get("C_NATIONAL_CHARACTER_VARYING").getJdbcType() == JDBCType.NVARCHAR;
    //    }
    //
    //    @Test
    //    public void getColumns_2() throws SQLException {
    //        List<OracleColumn> columnList = this.repository.getColumns("SCOTT", "PROC_TABLE_REF");
    //        Map<String, OracleColumn> columnMap = columnList.stream().collect(Collectors.toMap(OracleColumn::getName, c -> c));
    //        assert columnMap.size() == 6;
    //        assert columnMap.get("R_INT").isPrimaryKey();
    //        assert !columnMap.get("R_INT").isUniqueKey();
    //        assert !columnMap.get("R_K1").isPrimaryKey();
    //        assert !columnMap.get("R_K1").isUniqueKey();
    //        assert !columnMap.get("R_K2").isPrimaryKey();
    //        assert !columnMap.get("R_K2").isUniqueKey();
    //        assert !columnMap.get("R_NAME").isPrimaryKey();
    //        assert columnMap.get("R_NAME").isUniqueKey();
    //        assert !columnMap.get("R_INDEX").isPrimaryKey();
    //        assert !columnMap.get("R_INDEX").isUniqueKey();
    //        assert !columnMap.get("R_DATA").isPrimaryKey();
    //        assert !columnMap.get("R_DATA").isUniqueKey();
    //    }
}