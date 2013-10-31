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
package net.hasor.jdbc.transaction.core;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.noe.platform.modules.db.jdbcorm.jdbc.datasource.DataSourceUtils;
import net.hasor.Hasor;
import net.hasor.jdbc.transaction.TransactionSynchronizationManager;
import net.hasor.jdbc.transaction._.ConnectionHolder;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceTransactionManager extends AbstractPlatformTransactionManager {
    private DataSource dataSource;
    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    //
    //
    //
    protected Object doGetTransaction() {
        ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(this.dataSource);
        DataSourceTransactionObject dtObject = new DataSourceTransactionObject(connHolder);
        return dtObject;
    }
    protected boolean isExistingTransaction(Object transaction) {
        DataSourceTransactionObject dtObject = (DataSourceTransactionObject) transaction;
        return dtObject.getConnectionHolder().hasTransaction();
    }
    //
    //
    //
    protected void doBegin(Object transaction, DefaultTransactionStatus status) throws SQLException {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
        /*当前线程绑定的ConnectionHolder*/
        ConnectionHolder localHolder = txObject.getConnectionHolder();
        /*为事务管理器分配 Connection*/
        if (localHolder == null || status.isNew()) {
            Connection newCon = this.dataSource.getConnection();
            if (Hasor.isDebugLogger())
                Hasor.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
            localHolder = new ConnectionHolder(newCon);
            txObject.setConnectionHolder(localHolder, true);
        }
        /*取得事务隔离级别*/
        Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
        txObject.setPreviousIsolationLevel(previousIsolationLevel);
    }
    protected void doResume(DefaultTransactionStatus defStatus, Object resumeTransaction) throws SQLException {
        DataSourceTransactionObject dtObject = (DataSourceTransactionObject) resumeTransaction;
        // TODO Auto-generated method stub
    }
    protected void doSuspend(Object transaction, DefaultTransactionStatus defStatus) throws SQLException {
        // TODO Auto-generated method stub
    }
    protected void doCommit(Object transaction, DefaultTransactionStatus status) throws SQLException {
        // TODO Auto-generated method stub
    }
    protected void doRollback(Object transaction, DefaultTransactionStatus status) throws SQLException {
        // TODO Auto-generated method stub
    }
}