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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * executeBatch 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ExecuteBatchTest extends AbstractDbTest {
    //    public int[] executeBatch(String[] sql) throws SQLException;
    //    public int[] executeBatch(String sql, BatchPreparedStatementSetter pss) throws SQLException;
    //    public int[] executeBatch(String sql, Map<String, ?>[] batchValues) throws SQLException;
    //    public int[] executeBatch(String sql, Object[][] batchValues) throws SQLException;
    //    public int[] executeBatch(String sql, SqlParameterSource[] batchArgs) throws SQLException;
    //
    @Test
    public void executeUpdate_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TB_User> tbUsers1 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect1 = tbUsers1.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect1.size() == 3;
            //
            assert jdbcTemplate.executeUpdate(con -> {
                return con.prepareStatement("update tb_user set name = ?");
            }, ps -> {
                ps.setString(1, "123");
            }) == 3;
            //
            List<TB_User> tbUsers2 = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            Set<String> collect2 = tbUsers2.stream().map(TB_User::getName).collect(Collectors.toSet());
            assert collect2.size() == 1;
            assert collect2.contains("123");
        }
    }
}