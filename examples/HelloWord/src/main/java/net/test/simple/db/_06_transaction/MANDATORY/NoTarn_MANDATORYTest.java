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
package net.test.simple.db._06_transaction.MANDATORY;
import java.sql.SQLException;
import net.hasor.db.transaction.TransactionBehavior;
import net.hasor.db.transaction.TransactionStatus;
import net.test.simple.db._06_transaction.AbstractSimpleTransactionManagerTest;
import org.junit.Test;
/**
 * PROPAGATION_MANDATORY：要求环境中存在事务
 *   -条件：环境中没有事务，事务管理器会引发异常。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class NoTarn_MANDATORYTest extends AbstractSimpleTransactionManagerTest {
    @Test
    public void noTarn_MANDATORYTest() throws SQLException, InterruptedException {
        System.out.println("--->>noTarn_MANDATORYTest<<--");
        watchTable("TB_User");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..  (引发异常，因为环境中不存在事务)
         */
        /*Begin*/
        TransactionStatus tranStatus = begin(TransactionBehavior.PROPAGATION_MANDATORY);
    }
}