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
import net.test.simple.db._06_transaction.AbstractSimpleTransactionManagerTest;
import org.junit.Test;
/**
 * RROPAGATION_NESTED：嵌套事务
 *   -条件：环境中没有事务，开始的事务是一个新事务
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class NoTarn_NESTEDTest extends AbstractSimpleTransactionManagerTest {
    @Test
    public void noTarn_NESTEDTest() throws SQLException, InterruptedException {
        System.out.println("--->>noTarn_NESTEDTest<<--");
        watchTable("TB_User");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..
         *   2.新建‘安妮.贝隆’用户..
         *   3.暂停3秒，监控线程一直打印“table no change.”（数据已经插入但是没有递交事务因此监控线程得不到最新改动，因而继续打印“table no change.”）
         *   4.递交事务..
         *   5.暂停3秒，监控线程打印变更之后的全表数据.
         */
        /*Begin*/
        TransactionStatus tranStatus = begin(TransactionBehavior.PROPAGATION_NESTED);
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        //
        /*commit，递交事务*/
        System.out.println("commit Transaction!");
        commit(tranStatus);
        Thread.sleep(3000);
    }
}