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
package net.hasor.db.jdbc.core;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.extractor.ColumnMapResultSetExtractor;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.utils.ResourcesUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/***
 * execute 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class LoadTest extends AbstractDbTest {
    private boolean hasTable(JdbcTemplate jdbcTemplate) throws SQLException {

        List<Map<String, Object>> mapList = jdbcTemplate.execute((ConnectionCallback<List<Map<String, Object>>>) con -> {
            try (ResultSet tbUser = con.getMetaData().getTables(null, null, "tb_user", new String[] { "TABLE" })) {
                return new ColumnMapResultSetExtractor().extractData(tbUser);
            }
        });
        return !mapList.isEmpty();
    }

    @Test
    public void loadSQL_1() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            jdbcTemplate.loadSQL("/net_hasor_db/tb_user_for_mysql.sql");
            assert hasTable(jdbcTemplate);
        }
    }

    @Test
    public void loadSQL_2() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            InputStream asStream = ResourcesUtils.getResourceAsStream("/net_hasor_db/tb_user_for_mysql.sql");
            if (asStream == null) {
                assert false;
            }
            jdbcTemplate.loadSQL(new InputStreamReader(asStream));
            assert hasTable(jdbcTemplate);
        }
    }

    @Test
    public void loadSQL_3() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            jdbcTemplate.loadSQL(StandardCharsets.UTF_8, "/net_hasor_db/tb_user_for_mysql.sql");
            assert hasTable(jdbcTemplate);
        }
    }

    @Test
    public void loadSplitSQL_1() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            jdbcTemplate.loadSplitSQL(";", "/net_hasor_db/tb_user_for_mysql.sql");
            assert hasTable(jdbcTemplate);
        }
    }

    @Test
    public void loadSplitSQL_2() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/tb_user_for_mysql.sql");
            assert hasTable(jdbcTemplate);
        }
    }

    @Test
    public void loadSplitSQL_3() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            if (hasTable(jdbcTemplate)) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            //
            assert !hasTable(jdbcTemplate);
            InputStream asStream = ResourcesUtils.getResourceAsStream("/net_hasor_db/tb_user_for_mysql.sql");
            if (asStream == null) {
                assert false;
            }
            jdbcTemplate.loadSplitSQL(";", new InputStreamReader(asStream));
            assert hasTable(jdbcTemplate);
        }
    }
}
