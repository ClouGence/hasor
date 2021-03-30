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
import net.hasor.db.dialect.BatchBoundSql;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.dialect.provider.MySqlDialect;
import net.hasor.db.lambda.LambdaOperations.LambdaDelete;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

/***
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class BuilderDeleteTest extends AbstractDbTest {
    @Test
    public void deleteBuilder_1() {
        try {
            LambdaDelete<TB_User> lambdaDelete = new LambdaTemplate().lambdaDelete(TB_User.class);
            SqlDialect dialect = new MySqlDialect();
            lambdaDelete.getBoundSql(dialect);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("The dangerous DELETE operation,");
        }
    }

    @Test
    public void deleteBuilder_2() {
        LambdaDelete<TB_User> lambdaDelete = new LambdaTemplate().lambdaDelete(TB_User.class);
        lambdaDelete.allowEmptyWhere();
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaDelete.getBoundSql(dialect);
        assert boundSql1.getSqlString().equals("DELETE FROM TB_User");
        //
        BoundSql boundSql2 = lambdaDelete.useQualifier().getBoundSql(dialect);
        assert boundSql2.getSqlString().equals("DELETE FROM `TB_User`");
    }

    @Test
    public void deleteBuilder_3() {
        LambdaDelete<TB_User> lambdaDelete = new LambdaTemplate().lambdaDelete(TB_User.class);
        lambdaDelete.and(queryBuilder -> {
            queryBuilder.eq(TB_User::getIndex, 123);
        });
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaDelete.getBoundSql(dialect);
        assert !(boundSql1 instanceof BatchBoundSql);
        assert boundSql1.getSqlString().equals("DELETE FROM TB_User WHERE ( index = ? )");
        //
        BoundSql boundSql2 = lambdaDelete.useQualifier().getBoundSql(dialect);
        assert !(boundSql2 instanceof BatchBoundSql);
        assert boundSql2.getSqlString().equals("DELETE FROM `TB_User` WHERE ( `index` = ? )");
    }

    @Test
    public void deleteBuilder_4() {
        LambdaDelete<TB_User> lambdaDelete = new LambdaTemplate().lambdaDelete(TB_User.class);
        lambdaDelete.eq(TB_User::getLoginName, "admin").and().eq(TB_User::getLoginPassword, "pass");
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaDelete.getBoundSql(dialect);
        assert boundSql1.getSqlString().equals("DELETE FROM TB_User WHERE loginName = ? AND loginPassword = ?");
        //
        BoundSql boundSql2 = lambdaDelete.useQualifier().getBoundSql(dialect);
        assert boundSql2.getSqlString().equals("DELETE FROM `TB_User` WHERE `loginName` = ? AND `loginPassword` = ?");
    }
}
