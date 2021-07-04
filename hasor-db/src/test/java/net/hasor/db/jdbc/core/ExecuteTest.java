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
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.StatementCallback;
import net.hasor.db.jdbc.paramer.MapSqlParameterSource;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.beanForData1;

/***
 * execute 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ExecuteTest extends AbstractDbTest {
    @Test
    public void execute_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.execute((ConnectionCallback<Boolean>) con -> {
                return con.createStatement().execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + beanForData1().getUserUUID() + "'");
            });
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.execute((StatementCallback<Boolean>) s -> {
                return s.execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + beanForData1().getUserUUID() + "'");
            });
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_3() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.execute(con -> {
                return con.prepareCall("update tb_user set name = CONCAT(name, '~' ) where userUUID = ?");
            }, ps -> {
                ps.setString(1, beanForData1().getUserUUID());
                return ps.execute();
            });
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_4() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = ?", ps -> {
                ps.setString(1, beanForData1().getUserUUID());
                return ps.execute();
            });
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_5() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + beanForData1().getUserUUID() + "'");
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_6() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            Map<String, String> dat = Collections.singletonMap("uuid", beanForData1().getUserUUID());
            jdbcTemplate.execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = :uuid", dat, PreparedStatement::execute);
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }

    @Test
    public void execute_7() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            Map<String, String> dat = Collections.singletonMap("uuid", beanForData1().getUserUUID());
            jdbcTemplate.execute("update tb_user set name = CONCAT(name, '~' ) where userUUID = :uuid", new MapSqlParameterSource(dat), PreparedStatement::execute);
            //
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TB_User.class, beanForData1().getUserUUID());
            Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect.size() == 1;
            assert collect.contains(beanForData1().getName() + "~");
        }
    }
}
