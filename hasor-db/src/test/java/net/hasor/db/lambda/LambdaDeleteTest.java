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
import net.hasor.db.lambda.LambdaOperations.LambdaDelete;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * Lambda 方式执行 Delete 操作
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaDeleteTest extends AbstractDbTest {
    @Test
    public void lambda_delete_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource);
            //
            LambdaDelete<TB_User> lambdaDelete = lambdaTemplate.lambdaDelete(TB_User.class);
            int delete = lambdaDelete.allowEmptyWhere().doDelete();
            assert delete == 3;
        }
    }

    @Test
    public void lambda_delete_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource);
            //
            LambdaDelete<TB_User> lambdaDelete = lambdaTemplate.lambdaDelete(TB_User.class);
            int delete = lambdaDelete.eq(TB_User::getLoginName, beanForData1().getLoginName()).doDelete();
            assert delete == 1;
            //
            List<TB_User> tbUsers = lambdaTemplate.lambdaQuery(TB_User.class).queryForList();
            assert tbUsers.size() == 2;
            List<String> collect = tbUsers.stream().map(TB_User::getUserUUID).collect(Collectors.toList());
            assert collect.contains(beanForData2().getUserUUID());
            assert collect.contains(beanForData3().getUserUUID());
        }
    }
}
