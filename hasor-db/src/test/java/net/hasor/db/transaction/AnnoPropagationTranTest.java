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
package net.hasor.db.transaction;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.IgnoreProxy;
import net.hasor.core.Inject;
import net.hasor.db.Transactional;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.SQLException;

import static net.hasor.test.db.utils.TestUtils.*;

/**
 * 事务传播属性
 * @version : 2015年11月10日
 * @author 赵永春 (zyc@hasor.net)
 */
@IgnoreProxy(ignore = false)
public class AnnoPropagationTranTest extends AbstractDbTest {
    @Inject
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresnewTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void nestedTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void supportsTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notsupportedTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void neverTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatoryTranTest(ConsumerThrow callBack) throws SQLException {
        this.jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData5());
        callBack.accept(this.jdbcTemplate);
    }
    // ----------------------------------------------------

    @Test
    public void tran_required_test_1() throws Throwable {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(false))) {
            // .REQUIRED：尝试加入已经存在的事务中，在测试开始之前先构建一个独立事务
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
            temp.execute((TransactionCallbackWithoutResult) tranStatus -> {
                /*T1 - 赵子龙*/
                jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData4());
                assert tableCountWithCurrent(jdbcTemplate) == 1;
                assert tableCountWithNew(jdbcTemplate) == 0;
                /*T2 - 诸葛亮*/
                appContext.getInstance(AnnoPropagationTranTest.class).requiredTranTest(jdbcTemplate1 -> {
                    assert tableCountWithCurrent(jdbcTemplate) == 2;
                    assert tableCountWithNew(jdbcTemplate) == 0;
                });
                /*T3 - 张果老*/
                jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData6());
                assert tableCountWithCurrent(jdbcTemplate) == 3;
                assert tableCountWithNew(jdbcTemplate) == 0;
            }, Propagation.REQUIRES_NEW);
            //
            //
            printMapList(jdbcTemplate.queryForList("select * from tb_user"));
            assert tableCountWithCurrent(jdbcTemplate) == 3;// 最后事务都递交了
            assert tableCountWithNew(jdbcTemplate) == 3;    // 最后事务都递交了
        }
    }
}
