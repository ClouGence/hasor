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
package net.hasor.db.dialect;
import net.hasor.db.dialect.provider.DmDialect;
import net.hasor.db.dialect.provider.MySqlDialect;
import net.hasor.db.dialect.provider.OracleDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaInsert;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * 方言
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MultipleInsertSqlDialectTest extends AbstractDbTest {
    @Test
    public void dialect_mysql_1() {
        LambdaInsert<TB_User> lambdaInsert = new JdbcTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql = lambdaInsert.getBoundSql(dialect);
        //
        assert boundSql instanceof BatchBoundSql;
        assert boundSql.getSqlString().equals("insert into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? )");
        assert boundSql.getArgs().length == 2;
        assert ((BatchBoundSql) boundSql).getArgs()[0].length == 7;
        assert ((BatchBoundSql) boundSql).getArgs()[1].length == 7;
        //
        assert ((BatchBoundSql) boundSql).getArgs()[0][0].equals(beanForData1().getUserUUID());
        assert ((BatchBoundSql) boundSql).getArgs()[0][1].equals(beanForData1().getName());
        assert ((BatchBoundSql) boundSql).getArgs()[0][2].equals(beanForData1().getLoginName());
        assert ((BatchBoundSql) boundSql).getArgs()[0][3].equals(beanForData1().getLoginPassword());
        assert ((BatchBoundSql) boundSql).getArgs()[0][4].equals(beanForData1().getEmail());
        assert ((BatchBoundSql) boundSql).getArgs()[0][5].equals(beanForData1().getIndex());
        assert ((BatchBoundSql) boundSql).getArgs()[0][6].equals(beanForData1().getRegisterTime());
        //
        assert ((BatchBoundSql) boundSql).getArgs()[1][0].equals(beanForData2().getUserUUID());
        assert ((BatchBoundSql) boundSql).getArgs()[1][1].equals(beanForData2().getName());
        assert ((BatchBoundSql) boundSql).getArgs()[1][2].equals(beanForData2().getLoginName());
        assert ((BatchBoundSql) boundSql).getArgs()[1][3].equals(beanForData2().getLoginPassword());
        assert ((BatchBoundSql) boundSql).getArgs()[1][4].equals(beanForData2().getEmail());
        assert ((BatchBoundSql) boundSql).getArgs()[1][5].equals(beanForData2().getIndex());
        assert ((BatchBoundSql) boundSql).getArgs()[1][6].equals(beanForData2().getRegisterTime());
    }

    @Test
    public void dialect_mysql_2() {
        LambdaInsert<TB_User> lambdaInsert = new JdbcTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql = lambdaInsert.useMultipleValues().getBoundSql(dialect);
        //
        assert !(boundSql instanceof BatchBoundSql);
        assert boundSql.getSqlString().equals("insert into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? ) , ( ?,?,?,?,?,?,? )");
        assert boundSql.getArgs().length == 14;
        assert boundSql.getArgs()[0].equals(beanForData1().getUserUUID());
        assert boundSql.getArgs()[1].equals(beanForData1().getName());
        assert boundSql.getArgs()[2].equals(beanForData1().getLoginName());
        assert boundSql.getArgs()[3].equals(beanForData1().getLoginPassword());
        assert boundSql.getArgs()[4].equals(beanForData1().getEmail());
        assert boundSql.getArgs()[5].equals(beanForData1().getIndex());
        assert boundSql.getArgs()[6].equals(beanForData1().getRegisterTime());
        //
        assert boundSql.getArgs()[7].equals(beanForData2().getUserUUID());
        assert boundSql.getArgs()[8].equals(beanForData2().getName());
        assert boundSql.getArgs()[9].equals(beanForData2().getLoginName());
        assert boundSql.getArgs()[10].equals(beanForData2().getLoginPassword());
        assert boundSql.getArgs()[11].equals(beanForData2().getEmail());
        assert boundSql.getArgs()[12].equals(beanForData2().getIndex());
        assert boundSql.getArgs()[13].equals(beanForData2().getRegisterTime());
    }

    @Test
    public void dialect_dm_2() {
        LambdaInsert<TB_User> lambdaInsert = new JdbcTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new DmDialect();
        BoundSql boundSql = lambdaInsert.useMultipleValues().getBoundSql(dialect);
        //
        assert !(boundSql instanceof BatchBoundSql);
        assert boundSql.getSqlString().equals("insert into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? ) , ( ?,?,?,?,?,?,? )");
        assert boundSql.getArgs().length == 14;
        assert boundSql.getArgs()[0].equals(beanForData1().getUserUUID());
        assert boundSql.getArgs()[1].equals(beanForData1().getName());
        assert boundSql.getArgs()[2].equals(beanForData1().getLoginName());
        assert boundSql.getArgs()[3].equals(beanForData1().getLoginPassword());
        assert boundSql.getArgs()[4].equals(beanForData1().getEmail());
        assert boundSql.getArgs()[5].equals(beanForData1().getIndex());
        assert boundSql.getArgs()[6].equals(beanForData1().getRegisterTime());
        //
        assert boundSql.getArgs()[7].equals(beanForData2().getUserUUID());
        assert boundSql.getArgs()[8].equals(beanForData2().getName());
        assert boundSql.getArgs()[9].equals(beanForData2().getLoginName());
        assert boundSql.getArgs()[10].equals(beanForData2().getLoginPassword());
        assert boundSql.getArgs()[11].equals(beanForData2().getEmail());
        assert boundSql.getArgs()[12].equals(beanForData2().getIndex());
        assert boundSql.getArgs()[13].equals(beanForData2().getRegisterTime());
    }

    @Test
    public void dialect_oracle_2() {
        LambdaInsert<TB_User> lambdaInsert = new JdbcTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new OracleDialect();
        BoundSql boundSql = lambdaInsert.useMultipleValues().getBoundSql(dialect);
        //
        assert !(boundSql instanceof BatchBoundSql);
        assert boundSql.getSqlString().equals("insert all into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? )  into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? ) select 1 from dual");
        assert boundSql.getArgs().length == 14;
        assert boundSql.getArgs()[0].equals(beanForData1().getUserUUID());
        assert boundSql.getArgs()[1].equals(beanForData1().getName());
        assert boundSql.getArgs()[2].equals(beanForData1().getLoginName());
        assert boundSql.getArgs()[3].equals(beanForData1().getLoginPassword());
        assert boundSql.getArgs()[4].equals(beanForData1().getEmail());
        assert boundSql.getArgs()[5].equals(beanForData1().getIndex());
        assert boundSql.getArgs()[6].equals(beanForData1().getRegisterTime());
        //
        assert boundSql.getArgs()[7].equals(beanForData2().getUserUUID());
        assert boundSql.getArgs()[8].equals(beanForData2().getName());
        assert boundSql.getArgs()[9].equals(beanForData2().getLoginName());
        assert boundSql.getArgs()[10].equals(beanForData2().getLoginPassword());
        assert boundSql.getArgs()[11].equals(beanForData2().getEmail());
        assert boundSql.getArgs()[12].equals(beanForData2().getIndex());
        assert boundSql.getArgs()[13].equals(beanForData2().getRegisterTime());
    }
}
