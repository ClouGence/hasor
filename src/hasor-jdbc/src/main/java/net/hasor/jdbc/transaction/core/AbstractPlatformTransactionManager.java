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
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_MANDATORY;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NESTED;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NEVER;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NOT_SUPPORTED;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_REQUIRED;
import static net.hasor.jdbc.transaction.TransactionBehavior.RROPAGATION_REQUIRES_NEW;
import java.sql.SQLException;
import net.hasor.Hasor;
import net.hasor.jdbc.IllegalTransactionStateException;
import net.hasor.jdbc.TransactionDataAccessException;
import net.hasor.jdbc.TransactionSuspensionNotSupportedException;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionStatus;
import net.hasor.jdbc.transaction._.TransactionSynchronizationManager;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractPlatformTransactionManager {
    private int defaultTimeout = -1;
    /**开启事务*/
    public final TransactionStatus getTransaction(TransactionBehavior behavior) throws TransactionDataAccessException {
        Object transaction = doGetTransaction();//获取目前事务对象
        AbstractTransactionStatus defStatus = new AbstractTransactionStatus(behavior, transaction);
        /*-------------------------------------------------------------
        |                      环境已经存在事务
        |
        | PROPAGATION_REQUIRED     ：加入已有事务（不处理）
        | RROPAGATION_REQUIRES_NEW ：独立事务（挂起当前事务，开启新事务）
        | PROPAGATION_NESTED       ：嵌套事务（设置保存点）
        | PROPAGATION_SUPPORTS     ：跟随环境（不处理）
        | PROPAGATION_NOT_SUPPORTED：非事务方式（仅挂起当前事务）
        | PROPAGATION_NEVER        ：排除事务（异常）
        | PROPAGATION_MANDATORY    ：强制要求事务（不处理）
        ===============================================================*/
        if (this.isExistingTransaction(transaction) == true) {
            /*RROPAGATION_REQUIRES_NEW：独立事务*/
            if (behavior == RROPAGATION_REQUIRES_NEW) {
                Object suspendHolder = this.suspend(transaction, defStatus);/*挂起当前事务*/
                defStatus.setSuspendHolder(suspendHolder);
                this.processBegin(transaction, defStatus);/*开启一个新的事务*/
            }
            /*PROPAGATION_NESTED：嵌套事务*/
            if (behavior == PROPAGATION_NESTED) {
                defStatus.markHeldSavepoint();/*设置保存点*/
            }
            /*PROPAGATION_NOT_SUPPORTED：非事务方式*/
            if (behavior == PROPAGATION_NOT_SUPPORTED) {
                Object suspendHolder = this.suspend(transaction, defStatus);/*挂起当前事务*/
                defStatus.setSuspendHolder(suspendHolder);
            }
            /*PROPAGATION_NEVER：排除事务*/
            if (behavior == PROPAGATION_NEVER)
                throw new IllegalTransactionStateException("Existing transaction found for transaction marked with propagation 'never'");
            return defStatus;
        }
        /*-------------------------------------------------------------
        |                      环境不经存在事务
        |
        | PROPAGATION_REQUIRED     ：加入已有事务（开启新事务）
        | RROPAGATION_REQUIRES_NEW ：独立事务（开启新事务）
        | PROPAGATION_NESTED       ：嵌套事务（开启新事务）
        | PROPAGATION_SUPPORTS     ：跟随环境（不处理）
        | PROPAGATION_NOT_SUPPORTED：非事务方式（不处理）
        | PROPAGATION_NEVER        ：排除事务（不处理）
        | PROPAGATION_MANDATORY    ：强制要求事务（异常）
        ===============================================================*/
        /*PROPAGATION_REQUIRED：加入已有事务*/
        if (behavior == PROPAGATION_REQUIRED ||
        /*RROPAGATION_REQUIRES_NEW：独立事务*/
        behavior == RROPAGATION_REQUIRES_NEW ||
        /*PROPAGATION_NESTED：嵌套事务*/
        behavior == PROPAGATION_NESTED) {
            this.processBegin(transaction, defStatus);/*开启事务*/
        }
        /*PROPAGATION_MANDATORY：强制要求事务*/
        if (behavior == PROPAGATION_MANDATORY)
            throw new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'");
        return defStatus;
    };
    /**递交事务*/
    public final void commit(TransactionStatus status) throws TransactionDataAccessException {
        Object transaction = doGetTransaction();//获取目前事务对象
        AbstractTransactionStatus defStatus = (AbstractTransactionStatus) status;
        /*已完毕，不需要处理*/
        if (defStatus.isCompleted())
            throw new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        /*回滚情况*/
        if (defStatus.isRollbackOnly()) {
            if (Hasor.isDebugLogger())
                Hasor.debug("Transactional code has requested rollback");
            rollBack(defStatus);
            return;
        }
        /*-------------------------------------------------------------
        | 1.无论何种传播形式，递交事务操作都会将 isCompleted 属性置为 true。
        | 2.如果事务状态中包含一个未处理的保存点。仅递交保存点，而非递交整个事务。
        | 3.事务 isNew 只有为 true 时才真正触发递交事务操作。
        ===============================================================*/
        try {
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint())
                defStatus.releaseHeldSavepoint();
            else if (defStatus.isNew())
                doCommit(transaction, defStatus);
            //
        } catch (SQLException ex) {
            rollBack(defStatus);/*递交失败，回滚*/
            throw new TransactionDataAccessException("SQL Exception :", ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    /**回滚事务*/
    public final void rollBack(TransactionStatus status) throws TransactionDataAccessException {
        Object transaction = doGetTransaction();//获取目前事务对象
        AbstractTransactionStatus defStatus = (AbstractTransactionStatus) status;
        /*已完毕，不需要处理*/
        if (defStatus.isCompleted())
            throw new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        /*-------------------------------------------------------------
        | 1.无论何种传播形式，递交事务操作都会将 isCompleted 属性置为 true。
        | 2.如果事务状态中包含一个未处理的保存点。仅回滚保存点，而非回滚整个事务。
        | 3.事务 isNew 只有为 true 时才真正触发回滚事务操作。
        ===============================================================*/
        try {
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint())
                defStatus.rollbackToHeldSavepoint();
            else if (defStatus.isNew())
                doRollback(transaction, defStatus);
            //
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    //
    //
    //
    /**事务处理完之后的清理工作*/
    private void cleanupAfterCompletion(AbstractTransactionStatus defStatus) {
        defStatus.setCompleted();
        /*清空事务同步管理器*/
        if (defStatus.isNew())
            TransactionSynchronizationManager.clear();
        /*恢复挂起的事务*/
        if (defStatus.getSuspendedTransactionHolder() != null) {
            if (Hasor.isDebugLogger())
                Hasor.debug("Resuming suspended transaction after completion of inner transaction");
            resume(defStatus, (SuspendedTransactionHolder) defStatus.getSuspendedTransactionHolder());
        }
    }
    /**使用一个新的连接开启一个新的事务作为当前事务。请确保在调用该方法时候当前不存在事务。*/
    private void processBegin(Object transaction, AbstractTransactionStatus defStatus) {
        try {
            doBegin(transaction, defStatus);
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    /**恢复当前事务。请妥善处理当前事务之后在恢复挂起的事务*/
    protected void resume(AbstractTransactionStatus defStatus, Object transactionHolder) {
        try {
            doResume(defStatus, transactionHolder);
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    /**挂起当前事务，事务一旦被挂起会清空当前事务。*/
    protected Object suspend(Object transaction, AbstractTransactionStatus defStatus) {
        try {
            doSuspend(transaction, defStatus);
            SuspendedTransactionHolder suspendedHolder = new SuspendedTransactionHolder(transaction);
            //
            return suspendedHolder;
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    //
    //
    //
    /**恢复事务*/
    protected void doResume(AbstractTransactionStatus defStatus, Object transactionHolder) throws SQLException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }
    /**挂起事务*/
    protected void doSuspend(Object transaction, AbstractTransactionStatus defStatus) throws SQLException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }
    //
    //
    //
    private static class SuspendedTransactionHolder {
        private Object transaction;
        public SuspendedTransactionHolder(Object transaction) {
            this.transaction = transaction;
        }
    }
    //
    //
    //
    /**获取当前事务管理器中存在的事务对象。*/
    protected abstract Object doGetTransaction();
    /**判断当前事务管理器中是否已经存在开启了的事务。该方法会用于评估事务当前状态下事务传播行为操作方式。*/
    protected abstract boolean isExistingTransaction(Object transaction);
    /**开启事务*/
    protected abstract void doBegin(Object transaction, AbstractTransactionStatus status) throws SQLException;
    /**递交事务*/
    protected abstract void doCommit(Object transaction, AbstractTransactionStatus status) throws SQLException;
    /**回滚事务*/
    protected abstract void doRollback(Object transaction, AbstractTransactionStatus status) throws SQLException;
}