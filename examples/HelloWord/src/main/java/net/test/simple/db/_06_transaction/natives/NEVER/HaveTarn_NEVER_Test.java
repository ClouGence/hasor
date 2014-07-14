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
package net.test.simple.db._06_transaction.natives.NEVER;
import static net.hasor.test.utils.HasorUnit.newID;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.test.simple.db.SimpleJDBCWarp;
import net.test.simple.db._06_transaction.natives.AbstractNativesJDBCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * RROPAGATION_NEVER：排除事务
 *   -条件：环境中有事务，事务管理器会引发异常。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = SimpleJDBCWarp.class)
public class HaveTarn_NEVER_Test extends AbstractNativesJDBCTest {
    protected String watchTable() {
        return "TB_User";
    }
    @Test
    public void haveTarn_NEVER_Test() throws Exception {
        System.out.println("--->>haveTarn_NEVER_Test<<--");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..            (T1)
         *   2.新建‘默罕默德’用户..
         *   3.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’).
         *   4.开启事务..            (T2)
         *   5.打印异常信息，因为环境中不存在事务
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
        try {
            TransactionStatus tranStatus = begin(Propagation.NEVER);
            throw new Exception("执行逻辑出错，不该出现此类问题。");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}