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
import net.hasor.db.metadata.domain.adb.mysql.*;
import net.hasor.db.metadata.provider.AdbMySqlMetadataProvider;
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

import static net.hasor.test.db.utils.DsUtils.ADBMYSQL_SCHEMA_NAME;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AdbMySqlMetadataServiceSupplierTest {
    private Connection               connection;
    private AdbMySqlMetadataProvider repository;

    @Before
    public void beforeTest() throws SQLException, IOException {
        this.connection = DsUtils.aliyunAdbMySQL();
        this.repository = new AdbMySqlMetadataProvider(this.connection);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.connection);
        //
        List<AdbMySqlTable> allTables = this.repository.getAllTables();
        if (!allTables.isEmpty()) {
            List<String> collect = allTables.stream().map(AdbMySqlTable::getTable).collect(Collectors.toList());
            //
            if (collect.contains("proc_table")) {
                jdbcTemplate.execute("drop table proc_table");
            }
            if (collect.contains("proc_table_ref")) {
                jdbcTemplate.execute("drop table proc_table_ref");
            }
            if (collect.contains("t1")) {
                jdbcTemplate.execute("drop table t1");
            }
            if (collect.contains("t3")) {
                jdbcTemplate.execute("drop table t3");
            }
            if (collect.contains("tb_user")) {
                jdbcTemplate.execute("drop table tb_user");
            }
            if (collect.contains("m_tb_user")) {
                jdbcTemplate.execute("drop materialized view m_tb_user");
            }
        }
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/adbmysql_script.sql");
    }

    @After
    public void afterTest() throws SQLException {
        this.connection.close();
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<AdbMySqlSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(AdbMySqlSchema::getName).collect(Collectors.toList());
        assert collect.contains("INFORMATION_SCHEMA");
        assert collect.contains(ADBMYSQL_SCHEMA_NAME);
    }

    @Test
    public void getSchemaTest() throws SQLException {
        AdbMySqlSchema schema1 = this.repository.getSchema("abc");
        AdbMySqlSchema schema2 = this.repository.getSchema(ADBMYSQL_SCHEMA_NAME);
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        Map<String, List<AdbMySqlTable>> tableList = this.repository.getTables(new String[] { "INFORMATION_SCHEMA" });
        assert tableList.size() == 1;
        assert tableList.containsKey("INFORMATION_SCHEMA");
        List<String> tableForInformationSchema = tableList.get("INFORMATION_SCHEMA").stream().map(AdbMySqlTable::getTable).collect(Collectors.toList());
        assert tableForInformationSchema.contains("COLUMNS");
        assert tableForInformationSchema.contains("TABLES");
        assert tableForInformationSchema.contains("SCHEMATA");
        assert tableForInformationSchema.size() > 3;
    }

    @Test
    public void getMaterializedView() throws SQLException {
        List<AdbMySqlTable> tableList = this.repository.findTable(ADBMYSQL_SCHEMA_NAME, new String[] { "m_tb_user" });
        assert tableList.size() == 1;
        assert tableList.get(0) instanceof AdbMySqlMaterialized;
        assert tableList.get(0).getTableType() == AdbMySqlTableType.Materialized;
    }

    @Test
    public void findTables() throws SQLException {
        List<AdbMySqlTable> tableList = this.repository.findTable("INFORMATION_SCHEMA", new String[] { "COLUMNS", "TABLES", "SCHEMATA", "ABC" });
        List<String> tableNames = tableList.stream().map(AdbMySqlTable::getTable).collect(Collectors.toList());
        assert tableNames.size() == 3;
        assert tableNames.contains("COLUMNS");
        assert tableNames.contains("TABLES");
        assert tableNames.contains("SCHEMATA");
    }

    @Test
    public void getTable() throws SQLException {
        AdbMySqlTable tableObj1 = this.repository.getTable("INFORMATION_SCHEMA", "COLUMNS");
        AdbMySqlTable tableObj2 = this.repository.getTable("INFORMATION_SCHEMA", "ABC");
        AdbMySqlTable tableObj3 = this.repository.getTable(ADBMYSQL_SCHEMA_NAME, "t3");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == AdbMySqlTableType.SystemView;
        assert tableObj2 == null;
        assert tableObj3 != null;
        assert tableObj3.getTableType() == AdbMySqlTableType.Table;
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<AdbMySqlColumn> columnList = this.repository.getColumns("INFORMATION_SCHEMA", "COLUMNS");
        Map<String, AdbMySqlColumn> columnMap = columnList.stream().collect(Collectors.toMap(AdbMySqlColumn::getName, c -> c));
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
        assert columnMap.get("TABLE_NAME").getSqlType() == AdbMySqlTypes.VARCHAR;
    }

    @Test
    public void getColumns_2() throws SQLException {
        List<AdbMySqlColumn> columnList = this.repository.getColumns(ADBMYSQL_SCHEMA_NAME, "proc_table_ref");
        Map<String, AdbMySqlColumn> columnMap = columnList.stream().collect(Collectors.toMap(AdbMySqlColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("r_int").isPrimaryKey();
        assert !columnMap.get("r_k1").isPrimaryKey();
        assert !columnMap.get("r_k2").isPrimaryKey();
        assert !columnMap.get("r_name").isPrimaryKey();
        assert !columnMap.get("r_index").isPrimaryKey();
        assert !columnMap.get("r_data").isPrimaryKey();
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        AdbMySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(ADBMYSQL_SCHEMA_NAME, "proc_table_ref");
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        AdbMySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(ADBMYSQL_SCHEMA_NAME, "proc_table");
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("c_id");
        assert primaryKey.getColumns().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        AdbMySqlTable table = this.repository.getTable(ADBMYSQL_SCHEMA_NAME, "t3");
        AdbMySqlPrimaryKey primaryKey = this.repository.getPrimaryKey(ADBMYSQL_SCHEMA_NAME, "t3");
        assert table != null;
        assert primaryKey != null;
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumns().get(0).equals("__adb_auto_id__");
    }
}


