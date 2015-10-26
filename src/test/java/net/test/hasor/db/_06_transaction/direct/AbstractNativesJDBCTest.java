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
package net.test.hasor.db._06_transaction.direct;
import java.sql.SQLException;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TranManager;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.test.hasor.db._06_transaction.plugins.AbstractSimpleJDBCTest;
import org.junit.Before;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractNativesJDBCTest extends AbstractSimpleJDBCTest {
    private TransactionManager transactionManager = null;
    @Before
    public void initTran() {
        this.transactionManager = TranManager.getManager(this.getDataSource());
    }
    /**开始事物*/
    protected TransactionStatus begin(Propagation behavior) throws SQLException {
        return this.transactionManager.getTransaction(behavior);
    }
    /**递交事物*/
    protected void commit(TransactionStatus status) throws SQLException {
        this.transactionManager.commit(status);
    }
    /**回滚事物*/
    protected void rollBack(TransactionStatus status) throws SQLException {
        this.transactionManager.rollBack(status);
    }
}