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
import net.hasor.db.JdbcUtils;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialectRegister;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class PageTest extends AbstractDbTest {
    @Test
    public void pageTest_1() {
        BoundSql boundSql = new JdbcTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .initPage(10, 2)//
                .getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql.getSqlString().equals("SELECT loginName FROM tb_user LIMIT ?, ?");
        assert boundSql.getArgs()[0].equals(20);
        assert boundSql.getArgs()[1].equals(10);
    }

    @Test
    public void pageTest_2() {
        BoundSql boundSql = new JdbcTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .eq(TbUser::getIndex, 1)//
                .between(TbUser::getAccount, 2, 3)//
                .initPage(10, 2)//
                .getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql.getSqlString().equals("SELECT loginName FROM tb_user WHERE index = ? AND loginName BETWEEN ? AND ? LIMIT ?, ?");
        assert boundSql.getArgs()[0].equals(1);
        assert boundSql.getArgs()[1].equals(2);
        assert boundSql.getArgs()[2].equals(3);
        assert boundSql.getArgs()[3].equals(20);
        assert boundSql.getArgs()[4].equals(10);
    }
}
