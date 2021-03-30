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
import net.hasor.db.lambda.LambdaOperations.LambdaInsert;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUserShadow;
import org.junit.Test;

import static net.hasor.test.db.utils.TestUtils.beanForData1;
import static net.hasor.test.db.utils.TestUtils.mapForData2;

/***
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class BuilderInsertTest extends AbstractDbTest {
    @Test
    public void insertBuilder_1() {
        LambdaInsert<TB_User> lambdaInsert = new LambdaTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaInsert.getBoundSql(dialect);
        assert boundSql1 instanceof BatchBoundSql;
        assert boundSql1.getSqlString().equals("INSERT INTO TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) VALUES ( ?,?,?,?,?,?,? )");
        //
        BoundSql boundSql2 = lambdaInsert.useQualifier().getBoundSql(dialect);
        assert boundSql2 instanceof BatchBoundSql;
        assert boundSql2.getSqlString().equals("INSERT INTO `TB_User` ( `userUUID` , `name` , `loginName` , `loginPassword` , `email` , `index` , `registerTime` ) VALUES ( ?,?,?,?,?,?,? )");
    }

    @Test
    public void insertBuilder_2() {
        LambdaInsert<TbUserShadow> lambdaInsert = new LambdaTemplate().lambdaInsert(TbUserShadow.class);
        lambdaInsert.applyQueryAsInsert(TB_User.class, queryBuilder -> {
            queryBuilder.eq(TB_User::getIndex, 123);
        });
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaInsert.getBoundSql(dialect);
        assert !(boundSql1 instanceof BatchBoundSql);
        assert boundSql1.getSqlString().equals("INSERT INTO tb_user_shadow ( userUUID , name , loginName , loginPassword , email , index , registerTime )  SELECT * FROM TB_User WHERE index = ?");
        //
        BoundSql boundSql2 = lambdaInsert.useQualifier().getBoundSql(dialect);
        assert !(boundSql2 instanceof BatchBoundSql);
        assert boundSql2.getSqlString().equals("INSERT INTO `tb_user_shadow` ( `userUUID` , `name` , `loginName` , `loginPassword` , `email` , `index` , `registerTime` )  SELECT * FROM `TB_User` WHERE `index` = ?");
    }
}
