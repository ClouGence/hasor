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
package net.hasor.db.jdbc;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.TB_User;
import net.hasor.test.db.single.SingleDataSourceModule;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 批量Insert语句执行
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcBasicCURDTest extends AbstractDbTest {
    @Test
    public void insert_test_1() throws SQLException {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 3;
        //
        insertData_1(jdbcTemplate);
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 4;
        //
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙");
    }

    @Test
    public void insert_test_2() throws SQLException {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 3;
        //
        String batchInsert = "insert into TB_User values(:ID,:Name,:Account,:Pwd,:Email,:RegTime);";
        int count = 10;
        Map<String, Object>[] batchValues = new Map[count];
        for (int i = 0; i < count; i++) {
            batchValues[i] = new HashMap<>();
            batchValues[i].put("ID", UUID.randomUUID().toString());
            batchValues[i].put("Name", String.format("默认用户_%s", i));
            batchValues[i].put("Account", String.format("acc_%s", i));
            batchValues[i].put("Pwd", String.format("pwd_%s", i));
            batchValues[i].put("Email", String.format("autoUser_%s@hasor.net", i));
            batchValues[i].put("RegTime", new Date());
        }
        jdbcTemplate.executeBatch(batchInsert, batchValues);//批量执行执行插入语句
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 13;
    }

    @Test
    public void update_test_2() throws SQLException {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        assert jdbcTemplate.executeUpdate("update TB_User set name = '123'") == 3;
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
        assert collect.contains("123");
        assert collect.size() == 1;
    }
}