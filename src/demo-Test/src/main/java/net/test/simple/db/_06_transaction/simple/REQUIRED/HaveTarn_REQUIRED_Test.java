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
package net.test.simple.db._06_transaction.simple.REQUIRED;
import static net.hasor.test.utils.HasorUnit.newID;
import java.sql.Connection;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.test.simple.db.SimpleJDBCWarp;
import net.test.simple.db._06_transaction.simple.AbstractSimpleJDBCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * PROPAGATION_REQUIRED：加入已有的事务
 *   -条件：环境中有事务，事务管理器会将当前事务加入已有的事务中(commit,rollback均不生效)。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = SimpleJDBCWarp.class)
public class HaveTarn_REQUIRED_Test extends AbstractSimpleJDBCTest {
    protected Isolation getWatchThreadTransactionLevel() {
        /*监控线程的事务隔离级别修改为，允许读未递交的数据*/
        return Isolation.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED);
    }
    protected String watchTable() {
        return "TB_User";
    }
    @Test
    public void haveTarn_REQUIRED_Test() throws Exception {
        System.out.println("--->>haveTarn_REQUIRED_Test<<--");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..                     (T1)
         *   2.新建‘默罕默德’用户..
         *   3.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’).
         *   4.开启事务..                     (T2)
         *   5.新建‘安妮.贝隆’用户..
         *   6.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’、‘安妮.贝隆’).
         *   7.新建‘赵飞燕’用户..
         *   8.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’、‘安妮.贝隆’、‘赵飞燕’).
         *   9.回滚事务..                     (T2)
         *   a.暂停3秒，监控线程数据未改变一直打印：“watch : table no change.”
         *   b.新建‘吴广’用户..
         *   c.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’、‘安妮.贝隆’、‘赵飞燕’、‘吴广’).
         *   d.回滚事务..                     (T1)
         *   e.暂停3秒，监控线程数据未改变一直打印一张空表。
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
        TransactionStatus tranStatus = begin(Propagation.REQUIRED);
        System.out.println("begin Transaction!");
        //T1
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        {
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        {
            System.out.println("rollBack Transaction!");
            rollBack(tranStatus);//不生效
            Thread.sleep(3000);
        }
        {
            String insertUser = "insert into TB_User values(?,'吴广','wuguang','123','wuguang@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘吴广’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        /*T1-RollBack*/
        {
            conn.rollback();
            conn.setAutoCommit(true);
            Thread.sleep(3000);
            DataSourceUtils.releaseConnection(conn, getDataSource());//释放连接
        }
    }
}