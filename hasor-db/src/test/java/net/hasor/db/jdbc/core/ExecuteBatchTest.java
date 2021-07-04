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
import net.hasor.db.jdbc.BatchPreparedStatementSetter;
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.paramer.BeanSqlParameterSource;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * executeBatch 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ExecuteBatchTest extends AbstractDbTest {
    @Test
    public void executeUpdate_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            assert collect1.contains(TestUtils.beanForData1().getName());
            assert collect1.contains(TestUtils.beanForData2().getName());
            assert collect1.contains(TestUtils.beanForData3().getName());
            //
            String[] updateSql = new String[] {//
                    "update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + TestUtils.beanForData1().getUserUUID() + "'",//
                    "update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + TestUtils.beanForData2().getUserUUID() + "'",//
                    "update tb_user set name = CONCAT(name, '~' ) where userUUID = '" + TestUtils.beanForData3().getUserUUID() + "'",//
            };
            int[] ins = jdbcTemplate.executeBatch(updateSql);
            assert ins[0] == 1;
            assert ins[1] == 1;
            assert ins[2] == 1;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 3;
            assert !collect2.contains(TestUtils.beanForData1().getName());
            assert !collect2.contains(TestUtils.beanForData2().getName());
            assert !collect2.contains(TestUtils.beanForData3().getName());
            assert collect2.contains(TestUtils.beanForData1().getName() + "~");
            assert collect2.contains(TestUtils.beanForData2().getName() + "~");
            assert collect2.contains(TestUtils.beanForData3().getName() + "~");
        }
    }

    @Test
    public void executeUpdate_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            assert collect1.contains(TestUtils.beanForData1().getName());
            assert collect1.contains(TestUtils.beanForData2().getName());
            assert collect1.contains(TestUtils.beanForData3().getName());
            //
            Object[][] ids = new Object[][] {//
                    new Object[] { TestUtils.beanForData1().getUserUUID() },//
                    new Object[] { TestUtils.beanForData2().getUserUUID() },//
                    new Object[] { TestUtils.beanForData3().getUserUUID() } //
            };
            int[] ins = jdbcTemplate.executeBatch("update tb_user set name = CONCAT(name, '~' ) where userUUID = ?", ids);
            assert ins[0] == 1;
            assert ins[1] == 1;
            assert ins[2] == 1;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 3;
            assert !collect2.contains(TestUtils.beanForData1().getName());
            assert !collect2.contains(TestUtils.beanForData2().getName());
            assert !collect2.contains(TestUtils.beanForData3().getName());
            assert collect2.contains(TestUtils.beanForData1().getName() + "~");
            assert collect2.contains(TestUtils.beanForData2().getName() + "~");
            assert collect2.contains(TestUtils.beanForData3().getName() + "~");
        }
    }

    @Test
    public void executeUpdate_3() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            assert collect1.contains(TestUtils.beanForData1().getName());
            assert collect1.contains(TestUtils.beanForData2().getName());
            assert collect1.contains(TestUtils.beanForData3().getName());
            //
            Map<String, String>[] ids = new Map[] {//
                    Collections.singletonMap("uid", TestUtils.beanForData1().getUserUUID()),//
                    Collections.singletonMap("uid", TestUtils.beanForData2().getUserUUID()),//
                    Collections.singletonMap("uid", TestUtils.beanForData3().getUserUUID()) //
            };
            int[] ins = jdbcTemplate.executeBatch("update tb_user set name = CONCAT(name, '~' ) where userUUID = :uid", ids);
            assert ins[0] == 1;
            assert ins[1] == 1;
            assert ins[2] == 1;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 3;
            assert !collect2.contains(TestUtils.beanForData1().getName());
            assert !collect2.contains(TestUtils.beanForData2().getName());
            assert !collect2.contains(TestUtils.beanForData3().getName());
            assert collect2.contains(TestUtils.beanForData1().getName() + "~");
            assert collect2.contains(TestUtils.beanForData2().getName() + "~");
            assert collect2.contains(TestUtils.beanForData3().getName() + "~");
        }
    }

    @Test
    public void executeUpdate_4() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            assert collect1.contains(TestUtils.beanForData1().getName());
            assert collect1.contains(TestUtils.beanForData2().getName());
            assert collect1.contains(TestUtils.beanForData3().getName());
            //
            SqlParameterSource[] ids = new SqlParameterSource[] {//
                    new BeanSqlParameterSource(TestUtils.beanForData1()),//
                    new BeanSqlParameterSource(TestUtils.beanForData2()),//
                    new BeanSqlParameterSource(TestUtils.beanForData3()) //
            };
            int[] ins = jdbcTemplate.executeBatch("update tb_user set name = CONCAT(name, '~' ) where userUUID = :userUUID", ids);
            assert ins[0] == 1;
            assert ins[1] == 1;
            assert ins[2] == 1;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 3;
            assert !collect2.contains(TestUtils.beanForData1().getName());
            assert !collect2.contains(TestUtils.beanForData2().getName());
            assert !collect2.contains(TestUtils.beanForData3().getName());
            assert collect2.contains(TestUtils.beanForData1().getName() + "~");
            assert collect2.contains(TestUtils.beanForData2().getName() + "~");
            assert collect2.contains(TestUtils.beanForData3().getName() + "~");
        }
    }

    @Test
    public void executeUpdate_5() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            assert collect1.contains(TestUtils.beanForData1().getName());
            assert collect1.contains(TestUtils.beanForData2().getName());
            assert collect1.contains(TestUtils.beanForData3().getName());
            //
            SqlParameterSource[] ids = new SqlParameterSource[] {//
                    new BeanSqlParameterSource(TestUtils.beanForData1()),//
                    new BeanSqlParameterSource(TestUtils.beanForData2()),//
                    new BeanSqlParameterSource(TestUtils.beanForData3()) //
            };
            BatchPreparedStatementSetter bps = new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, ids[i].getValue("userUUID").toString());
                }

                public int getBatchSize() {
                    return 3;
                }
            };
            int[] ins = jdbcTemplate.executeBatch("update tb_user set name = CONCAT(name, '~' ) where userUUID = :userUUID", bps);
            assert ins[0] == 1;
            assert ins[1] == 1;
            assert ins[2] == 1;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 3;
            assert !collect2.contains(TestUtils.beanForData1().getName());
            assert !collect2.contains(TestUtils.beanForData2().getName());
            assert !collect2.contains(TestUtils.beanForData3().getName());
            assert collect2.contains(TestUtils.beanForData1().getName() + "~");
            assert collect2.contains(TestUtils.beanForData2().getName() + "~");
            assert collect2.contains(TestUtils.beanForData3().getName() + "~");
        }
    }
}
