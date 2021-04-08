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
import net.hasor.db.lambda.LambdaOperations.LambdaUpdate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

/***
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class BuilderUpdateTest extends AbstractDbTest {
    @Test
    public void updateBuilder_1() {
        try {
            LambdaUpdate<TB_User> lambdaUpdate = new LambdaTemplate().lambdaUpdate(TB_User.class);
            SqlDialect dialect = new MySqlDialect();
            assert lambdaUpdate.getBoundSql(dialect) == null;
            lambdaUpdate.doUpdate();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("Nothing to update.");
        }
        //
        try {
            new LambdaTemplate().lambdaUpdate(TB_User.class).updateTo((TB_User) null);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("newValue is null.");
        }
        //
        try {
            UpdateExecute<TB_User> lambdaUpdate = new LambdaTemplate().lambdaUpdate(TB_User.class).updateTo(new TB_User());
            lambdaUpdate.doUpdate();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("The dangerous UPDATE operation,");
        }
    }

    @Test
    public void updateBuilder_2() {
        LambdaUpdate<TB_User> lambdaUpdate = new LambdaTemplate().lambdaUpdate(TB_User.class);
        lambdaUpdate.allowEmptyWhere();
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaUpdate.getBoundSql(dialect);
        assert boundSql1 == null;
        //
        BoundSql boundSql2 = lambdaUpdate.useQualifier().getBoundSql(dialect);
        assert boundSql2 == null;
    }

    @Test
    public void updateBuilder_3() {
        TB_User data = new TB_User();
        data.setLoginName("acc");
        data.setLoginPassword("pwd");
        //
        LambdaUpdate<TB_User> lambdaUpdate = new LambdaTemplate().lambdaUpdate(TB_User.class);
        lambdaUpdate.and(queryBuilder -> {
            queryBuilder.eq(TB_User::getIndex, 123);
        }).updateTo(data, TB_User::getLoginPassword, TB_User::getLoginName);
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaUpdate.getBoundSql(dialect);
        assert !(boundSql1 instanceof BatchBoundSql);
        assert boundSql1.getSqlString().equals("UPDATE TB_User SET loginName = ? , loginPassword = ? WHERE ( index = ? )");
        assert boundSql1.getArgs()[0].equals("acc");
        assert boundSql1.getArgs()[1].equals("pwd");
        assert boundSql1.getArgs()[2].equals(123);
        //
        BoundSql boundSql2 = lambdaUpdate.useQualifier().getBoundSql(dialect);
        assert !(boundSql2 instanceof BatchBoundSql);
        assert boundSql2.getSqlString().equals("UPDATE `TB_User` SET `loginName` = ? , `loginPassword` = ? WHERE ( `index` = ? )");
        assert boundSql2.getArgs()[0].equals("acc");
        assert boundSql2.getArgs()[1].equals("pwd");
        assert boundSql2.getArgs()[2].equals(123);
    }

    @Test
    public void updateBuilder_4() {
        TB_User data = new TB_User();
        data.setLoginName("acc");
        data.setLoginPassword("pwd");
        //
        LambdaUpdate<TB_User> lambdaUpdate = new LambdaTemplate().lambdaUpdate(TB_User.class);
        lambdaUpdate.eq(TB_User::getLoginName, "admin").and().eq(TB_User::getLoginPassword, "pass").updateTo(data);
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaUpdate.getBoundSql(dialect);
        assert boundSql1.getSqlString().equals("UPDATE TB_User SET registerTime = ? , loginName = ? , name = ? , loginPassword = ? , index = ? , userUUID = ? , email = ? WHERE loginName = ? AND loginPassword = ?");
        //
        BoundSql boundSql2 = lambdaUpdate.useQualifier().getBoundSql(dialect);
        assert boundSql2.getSqlString().equals("UPDATE `TB_User` SET `registerTime` = ? , `loginName` = ? , `name` = ? , `loginPassword` = ? , `index` = ? , `userUUID` = ? , `email` = ? WHERE `loginName` = ? AND `loginPassword` = ?");
    }
}
