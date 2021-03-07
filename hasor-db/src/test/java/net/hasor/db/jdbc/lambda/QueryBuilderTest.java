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
import net.hasor.db.dialect.BatchBoundSql;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.dialect.provider.MySqlDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.hasor.test.db.utils.TestUtils.beanForData1;
import static net.hasor.test.db.utils.TestUtils.mapForData2;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class QueryBuilderTest extends AbstractDbTest {
    @Test
    public void queryBuilder1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ?");
        assert boundSql1.getArgs()[0].equals("abc");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).eq(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql2.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName = ?");
        assert boundSql2.getArgs()[0].equals(1);
        assert boundSql2.getArgs()[1].equals("abc");
        BoundSql boundSql3 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().eq(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName = ?");
        assert boundSql3.getArgs()[0].equals(1);
        assert boundSql3.getArgs()[1].equals("abc");
        //
        BoundSql boundSql4 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).ne(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName <> ?");
        assert boundSql4.getArgs()[0].equals(1);
        assert boundSql4.getArgs()[1].equals("abc");
        BoundSql boundSql5 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().ne(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql5.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName <> ?");
        assert boundSql5.getArgs()[0].equals(1);
        assert boundSql5.getArgs()[1].equals("abc");
        //
        BoundSql boundSql6 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).gt(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql6.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName > ?");
        assert boundSql6.getArgs()[0].equals(1);
        assert boundSql6.getArgs()[1].equals("abc");
        BoundSql boundSql7 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().gt(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql7.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName > ?");
        assert boundSql7.getArgs()[0].equals(1);
        assert boundSql7.getArgs()[1].equals("abc");
        //
        BoundSql boundSql8 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).ge(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql8.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName >= ?");
        assert boundSql8.getArgs()[0].equals(1);
        assert boundSql8.getArgs()[1].equals("abc");
        BoundSql boundSql9 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().ge(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql9.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName >= ?");
        assert boundSql9.getArgs()[0].equals(1);
        assert boundSql9.getArgs()[1].equals("abc");
        //
        BoundSql boundSql10 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).lt(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql10.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName < ?");
        assert boundSql10.getArgs()[0].equals(1);
        assert boundSql10.getArgs()[1].equals("abc");
        BoundSql boundSql11 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().lt(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql11.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName < ?");
        assert boundSql11.getArgs()[0].equals(1);
        assert boundSql11.getArgs()[1].equals("abc");
        //
        BoundSql boundSql12 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).le(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql12.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName <= ?");
        assert boundSql12.getArgs()[0].equals(1);
        assert boundSql12.getArgs()[1].equals("abc");
        BoundSql boundSql13 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().le(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql13.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName <= ?");
        assert boundSql13.getArgs()[0].equals(1);
        assert boundSql13.getArgs()[1].equals("abc");
        //
        BoundSql boundSql14 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).isNull(TbUser::getAccount).getBoundSql();
        assert boundSql14.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName IS NULL");
        assert boundSql14.getArgs()[0].equals(1);
        BoundSql boundSql15 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().isNull(TbUser::getAccount).getBoundSql();
        assert boundSql15.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName IS NULL");
        assert boundSql15.getArgs()[0].equals(1);
        //
        BoundSql boundSql16 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).isNotNull(TbUser::getAccount).getBoundSql();
        assert boundSql16.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName IS NOT NULL");
        assert boundSql16.getArgs()[0].equals(1);
        BoundSql boundSql17 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().isNotNull(TbUser::getAccount).getBoundSql();
        assert boundSql17.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName IS NOT NULL");
        assert boundSql17.getArgs()[0].equals(1);
        //
        List<String> inData = Arrays.asList("a", "b", "c");
        BoundSql boundSql18 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).in(TbUser::getAccount, inData).getBoundSql();
        assert boundSql18.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName IN ( ? , ? , ? )");
        assert boundSql18.getArgs()[0].equals(1);
        assert boundSql18.getArgs()[1].equals("a");
        assert boundSql18.getArgs()[2].equals("b");
        assert boundSql18.getArgs()[3].equals("c");
        BoundSql boundSql19 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().in(TbUser::getAccount, inData).getBoundSql();
        assert boundSql19.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName IN ( ? , ? , ? )");
        assert boundSql19.getArgs()[0].equals(1);
        assert boundSql19.getArgs()[1].equals("a");
        assert boundSql19.getArgs()[2].equals("b");
        assert boundSql19.getArgs()[3].equals("c");
        //
        List<String> notInData = Arrays.asList("a", "b", "c");
        BoundSql boundSql20 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).notIn(TbUser::getAccount, notInData).getBoundSql();
        assert boundSql20.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName NOT IN ( ? , ? , ? )");
        assert boundSql20.getArgs()[0].equals(1);
        assert boundSql20.getArgs()[1].equals("a");
        assert boundSql20.getArgs()[2].equals("b");
        assert boundSql20.getArgs()[3].equals("c");
        BoundSql boundSql21 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().notIn(TbUser::getAccount, notInData).getBoundSql();
        assert boundSql21.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName NOT IN ( ? , ? , ? )");
        assert boundSql21.getArgs()[0].equals(1);
        assert boundSql21.getArgs()[1].equals("a");
        assert boundSql21.getArgs()[2].equals("b");
        assert boundSql21.getArgs()[3].equals("c");
        //
        BoundSql boundSql22 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).between(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql22.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName BETWEEN ? AND ?");
        assert boundSql22.getArgs()[0].equals(1);
        assert boundSql22.getArgs()[1].equals(2);
        assert boundSql22.getArgs()[2].equals(3);
        BoundSql boundSql23 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().between(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql23.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName BETWEEN ? AND ?");
        assert boundSql23.getArgs()[0].equals(1);
        assert boundSql23.getArgs()[1].equals(2);
        assert boundSql23.getArgs()[2].equals(3);
        //
        BoundSql boundSql24 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).notBetween(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql24.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName NOT BETWEEN ? AND ?");
        assert boundSql24.getArgs()[0].equals(1);
        assert boundSql24.getArgs()[1].equals(2);
        assert boundSql24.getArgs()[2].equals(3);
        BoundSql boundSql25 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().notBetween(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql25.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName NOT BETWEEN ? AND ?");
        assert boundSql25.getArgs()[0].equals(1);
        assert boundSql25.getArgs()[1].equals(2);
        assert boundSql25.getArgs()[2].equals(3);
        //
        BoundSql boundSql26 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).like(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql26.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName LIKE CONCAT('%', ? ,'%')");
        assert boundSql26.getArgs()[0].equals(1);
        assert boundSql26.getArgs()[1].equals("abc");
        BoundSql boundSql27 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().notLike(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql27.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName NOT LIKE CONCAT('%', ? ,'%')");
        assert boundSql27.getArgs()[0].equals(1);
        assert boundSql27.getArgs()[1].equals("abc");
        //
        BoundSql boundSql28 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).likeRight(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql28.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName LIKE CONCAT( ? ,'%')");
        assert boundSql28.getArgs()[0].equals(1);
        assert boundSql28.getArgs()[1].equals("abc");
        BoundSql boundSql29 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().notLikeRight(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql29.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName NOT LIKE CONCAT( ? ,'%')");
        assert boundSql29.getArgs()[0].equals(1);
        assert boundSql29.getArgs()[1].equals("abc");
        //
        BoundSql boundSql30 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).likeLeft(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql30.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? AND loginName LIKE CONCAT('%', ? )");
        assert boundSql30.getArgs()[0].equals(1);
        assert boundSql30.getArgs()[1].equals("abc");
        BoundSql boundSql31 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getIndex, 1).or().notLikeLeft(TbUser::getAccount, "abc").getBoundSql();
        assert boundSql31.getSqlString().equals("SELECT * FROM tb_user WHERE index = ? OR loginName NOT LIKE CONCAT('%', ? )");
        assert boundSql31.getArgs()[0].equals(1);
        assert boundSql31.getArgs()[1].equals("abc");
        //
        BoundSql boundSql32 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").getBoundSql();
        assert boundSql32.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? AND loginName = ?");
        assert boundSql32.getArgs()[0].equals("a");
        assert boundSql32.getArgs()[1].equals("b");
        //
        BoundSql boundSql33 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit ?", 123).getBoundSql();
        assert boundSql33.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? AND loginName = ? limit ?");
        assert boundSql33.getArgs()[0].equals("a");
        assert boundSql33.getArgs()[1].equals("b");
        assert boundSql33.getArgs()[2].equals(123);
    }

    @Test
    public void queryBuilder2() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b")//
                .groupBy(TbUser::getIndex).getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT index FROM tb_user WHERE loginName = ? AND loginName = ? GROUP BY index");
        assert boundSql1.getArgs()[0].equals("a");
        assert boundSql1.getArgs()[1].equals("b");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a")//
                .eq(TbUser::getAccount, "b")//
                .apply("limit 1")//
                .groupBy(TbUser::getIndex)//
                .apply("limit 1").getBoundSql();
        assert boundSql2.getSqlString().equals("SELECT index FROM tb_user WHERE loginName = ? AND loginName = ? limit 1 GROUP BY index limit 1");
    }

    @Test
    public void queryBuilder3() {
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .groupBy(TbUser::getIndex)          //
                    .eq(TbUser::getAccount, "b"); // after groupBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("condition is locked.");
        }
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .orderBy(TbUser::getIndex)          //
                    .eq(TbUser::getAccount, "b"); // << --- after orderBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("condition is locked.");
        }
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .groupBy(TbUser::getIndex)  //
                    .orderBy(TbUser::getIndex)  //
                    .groupBy(TbUser::getIndex); // << --- after orderBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("group by is locked.");
        }
    }

    @Test
    public void queryBuilder_or_1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql3 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").or(nestedQuery -> {
                    nestedQuery.ge(TbUser::getCreateTime, 1); // >= ?
                    nestedQuery.le(TbUser::getCreateTime, 2); // <= ?
                }).getBoundSql();
        BoundSql boundSql4 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").or().nested(nestedQuery -> {
                    nestedQuery.ge(TbUser::getCreateTime, 1); // >= ?
                    nestedQuery.le(TbUser::getCreateTime, 2); // <= ?
                }).getBoundSql();
        //
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? OR ( registerTime >= ? AND registerTime <= ? )");
        assert boundSql3.getArgs()[0].equals("a");
        assert boundSql3.getArgs()[1].equals(1);
        assert boundSql3.getArgs()[2].equals(2);
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? OR ( registerTime >= ? AND registerTime <= ? )");
        assert boundSql4.getArgs()[0].equals("a");
        assert boundSql4.getArgs()[1].equals(1);
        assert boundSql4.getArgs()[2].equals(2);
    }

    @Test
    public void queryBuilder_and_1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql3 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").and(nestedQuery -> {
                    nestedQuery.ge(TbUser::getCreateTime, 1); // >= ?
                    nestedQuery.le(TbUser::getCreateTime, 2); // <= ?
                }).eq(TbUser::getAccount, 123).getBoundSql();
        BoundSql boundSql4 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").and().nested(nestedQuery -> {
                    nestedQuery.ge(TbUser::getCreateTime, 1); // >= ?
                    nestedQuery.le(TbUser::getCreateTime, 2); // <= ?
                }).eq(TbUser::getAccount, 123).getBoundSql();
        //
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? AND ( registerTime >= ? AND registerTime <= ? ) AND loginName = ?");
        assert boundSql3.getArgs()[0].equals("a");
        assert boundSql3.getArgs()[1].equals(1);
        assert boundSql3.getArgs()[2].equals(2);
        assert boundSql3.getArgs()[3].equals(123);
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? AND ( registerTime >= ? AND registerTime <= ? ) AND loginName = ?");
        assert boundSql4.getArgs()[0].equals("a");
        assert boundSql4.getArgs()[1].equals(1);
        assert boundSql4.getArgs()[2].equals(2);
        assert boundSql4.getArgs()[3].equals(123);
    }

    @Test
    public void queryBuilder_nested_1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql3 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .eq(TbUser::getAccount, "a").nested(nestedQuery -> {
                    nestedQuery.ge(TbUser::getCreateTime, 1); // >= ?
                    nestedQuery.le(TbUser::getCreateTime, 2); // <= ?
                }).eq(TbUser::getAccount, 123).getBoundSql();
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? AND ( registerTime >= ? AND registerTime <= ? ) AND loginName = ?");
        assert boundSql3.getArgs()[0].equals("a");
        assert boundSql3.getArgs()[1].equals(1);
        assert boundSql3.getArgs()[2].equals(2);
        assert boundSql3.getArgs()[3].equals(123);
        //
        BoundSql boundSql4 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .nested(nq0 -> {
                    nq0.nested(nq1 -> {
                        nq1.ge(TbUser::getCreateTime, 1); // >= ?
                        nq1.le(TbUser::getCreateTime, 2); // <= ?
                    }).nested(nq2 -> {
                        nq2.eq(TbUser::getIndex, 1);
                    });
                }).eq(TbUser::getAccount, 123).getBoundSql();
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE ( ( registerTime >= ? AND registerTime <= ? ) AND ( index = ? ) ) AND loginName = ?");
        assert boundSql4.getArgs()[0].equals(1);
        assert boundSql4.getArgs()[1].equals(2);
        assert boundSql4.getArgs()[2].equals(1);
        assert boundSql4.getArgs()[3].equals(123);
        //
        BoundSql boundSql5 = jdbcTemplate.lambdaQuery(TbUser.class)//
                .nested(nq0 -> {
                    nq0.nested(nq1 -> {
                        nq1.ge(TbUser::getCreateTime, 1); // >= ?
                        nq1.le(TbUser::getCreateTime, 2); // <= ?
                    }).nested(nq2 -> {
                        nq2.eq(TbUser::getIndex, 1);
                    });
                }).getBoundSql();
        assert boundSql5.getSqlString().equals("SELECT * FROM tb_user WHERE ( ( registerTime >= ? AND registerTime <= ? ) AND ( index = ? ) )");
        assert boundSql5.getArgs()[0].equals(1);
        assert boundSql5.getArgs()[1].equals(2);
        assert boundSql5.getArgs()[2].equals(1);
    }

    @Test
    public void queryBuilder4() throws SQLException {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            Map<String, Object> tbUser = jdbcTemplate.lambdaQuery(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                    .queryForMap();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("DataSource or Connection are not available.");
        }
    }

    @Test
    public void queryBuilder5() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").asc(TbUser::getAccount).getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? ORDER BY loginName ASC");
        assert boundSql1.getArgs()[0].equals("a");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").asc(Arrays.asList(TbUser::getAccount, TbUser::getIndex)).getBoundSql();
        assert boundSql2.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? ORDER BY loginName ASC , index ASC");
        assert boundSql2.getArgs()[0].equals("a");
        //
        BoundSql boundSql3 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").desc(TbUser::getAccount).getBoundSql();
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? ORDER BY loginName DESC");
        assert boundSql3.getArgs()[0].equals("a");
        //
        BoundSql boundSql4 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").asc(TbUser::getIndex).desc(TbUser::getAccount).getBoundSql();
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? ORDER BY index ASC , loginName DESC");
        assert boundSql4.getArgs()[0].equals("a");
        //
        BoundSql boundSql5 = jdbcTemplate.lambdaQuery(TbUser.class).eq(TbUser::getAccount, "a").orderBy(TbUser::getIndex).getBoundSql();
        assert boundSql5.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = ? ORDER BY index");
        assert boundSql5.getArgs()[0].equals("a");
    }

    @Test
    public void queryBuilder6() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class).select("a", "b", "c", "d")//
                .eq(TbUser::getIndex, 1).or().between(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT a , b , c , d FROM tb_user WHERE index = ? OR loginName BETWEEN ? AND ?");
        assert boundSql1.getArgs()[0].equals(1);
        assert boundSql1.getArgs()[1].equals(2);
        assert boundSql1.getArgs()[2].equals(3);
    }

    @Test
    public void queryBuilder7() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class).select(Arrays.asList(TbUser::getAccount, TbUser::getIndex))//
                .eq(TbUser::getIndex, 1).or().between(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT loginName , index FROM tb_user WHERE index = ? OR loginName BETWEEN ? AND ?");
        assert boundSql1.getArgs()[0].equals(1);
        assert boundSql1.getArgs()[1].equals(2);
        assert boundSql1.getArgs()[2].equals(3);
    }

    @Test
    public void queryBuilder8() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaQuery(TbUser.class).select(TbUser::getAccount)//
                .eq(TbUser::getIndex, 1).or().between(TbUser::getAccount, 2, 3).getBoundSql();
        assert boundSql1.getSqlString().equals("SELECT loginName FROM tb_user WHERE index = ? OR loginName BETWEEN ? AND ?");
        assert boundSql1.getArgs()[0].equals(1);
        assert boundSql1.getArgs()[1].equals(2);
        assert boundSql1.getArgs()[2].equals(3);
    }

    @Test
    public void queryBuilder9() {
        LambdaOperations.LambdaInsert<TB_User> lambdaInsert = new JdbcTemplate().lambdaInsert(TB_User.class);
        lambdaInsert.applyEntity(beanForData1());
        lambdaInsert.applyMap(mapForData2());
        //
        SqlDialect dialect = new MySqlDialect();
        BoundSql boundSql1 = lambdaInsert.getBoundSql(dialect);
        assert boundSql1 instanceof BatchBoundSql;
        assert boundSql1.getSqlString().equals("insert into TB_User ( userUUID , name , loginName , loginPassword , email , index , registerTime ) values ( ?,?,?,?,?,?,? )");
        //
        BoundSql boundSql2 = lambdaInsert.useQualifier().getBoundSql(dialect);
        assert boundSql2 instanceof BatchBoundSql;
        assert boundSql2.getSqlString().equals("insert into `TB_User` ( `userUUID` , `name` , `loginName` , `loginPassword` , `email` , `index` , `registerTime` ) values ( ?,?,?,?,?,?,? )");
    }
}
