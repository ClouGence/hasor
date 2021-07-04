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
package net.hasor.db.datasource;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerTest extends AbstractDbTest {
    @Test
    public void manager_basic_test_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            Connection connection = DataSourceManager.newConnection(dataSource);
            //
            JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
            int executeUpdate = jdbcTemplate.queryForInt("select count(1) from tb_user");
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            //
            assert executeUpdate == 3;
            assert tbUsers.size() == 3;
            List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
            assert collect.contains("默罕默德");
            assert collect.contains("安妮.贝隆");
            assert collect.contains("赵飞燕");
            //
            connection.close();
        }
    }
}
