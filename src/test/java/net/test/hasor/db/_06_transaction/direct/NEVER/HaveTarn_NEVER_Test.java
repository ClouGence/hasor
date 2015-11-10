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
package net.test.hasor.db._06_transaction.direct.NEVER;
import static net.test.hasor.test.utils.HasorUnit.newID;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DSManager;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionStatus;
import net.test.hasor.db._06_transaction.direct.AbstractNativesJDBCTest;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.test.junit.ContextConfiguration;
import net.test.hasor.test.runner.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * RROPAGATION_NEVER：排除事务
 *   -条件：环境中有事务，事务管理器会引发异常。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class HaveTarn_NEVER_Test extends AbstractNativesJDBCTest {
    @Test
    public void haveTarn_NEVER_Test() throws Exception {
        System.out.println("--->>haveTarn_NEVER_Test<<--");
        Thread.sleep(1000);
        /* 执行步骤：
         *   T1   ，开启事务                                 (不打印).
         *   T1   ，新建‘默罕默德’用户           (不打印).
         *      T2，开启事务                                (打印：Existing transaction found for transaction marked with propagation 'never').
         *   T1   ，新建‘赵飞燕’用户               (不打印).
         *   T1   ，递交事务                                 (打印：默罕默德，赵飞燕).
         */
        Connection conn = DSManager.getConnection(getDataSource());//申请连接
        {
            /*T1-Begin*/
            System.out.println("begin T1!");
            conn.setAutoCommit(false);
            Thread.sleep(1000);
        }
        {
            /*T1*/
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        {
            /*T2*/
            try {
                this.executeTransactional();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            Thread.sleep(1000);
        }
        {
            /*T1*/
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        {
            /*T1-Commit*/
            System.out.println("commit T1!");
            conn.commit();
            conn.setAutoCommit(true);
            Thread.sleep(1000);
        }
        DSManager.releaseConnection(conn, getDataSource());//释放连接
    }
    //
    //
    public void executeTransactional() throws Exception {
        /*T2-Begin*/
        System.out.println("begin T2!");
        TransactionStatus tranStatus = begin(Propagation.NEVER);
        Thread.sleep(1000);
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        /*T2-rollBack*/
        System.out.println("rollBack T2!");
        rollBack(tranStatus);
    }
}