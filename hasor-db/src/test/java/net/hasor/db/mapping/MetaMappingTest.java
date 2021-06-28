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
package net.hasor.db.mapping;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.mapping.resolve.MappingOptions;
import net.hasor.db.metadata.AbstractMetadataServiceSupplierTest;
import net.hasor.db.metadata.domain.mysql.MySqlSchema;
import net.hasor.db.metadata.provider.MySqlMetadataProvider;
import net.hasor.test.db.dto.T1;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.DsUtils.MYSQL_SCHEMA_NAME;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MetaMappingTest extends AbstractMetadataServiceSupplierTest<MySqlMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localMySQL();
    }

    @Override
    protected MySqlMetadataProvider initRepository(Connection con) {
        return new MySqlMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, MySqlMetadataProvider repository) throws SQLException, IOException {
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/mysql_script.sql");
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<MySqlSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(MySqlSchema::getName).collect(Collectors.toList());
        assert collect.contains("information_schema");
        assert collect.contains("mysql");
        assert collect.contains(MYSQL_SCHEMA_NAME);
    }

    @Test
    public void metaTest_02() throws Exception {
        MappingRegistry mappingRegistry = MappingRegistry.newInstance(this.repository);
        TableMapping tableMapping = mappingRegistry.loadMapping(T1.class, new MappingOptions());
        //
        assert tableMapping.getMapping("t1S1").getName().equals("t1_s1");
        assert tableMapping.getMapping("t1S2").getName().equals("t1_s2");
        assert tableMapping.getMapping("t1S3").getName().equals("t1_s3");
    }
}
