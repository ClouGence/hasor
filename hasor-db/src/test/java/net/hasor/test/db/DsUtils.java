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
package net.hasor.test.db;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.ColumnMapResultSetExtractor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class DsUtils {
    public static DruidDataSource createDs(String dbID) throws Throwable {
        DruidDataSource druid = new DruidDataSource();
        druid.setUrl("jdbc:h2:mem:test_" + dbID);
        druid.setDriverClassName("org.h2.Driver");
        druid.setUsername("sa");
        druid.setPassword("");
        druid.setMaxActive(5);
        druid.setMaxWait(3 * 1000);
        druid.setInitialSize(1);
        druid.setConnectionErrorRetryAttempts(1);
        druid.setBreakAfterAcquireFailure(true);
        druid.setTestOnBorrow(true);
        druid.setTestWhileIdle(true);
        druid.setFailFast(true);
        druid.init();
        return druid;
    }

    public static void initDB(JdbcTemplate jdbcTemplate) throws SQLException, IOException {
        // init table
        jdbcTemplate.execute((ConnectionCallback<Object>) con -> {
            ResultSet resultSet = con.getMetaData().getTables(null, "tb_user", null, null);
            List<Map<String, Object>> mapList = new ColumnMapResultSetExtractor().extractData(resultSet);
            if (!mapList.isEmpty()) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            return null;
        });
        jdbcTemplate.loadSQL("net_hasor_db/tb_user.sql");
    }
}