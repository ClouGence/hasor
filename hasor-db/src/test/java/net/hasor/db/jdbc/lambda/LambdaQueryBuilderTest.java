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
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.LambdaOperations.BoundSql;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/***
 * 批量Insert语句执行
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaQueryBuilderTest extends AbstractDbTest {
    @Test
    public void lambdaQueryBuilder_1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getAccount, "abc");
        assert boundSql1.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = :param_1");
        assert boundSql1.getArgs().get("param_1").equals("abc");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).eq(TbUser::getAccount, "abc");
        assert boundSql2.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName = :param_2");
        assert boundSql2.getArgs().get("param_1").equals(1);
        assert boundSql2.getArgs().get("param_2").equals("abc");
        BoundSql boundSql3 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().eq(TbUser::getAccount, "abc");
        assert boundSql3.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName = :param_2");
        assert boundSql3.getArgs().get("param_1").equals(1);
        assert boundSql3.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql4 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).ne(TbUser::getAccount, "abc");
        assert boundSql4.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName <> :param_2");
        assert boundSql4.getArgs().get("param_1").equals(1);
        assert boundSql4.getArgs().get("param_2").equals("abc");
        BoundSql boundSql5 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().ne(TbUser::getAccount, "abc");
        assert boundSql5.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName <> :param_2");
        assert boundSql5.getArgs().get("param_1").equals(1);
        assert boundSql5.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql6 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).gt(TbUser::getAccount, "abc");
        assert boundSql6.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName > :param_2");
        assert boundSql6.getArgs().get("param_1").equals(1);
        assert boundSql6.getArgs().get("param_2").equals("abc");
        BoundSql boundSql7 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().gt(TbUser::getAccount, "abc");
        assert boundSql7.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName > :param_2");
        assert boundSql7.getArgs().get("param_1").equals(1);
        assert boundSql7.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql8 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).ge(TbUser::getAccount, "abc");
        assert boundSql8.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName >= :param_2");
        assert boundSql8.getArgs().get("param_1").equals(1);
        assert boundSql8.getArgs().get("param_2").equals("abc");
        BoundSql boundSql9 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().ge(TbUser::getAccount, "abc");
        assert boundSql9.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName >= :param_2");
        assert boundSql9.getArgs().get("param_1").equals(1);
        assert boundSql9.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql10 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).lt(TbUser::getAccount, "abc");
        assert boundSql10.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName < :param_2");
        assert boundSql10.getArgs().get("param_1").equals(1);
        assert boundSql10.getArgs().get("param_2").equals("abc");
        BoundSql boundSql11 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().lt(TbUser::getAccount, "abc");
        assert boundSql11.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName < :param_2");
        assert boundSql11.getArgs().get("param_1").equals(1);
        assert boundSql11.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql12 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).le(TbUser::getAccount, "abc");
        assert boundSql12.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName <= :param_2");
        assert boundSql12.getArgs().get("param_1").equals(1);
        assert boundSql12.getArgs().get("param_2").equals("abc");
        BoundSql boundSql13 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().le(TbUser::getAccount, "abc");
        assert boundSql13.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName <= :param_2");
        assert boundSql13.getArgs().get("param_1").equals(1);
        assert boundSql13.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql14 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).isNull(TbUser::getAccount);
        assert boundSql14.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName IS NULL");
        assert boundSql14.getArgs().get("param_1").equals(1);
        BoundSql boundSql15 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().isNull(TbUser::getAccount);
        assert boundSql15.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName IS NULL");
        assert boundSql15.getArgs().get("param_1").equals(1);
        //
        BoundSql boundSql16 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).isNotNull(TbUser::getAccount);
        assert boundSql16.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName IS NOT NULL");
        assert boundSql16.getArgs().get("param_1").equals(1);
        BoundSql boundSql17 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().isNotNull(TbUser::getAccount);
        assert boundSql17.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName IS NOT NULL");
        assert boundSql17.getArgs().get("param_1").equals(1);
        //
        List<String> inData = Arrays.asList("a", "b", "c");
        BoundSql boundSql18 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).in(TbUser::getAccount, inData);
        assert boundSql18.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName IN ( :param_2 , :param_3 , :param_4 )");
        assert boundSql18.getArgs().get("param_1").equals(1);
        assert boundSql18.getArgs().get("param_2").equals("a");
        assert boundSql18.getArgs().get("param_3").equals("b");
        assert boundSql18.getArgs().get("param_4").equals("c");
        BoundSql boundSql19 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().in(TbUser::getAccount, inData);
        assert boundSql19.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName IN ( :param_2 , :param_3 , :param_4 )");
        assert boundSql19.getArgs().get("param_1").equals(1);
        assert boundSql19.getArgs().get("param_2").equals("a");
        assert boundSql19.getArgs().get("param_3").equals("b");
        assert boundSql19.getArgs().get("param_4").equals("c");
        //
        List<String> notInData = Arrays.asList("a", "b", "c");
        BoundSql boundSql20 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).notIn(TbUser::getAccount, notInData);
        assert boundSql20.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName NOT IN ( :param_2 , :param_3 , :param_4 )");
        assert boundSql20.getArgs().get("param_1").equals(1);
        assert boundSql20.getArgs().get("param_2").equals("a");
        assert boundSql20.getArgs().get("param_3").equals("b");
        assert boundSql20.getArgs().get("param_4").equals("c");
        BoundSql boundSql21 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().notIn(TbUser::getAccount, notInData);
        assert boundSql21.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName NOT IN ( :param_2 , :param_3 , :param_4 )");
        assert boundSql21.getArgs().get("param_1").equals(1);
        assert boundSql21.getArgs().get("param_2").equals("a");
        assert boundSql21.getArgs().get("param_3").equals("b");
        assert boundSql21.getArgs().get("param_4").equals("c");
        //
        BoundSql boundSql22 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).between(TbUser::getAccount, 2, 3);
        assert boundSql22.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName BETWEEN :param_2 AND :param_3");
        assert boundSql22.getArgs().get("param_1").equals(1);
        assert boundSql22.getArgs().get("param_2").equals(2);
        assert boundSql22.getArgs().get("param_3").equals(3);
        BoundSql boundSql23 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().between(TbUser::getAccount, 2, 3);
        assert boundSql23.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName BETWEEN :param_2 AND :param_3");
        assert boundSql23.getArgs().get("param_1").equals(1);
        assert boundSql23.getArgs().get("param_2").equals(2);
        assert boundSql23.getArgs().get("param_3").equals(3);
        //
        BoundSql boundSql24 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).notBetween(TbUser::getAccount, 2, 3);
        assert boundSql24.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName NOT BETWEEN :param_2 AND :param_3");
        assert boundSql24.getArgs().get("param_1").equals(1);
        assert boundSql24.getArgs().get("param_2").equals(2);
        assert boundSql24.getArgs().get("param_3").equals(3);
        BoundSql boundSql25 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().notBetween(TbUser::getAccount, 2, 3);
        assert boundSql25.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName NOT BETWEEN :param_2 AND :param_3");
        assert boundSql25.getArgs().get("param_1").equals(1);
        assert boundSql25.getArgs().get("param_2").equals(2);
        assert boundSql25.getArgs().get("param_3").equals(3);
        //
        BoundSql boundSql26 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).like(TbUser::getAccount, "abc");
        assert boundSql26.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName LIKE CONCAT('%', :param_2 ,'%')");
        assert boundSql26.getArgs().get("param_1").equals(1);
        assert boundSql26.getArgs().get("param_2").equals("abc");
        BoundSql boundSql27 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().notLike(TbUser::getAccount, "abc");
        assert boundSql27.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName NOT LIKE CONCAT('%', :param_2 ,'%')");
        assert boundSql27.getArgs().get("param_1").equals(1);
        assert boundSql27.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql28 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).likeRight(TbUser::getAccount, "abc");
        assert boundSql28.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName LIKE CONCAT( :param_2 ,'%')");
        assert boundSql28.getArgs().get("param_1").equals(1);
        assert boundSql28.getArgs().get("param_2").equals("abc");
        BoundSql boundSql29 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().notLikeRight(TbUser::getAccount, "abc");
        assert boundSql29.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName NOT LIKE CONCAT( :param_2 ,'%')");
        assert boundSql29.getArgs().get("param_1").equals(1);
        assert boundSql29.getArgs().get("param_2").equals("abc");
        //
        BoundSql boundSql30 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).likeLeft(TbUser::getAccount, "abc");
        assert boundSql30.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 AND loginName LIKE CONCAT('%', :param_2 )");
        assert boundSql30.getArgs().get("param_1").equals(1);
        assert boundSql30.getArgs().get("param_2").equals("abc");
        BoundSql boundSql31 = jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getIndex, 1).or().notLikeLeft(TbUser::getAccount, "abc");
        assert boundSql31.getSqlString().equals("SELECT * FROM tb_user WHERE index = :param_1 OR loginName NOT LIKE CONCAT('%', :param_2 )");
        assert boundSql31.getArgs().get("param_1").equals(1);
        assert boundSql31.getArgs().get("param_2").equals("abc");
    }

    @Test
    public void lambdaQueryBuilder_2() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaSelect(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b");
        assert boundSql1.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = :param_1 AND loginName = :param_2");
        assert boundSql1.getArgs().get("param_1").equals("a");
        assert boundSql1.getArgs().get("param_2").equals("b");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaSelect(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit ?", 123);
        assert boundSql2.getSqlString().equals("SELECT * FROM tb_user WHERE loginName = :param_1 AND loginName = :param_2 limit :param_3");
        assert boundSql2.getArgs().get("param_1").equals("a");
        assert boundSql2.getArgs().get("param_2").equals("b");
        assert boundSql2.getArgs().get("param_3").equals(123);
        //
    }

    @Test
    public void lambdaQueryBuilder_3() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //
        BoundSql boundSql1 = jdbcTemplate.lambdaSelect(TbUser.class)//
                .eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b")//
                .groupBy(TbUser::getIndex);
        assert boundSql1.getSqlString().equals("SELECT index FROM tb_user WHERE loginName = :param_1 AND loginName = :param_2 GROUP BY index");
        assert boundSql1.getArgs().get("param_1").equals("a");
        assert boundSql1.getArgs().get("param_2").equals("b");
        //
        BoundSql boundSql2 = jdbcTemplate.lambdaSelect(TbUser.class)//
                .eq(TbUser::getAccount, "a")//
                .eq(TbUser::getAccount, "b")//
                .apply("limit 1")//
                .groupBy(TbUser::getIndex)//
                .apply("limit 1");
        assert boundSql2.getSqlString().equals("SELECT index FROM tb_user WHERE loginName = :param_1 AND loginName = :param_2 limit 1 GROUP BY index limit 1");
    }

    @Test
    public void lambdaQueryBuilder_4() {
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .groupBy(TbUser::getIndex)          //
                    .eq(TbUser::getAccount, "b"); // after groupBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("condition is locked.");
        }
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .orderBy(TbUser::getIndex)          //
                    .eq(TbUser::getAccount, "b"); // << --- after orderBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("condition is locked.");
        }
        //
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.lambdaSelect(TbUser.class).eq(TbUser::getAccount, "a").eq(TbUser::getAccount, "b").apply("limit 1")//
                    .groupBy(TbUser::getIndex)  //
                    .orderBy(TbUser::getIndex)  //
                    .groupBy(TbUser::getIndex); // << --- after orderBy is Error.
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("group by is locked.");
        }
    }
}






















