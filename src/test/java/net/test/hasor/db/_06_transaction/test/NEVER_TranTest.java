/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.hasor.db._06_transaction.test;
import java.sql.Connection;
import net.hasor.db.Transactional;
import net.hasor.db.datasource.DSManager;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.test.hasor.db._06_transaction.AbstractNativesJDBCTest;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.junit.ContextConfiguration;
import net.test.hasor.junit.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
* PROPAGATION_SUPPORTS：跟随环境
*   -条件：环境中有事务，事务管理器正常运行。
* @version : 2013-12-10
* @author 赵永春(zyc@hasor.net)
*/
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class NEVER_TranTest extends AbstractNativesJDBCTest {
    // - 事务1
    @Transactional(propagation = Propagation.NEVER)
    protected void doTransactionalA(final JdbcTemplate jdbcTemplate) throws Throwable {
        super.doTransactionalA(jdbcTemplate);
    }
    // - 事务2
    @Transactional(propagation = Propagation.NEVER)
    protected void doTransactionalB(final JdbcTemplate jdbcTemplate) throws Throwable {
        super.doTransactionalB(jdbcTemplate);
    }
    //
    //
    @Test
    public void yesTarn_Test() throws Throwable {
        System.out.println("--->>haveTarn_REQUIRED_New_Test<<--");
        Thread.sleep(1000);
        /* 执行步骤：
         *   T1   ，开启事务                                 (不打印).
         *   T1   ，新建‘默罕默德’用户           (不打印).
         *      T2，开启事务                                (打印：Existing transaction found for transaction marked with propagation 'never').
         *   T1   ，新建‘赵飞燕’用户               (不打印).
         *   T1   ，递交事务                                 (打印：默罕默德，赵飞燕).
         */
        Connection conn = DSManager.getConnection(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        doTransactionalA(jdbcTemplate);
        DSManager.releaseConnection(conn, dataSource);
    }
    @Test
    public void noTarn_Test() throws Throwable {
        System.out.println("--->>noTarn_REQUIRED_New_Test<<--");
        Thread.sleep(1000);
        /* 执行步骤：
         *   T1   ，新建‘默罕默德’用户           (打印：默罕默德).
         *      T2，开启事务                                (不打印).
         *      T2，新建‘安妮.贝隆’用户        (打印：默罕默德、安妮.贝隆).
         *      T2，回滚事务                                 (不打印).
         *   T1   ，新建‘赵飞燕’用户               (打印：默罕默德、安妮.贝隆、赵飞燕).
         */
        Connection conn = DSManager.getConnection(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        doTransactionalA(jdbcTemplate);
        DSManager.releaseConnection(conn, dataSource);
    }
}