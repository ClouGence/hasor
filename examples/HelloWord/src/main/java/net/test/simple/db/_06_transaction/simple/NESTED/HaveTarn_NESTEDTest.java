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
package net.test.simple.db._06_transaction.simple.NESTED;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.TransactionStatus;
import net.test.simple.db._06_transaction.natives.AbstractSimpleTransactionManagerTest;
import org.junit.Test;
/**
 * RROPAGATION_NESTED：嵌套事务
 *   -条件：环境中有事务，事务管理器使用保存点管理嵌套事务。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class HaveTarn_NESTEDTest extends AbstractSimpleTransactionManagerTest {
    protected Isolation getWatchThreadTransactionLevel() {
        /*监控线程的事务隔离级别修改为，允许读未递交的数据*/
        return Isolation.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED);
    }
    @Test
    public void haveTarn_NESTEDTest() throws SQLException, InterruptedException {
        System.out.println("--->>haveTarn_NESTEDTest<<--");
        watchTable("TB_User");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..            (T1)
         *   2.新建‘默罕默德’用户..
         *   3.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’).
         *   4.开启事务..            (T2)
         *   5.新建‘安妮.贝隆’用户..
         *   6.暂停3秒，监控线程打印全表数据.(包含‘安妮.贝隆’).
         *   7.回滚事务..            (T2)
         *   8.暂停3秒，监控线程打印变更之后的全表数据(‘安妮.贝隆’数据不再存在).
         *   9.新建‘赵飞燕’用户..
         *   a.递交事务..            (T1)
         *   b.暂停3秒，监控线程打印变更之后的全表数据(仅包含‘默罕默德’、‘赵飞燕’).
         */
        Connection conn = DataSourceUtils.getConnection(getDataSource());//申请连接
        /*T1-Begin*/
        {
            conn.setAutoCommit(false);//----begin T1
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        /*T2-Begin*/
        TransactionStatus tranStatus = begin(Propagation.NESTED);
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        /*T2-Commit*/
        {
            System.out.println("rollBack Transaction!");
            rollBack(tranStatus);
            Thread.sleep(3000);
        }
        /*T1-Commit*/
        {
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
            //
            conn.commit();
            conn.setAutoCommit(true);
        }
        Thread.sleep(3000);
        DataSourceUtils.releaseConnection(conn, getDataSource());//释放连接
    }
}