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
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.lambda.LambdaOperations.LambdaUpdate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static net.hasor.test.db.utils.TestUtils.beanForData1;

/***
 * Lambda 方式执行 Update 操作
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaUpdateTest extends AbstractDbTest {
    @Test
    public void lambda_update_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource);
            LambdaQuery<TB_User> lambdaQuery = lambdaTemplate.lambdaQuery(TB_User.class);
            List<TB_User> tbUsers1 = lambdaQuery.queryForList();
            assert tbUsers1.size() == 3;
            assert tbUsers1.get(0).getName() != null;
            assert tbUsers1.get(1).getName() != null;
            assert tbUsers1.get(2).getName() != null;
            //
            LambdaUpdate<TB_User> lambdaUpdate = lambdaTemplate.lambdaUpdate(TB_User.class);
            lambdaUpdate.updateTo(new HashMap<>(), "name");
            int update = lambdaUpdate.allowEmptyWhere().doUpdate();
            assert update == 3;
            //
            List<TB_User> tbUsers2 = lambdaQuery.queryForList();
            assert tbUsers2.size() == 3;
            assert tbUsers2.get(0).getName() == null;
            assert tbUsers2.get(1).getName() == null;
            assert tbUsers2.get(2).getName() == null;
        }
    }

    @Test
    public void lambda_update_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource);
            LambdaQuery<TB_User> lambdaQuery = lambdaTemplate.lambdaQuery(TB_User.class);
            TB_User tbUser1 = lambdaQuery.eq(TB_User::getLoginName, beanForData1().getLoginName()).queryForObject();
            assert tbUser1.getName() != null;
            //
            LambdaUpdate<TB_User> lambdaUpdate = lambdaTemplate.lambdaUpdate(TB_User.class);
            int update = lambdaUpdate.eq(TB_User::getLoginName, beanForData1().getLoginName())//
                    .updateTo(new HashMap<>(), "name")//
                    .doUpdate();
            assert update == 1;
            //
            TB_User tbUser2 = lambdaQuery.eq(TB_User::getLoginName, beanForData1().getLoginName()).queryForObject();
            assert tbUser2.getName() == null;
        }
    }
}
