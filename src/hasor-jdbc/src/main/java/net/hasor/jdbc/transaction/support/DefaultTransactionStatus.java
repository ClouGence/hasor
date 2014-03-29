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
package net.hasor.jdbc.transaction.support;
import java.sql.SQLException;
import java.sql.Savepoint;
import net.hasor.jdbc.datasource.SavepointManager;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionLevel;
import net.hasor.jdbc.transaction.TransactionStatus;
/**
 * 表示一个用于管理事务的状态点
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultTransactionStatus implements TransactionStatus {
    private Savepoint           savepoint     = null; //事务保存点
    private TransactionObject   tranConn      = null; //当前事务使用的数据库连接
    private TransactionObject   suspendConn   = null; //当前事务之前挂起的上一个数据库事务
    private TransactionBehavior behavior      = null; //传播属性
    private TransactionLevel    level         = null; //隔离级别
    private boolean             completed     = false; //完成（true表示完成）
    private boolean             rollbackOnly  = false; //要求回滚（true表示回滚）
    private boolean             newConnection = false; //是否使用了一个全新的数据库连接开启事务（true表示新连接）
    private boolean             readOnly      = false; //只读模式（true表示只读）
    //
    public DefaultTransactionStatus(TransactionBehavior behavior, TransactionLevel level) {
        this.behavior = behavior;
        this.level = level;
    }
    //
    //
    //
    private SavepointManager getSavepointManager() {
        return this.tranConn.getSavepointManager();
    }
    public void markHeldSavepoint() throws SQLException {
        if (this.hasSavepoint())
            throw new SQLException("TransactionStatus has Savepoint");
        if (this.getSavepointManager().supportSavepoint() == false)
            throw new SQLException("SavepointManager does not support Savepoint.");
        //
        this.savepoint = this.getSavepointManager().createSavepoint();
    }
    public void releaseHeldSavepoint() throws SQLException {
        if (this.hasSavepoint() == false)
            throw new SQLException("TransactionStatus has not Savepoint");
        if (this.getSavepointManager().supportSavepoint() == false)
            throw new SQLException("SavepointManager does not support Savepoint.");
        //
        this.getSavepointManager().releaseSavepoint(this.savepoint);
    }
    public void rollbackToHeldSavepoint() throws SQLException {
        if (this.hasSavepoint() == false)
            throw new SQLException("TransactionStatus has not Savepoint");
        if (this.getSavepointManager().supportSavepoint() == false)
            throw new SQLException("SavepointManager does not support Savepoint.");
        //
        this.getSavepointManager().rollbackToSavepoint(this.savepoint);
    }
    /*设置完成状态*/
    void setCompleted() {
        this.completed = true;
    }
    /*标记使用的是全新连接*/
    void markNewConnection() {
        this.newConnection = true;
    }
    TransactionObject getTranConn() {
        return tranConn;
    }
    void setTranConn(TransactionObject tranConn) {
        this.tranConn = tranConn;
    }
    TransactionObject getSuspendConn() {
        return suspendConn;
    }
    void setSuspendConn(TransactionObject suspendConn) {
        this.suspendConn = suspendConn;
    }
    //
    //
    //
    public TransactionBehavior getTransactionBehavior() {
        return this.behavior;
    }
    public TransactionLevel getIsolationLevel() {
        return this.level;
    }
    public boolean isCompleted() {
        return this.completed;
    }
    public boolean isRollbackOnly() {
        return this.rollbackOnly;
    }
    public boolean isReadOnly() {
        return this.readOnly;
    }
    public boolean isNewConnection() {
        return this.newConnection;
    }
    public boolean isSuspend() {
        return (this.suspendConn != null) ? true : false;
    }
    public boolean hasSavepoint() {
        return (this.savepoint != null) ? true : false;
    }
    public void setRollbackOnly() throws SQLException {
        if (this.isCompleted())
            throw new SQLException("Transaction is already completed.");
        this.rollbackOnly = true;
    }
    public void setReadOnly() throws SQLException {
        if (this.isCompleted())
            throw new SQLException("Transaction is already completed.");
        this.readOnly = true;
    }
}