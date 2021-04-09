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
import net.hasor.db.JdbcUtils;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialectRegister;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.hasor.test.db.utils.TestUtils.INSERT_ARRAY;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class PageTest extends AbstractDbTest {
    @Test
    public void pageTest_1() {
        BoundSql boundSql = new LambdaTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .initPage(10, 2)//
                .getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql.getSqlString().equals("SELECT loginName FROM tb_user LIMIT ?, ?");
        assert boundSql.getArgs()[0].equals(20);
        assert boundSql.getArgs()[1].equals(10);
    }

    @Test
    public void pageTest_2() {
        BoundSql boundSql = new LambdaTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .eq(TbUser::getIndex, 1)//
                .between(TbUser::getAccount, 2, 3)//
                .initPage(10, 2)//
                .getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql.getSqlString().equals("SELECT loginName FROM tb_user WHERE `index` = ? AND loginName BETWEEN ? AND ? LIMIT ?, ?");
        assert boundSql.getArgs()[0].equals(1);
        assert boundSql.getArgs()[1].equals(2);
        assert boundSql.getArgs()[2].equals(3);
        assert boundSql.getArgs()[3].equals(20);
        assert boundSql.getArgs()[4].equals(10);
    }

    @Test
    public void pageTest_3() {
        BoundSql boundSql1 = new LambdaTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .orderBy(TbUser::getUid).initPage(5, 0).getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql1.getSqlString().equals("SELECT loginName FROM tb_user ORDER BY userUUID LIMIT ?");
        assert boundSql1.getArgs()[0].equals(5);
        //
        BoundSql boundSql2 = new LambdaTemplate().lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .orderBy(TbUser::getUid).initPage(5, 1).getBoundSql(SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL));
        assert boundSql2.getSqlString().equals("SELECT loginName FROM tb_user ORDER BY userUUID LIMIT ?, ?");
        assert boundSql2.getArgs()[0].equals(5);
        assert boundSql2.getArgs()[1].equals(5);
    }

    @Test
    public void pageTest_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            LambdaTemplate lambdaTemplate = appContext.getInstance(LambdaTemplate.class);
            JdbcTemplate jdbcTemplate = lambdaTemplate.getJdbcTemplate();
            jdbcTemplate.execute("delete from tb_user");
            //
            int count = 13;
            Object[][] batchValues = new Object[count][];
            for (int i = 0; i < count; i++) {
                batchValues[i] = new Object[7];
                batchValues[i][0] = "id_" + i;
                batchValues[i][1] = String.format("默认用户_%s", i);
                batchValues[i][2] = String.format("acc_%s", i);
                batchValues[i][3] = String.format("pwd_%s", i);
                batchValues[i][4] = String.format("autoUser_%s@hasor.net", i);
                batchValues[i][5] = i;
                batchValues[i][6] = new Date();
            }
            jdbcTemplate.executeBatch(INSERT_ARRAY, batchValues);//批量执行执行插入语句
            assert jdbcTemplate.queryForInt("select count(1) from tb_user") == 13;
            //
            List<TbUser> page0 = lambdaTemplate.lambdaQuery(TbUser.class).orderBy(TbUser::getIndex).initPage(5, 0).queryForList();
            List<TbUser> page1 = lambdaTemplate.lambdaQuery(TbUser.class).orderBy(TbUser::getIndex).initPage(5, 1).queryForList();
            List<TbUser> page2 = lambdaTemplate.lambdaQuery(TbUser.class).orderBy(TbUser::getIndex).initPage(5, 2).queryForList();
            List<TbUser> page3 = lambdaTemplate.lambdaQuery(TbUser.class).orderBy(TbUser::getIndex).initPage(5, 3).queryForList();
            List<TbUser> page4 = lambdaTemplate.lambdaQuery(TbUser.class).orderBy(TbUser::getIndex).initPage(5, 4).queryForList();
            //
            assert page0.size() == 5;
            assert page1.size() == 5;
            assert page2.size() == 3;
            assert page3.size() == 0;
            assert page4.size() == 0;
            //
            List<TbUser> pageAll = new ArrayList<>();
            pageAll.addAll(page0);
            pageAll.addAll(page1);
            pageAll.addAll(page2);
            pageAll.addAll(page3);
            pageAll.addAll(page4);
            for (int i = 0; i < count; i++) {
                assert pageAll.get(i).getUid().equals("id_" + i);
            }
        }
    }

    @Test
    public void pageTest_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            LambdaTemplate lambdaTemplate = appContext.getInstance(LambdaTemplate.class);
            JdbcTemplate jdbcTemplate = lambdaTemplate.getJdbcTemplate();
            jdbcTemplate.execute("delete from tb_user");
            //
            int count = 13;
            Object[][] batchValues = new Object[count][];
            for (int i = 0; i < count; i++) {
                batchValues[i] = new Object[7];
                batchValues[i][0] = "id_" + i;
                batchValues[i][1] = String.format("默认用户_%s", i);
                batchValues[i][2] = String.format("acc_%s", i);
                batchValues[i][3] = String.format("pwd_%s", i);
                batchValues[i][4] = String.format("autoUser_%s@hasor.net", i);
                batchValues[i][5] = i;
                batchValues[i][6] = new Date();
            }
            jdbcTemplate.executeBatch(INSERT_ARRAY, batchValues);//批量执行执行插入语句
            assert jdbcTemplate.queryForInt("select count(1) from tb_user") == 13;
            //
            List<TB_User> page0 = lambdaTemplate.lambdaQuery(TB_User.class).orderBy(TB_User::getIndex).initPage(5, 0).queryForList();
            List<TB_User> page1 = lambdaTemplate.lambdaQuery(TB_User.class).orderBy(TB_User::getIndex).initPage(5, 1).queryForList();
            List<TB_User> page2 = lambdaTemplate.lambdaQuery(TB_User.class).orderBy(TB_User::getIndex).initPage(5, 2).queryForList();
            List<TB_User> page3 = lambdaTemplate.lambdaQuery(TB_User.class).orderBy(TB_User::getIndex).initPage(5, 3).queryForList();
            List<TB_User> page4 = lambdaTemplate.lambdaQuery(TB_User.class).orderBy(TB_User::getIndex).initPage(5, 4).queryForList();
            //
            assert page0.size() == 5;
            assert page1.size() == 5;
            assert page2.size() == 3;
            assert page3.size() == 0;
            assert page4.size() == 0;
            //
            List<TB_User> pageAll = new ArrayList<>();
            pageAll.addAll(page0);
            pageAll.addAll(page1);
            pageAll.addAll(page2);
            pageAll.addAll(page3);
            pageAll.addAll(page4);
            for (int i = 0; i < count; i++) {
                assert pageAll.get(i).getUserUUID().equals("id_" + i);
            }
        }
    }
}
