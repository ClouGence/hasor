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
package net.hasor.db.jdbc.lambda;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * 批量Insert语句执行
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaTest extends AbstractDbTest {
    @Test
    public void lambda_select_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.lambdaSelect(TbUser.class).queryForList();
            //
            List<String> collect = tbUsers.stream().map(TbUser::getName).collect(Collectors.toList());
            //
            assert collect.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }

    @Test
    public void lambda_select_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers1 = jdbcTemplate.lambdaSelect(TbUser.class).selectAll().queryForList();
            List<TbUser> tbUsers2 = jdbcTemplate.lambdaSelect(TbUser.class).queryForList();
            assert tbUsers1.size() == 3;
            assert tbUsers2.size() == 3;
            //
            Map<String, Object> forData1 = mapForData1();
            List<TbUser> tbUsers3 = jdbcTemplate.lambdaSelect(TbUser.class)//
                    .eq(TbUser::getAccount, forData1.get("loginName")).queryForList();
            assert tbUsers3.size() == 1;
            assert tbUsers3.get(0).getAccount().equals("muhammad");
            assert tbUsers3.get(0).getAccount().equals(forData1.get("loginName"));
            assert tbUsers3.get(0).getUid().equals(forData1.get("userUUID"));
        }
    }

    @Test
    public void lambda_select_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TbUser tbUser = jdbcTemplate.lambdaSelect(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1").queryForObject();
            //
            assert tbUser.getName().equals("默罕默德");
        }
    }
}