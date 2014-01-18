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
package net.hasor.plugins.transaction.core;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 
 * @version : 2014-1-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultPlatformTransactionManager extends AbstractPlatformTransactionManager {
    protected boolean isExistingTransaction(Object transaction) {
        TransactionObject tranObject = (TransactionObject) transaction;
        boolean autoMark = tranObject.getConnectionHolder().getConnection().getAutoCommit();
        /*当autoCommit为true时表示已经开启了一个事务*/
        if (autoMark == true)
            return false;
        else
            return true;
    }
    protected void doBegin(Object transaction) throws SQLException {
        TransactionObject tranObject = (TransactionObject) transaction;
        Connection conn = tranObject.getConnectionHolder().getConnection();
        boolean autoMark = conn.getAutoCommit();
        if (autoMark == true)
            conn.setAutoCommit(false);//将连接autoCommit设置为false，意义为手动递交事务。
    }
    protected void doCommit(Object transaction) throws SQLException {
        TransactionObject tranObject = (TransactionObject) transaction;
        Connection conn = tranObject.getConnectionHolder().getConnection();
        conn.commit();
    }
    protected void doRollback(Object transaction) throws SQLException {
        TransactionObject tranObject = (TransactionObject) transaction;
        Connection conn = tranObject.getConnectionHolder().getConnection();
        conn.rollback();
    }
    protected TransactionObject doGetTransaction() {
        // TODO Auto-generated method stub
        return null;
    }
    //
    protected void doSuspend(Object transaction) throws SQLException {
        // TODO Auto-generated method stub
    }
    protected void doResume(Object resumeTransaction) throws SQLException {
        // TODO Auto-generated method stub
    }
}