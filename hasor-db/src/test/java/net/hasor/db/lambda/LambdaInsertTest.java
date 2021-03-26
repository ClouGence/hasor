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
package net.hasor.db.lambda;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.LambdaOperations.LambdaInsert;
import net.hasor.db.lambda.dialect.BatchBoundSql;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUserShadow;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * Lambda 方式执行 Insert 操作
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaInsertTest extends AbstractDbTest {
    @Test
    public void lambda_insert_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            jdbcTemplate.execute("delete from tb_user");
            //
            LambdaInsert<TB_User> lambdaInsert = jdbcTemplate.lambdaInsert(TB_User.class);
            lambdaInsert.applyEntity(beanForData1());
            lambdaInsert.applyMap(mapForData2());
            assert lambdaInsert.getBoundSql() instanceof BatchBoundSql;
            //
            int i = lambdaInsert.executeSumResult();
            assert i == 2;
            //
            List<TB_User> tbUsers = jdbcTemplate.lambdaQuery(TB_User.class).queryForList();
            assert tbUsers.size() == 2;
            List<String> ids = tbUsers.stream().map(TB_User::getUserUUID).collect(Collectors.toList());
            assert ids.contains(beanForData1().getUserUUID());
            assert ids.contains(beanForData2().getUserUUID());
        }
    }

    @Test
    public void lambda_insert_2() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            jdbcTemplate.loadSQL("net_hasor_db/tb_user_shadow_for_h2.sql");
            //
            LambdaInsert<TbUserShadow> lambdaInsert = jdbcTemplate.lambdaInsert(TbUserShadow.class);
            lambdaInsert.applyQueryAsInsert(jdbcTemplate.lambdaQuery(TB_User.class));
            //
            assert !(lambdaInsert.getBoundSql() instanceof BatchBoundSql);
            //
            int i = lambdaInsert.executeSumResult();
            assert i == 3;
            //
            List<TbUserShadow> tbUsers = jdbcTemplate.lambdaQuery(TbUserShadow.class).queryForList();
            assert tbUsers.size() == 3;
            List<String> ids = tbUsers.stream().map(TbUserShadow::getUserUUID).collect(Collectors.toList());
            assert ids.contains(beanForData1().getUserUUID());
            assert ids.contains(beanForData2().getUserUUID());
            assert ids.contains(beanForData3().getUserUUID());
        }
    }
}
