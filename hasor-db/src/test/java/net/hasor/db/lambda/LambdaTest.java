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
import com.alibaba.fastjson.JSONObject;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.TB_User;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

/***
 * 批量Insert语句执行
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaTest extends AbstractDbTest {
    @Test
    public void insert_test_1() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        LambdaOperations.LambdaQuery<TB_User> lambdaQuery = jdbcTemplate.lambdaSelect(TB_User.class)//
                .select(TB_User::getLoginName, TB_User::getLoginPassword, TB_User::getEmail)//
                .andEq(TB_User::getLoginName, "zyc")//
                .andEq(TB_User::getLoginPassword, "123456");//
        String string = lambdaQuery.getSqlString();
        Map<String, Object> args = lambdaQuery.getArgs();
        System.out.println(string);
        System.out.println(JSONObject.toJSONString(args, true));
    }
}