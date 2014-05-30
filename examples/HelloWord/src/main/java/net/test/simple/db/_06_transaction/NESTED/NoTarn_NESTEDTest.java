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
package net.test.simple.db._06_transaction.NESTED;
import java.sql.SQLException;
import net.hasor.db.transaction.TransactionBehavior;
import net.hasor.db.transaction.TransactionStatus;
import net.test.simple.db._06_transaction.AbstractTransactionManagerJDBCTest;
import org.junit.Test;
/**
 * RROPAGATION_NESTED：嵌套事务
 *   -条件：环境中没有事务，开始的事务是一个新事务
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class NoTarn_NESTEDTest extends AbstractTransactionManagerJDBCTest {
    @Test
    public void noTarn_Test() throws SQLException, InterruptedException {
        /* 预期执行结果为：
         *   1.打印 TB_User表内容。
         *   2.打印“table no change.”  <-- 输出2~3次
         *   3.打印“insert new User ‘安妮.贝隆’...”
         *   4.打印“insert complete!”
         *   5.打印“table no change.”  <-- 继续打印若干次
         *   6.打印“commit Transaction”
         *   7.打印 TB_User表内容。
         *   ...
         */
        watchTable("TB_User");
        Thread.sleep(3000);
        /*Begin*/
        TransactionStatus tranStatus = begin(TransactionBehavior.PROPAGATION_NESTED);
        {
            /*
             * 1.监视线程每隔1秒就会打印一次数据是否发生变化。
             * 2.当插入数据之后不马上递交事务，暂停执行三秒，监视线程应该是监视不到改变才对。
             */
            String insertUser = "insert into TB_User values('deb4f4c8-5ba1-4f76-8b4a-c2be028bf57b','安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser);//执行插入语句
            System.out.println("insert complete!");
            Thread.sleep(3000);
        }
        //
        /*commit，递交事务*/
        System.out.println("commit Transaction!");
        commit(tranStatus);
        Thread.sleep(3000);
    }
}