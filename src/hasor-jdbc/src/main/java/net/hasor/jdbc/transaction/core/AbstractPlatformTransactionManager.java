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
import java.util.LinkedList;
import net.hasor.Hasor;
import net.hasor.jdbc.IllegalTransactionStateException;
import net.hasor.jdbc.TransactionDataAccessException;
import net.hasor.jdbc.TransactionSuspensionNotSupportedException;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionLevel;
import net.hasor.jdbc.transaction.TransactionManager;
import net.hasor.jdbc.transaction.TransactionStatus;
/**
 * 某一个数据源的事务管理器
 * 
 * <p><b><i>事务栈：</i></b>
 * <p>事务管理器允许使用不同的传播属性反复开启新的事务。所有被开启的事务在正确处置（commit,rollback）
 * 它们之前都会按照先后顺序依次压入事务管理器的“事务栈”中。一旦有事务被处理（commit,rollback）这个事务才会被从事务栈中弹出。
 * <p>倘若被弹出的事务(A)并不是栈顶的事务，那么在事务(A)被处理（commit,rollback）时会优先处理自事务(A)以后开启的其它事务。
 * 
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractPlatformTransactionManager implements TransactionManager {
    private int                           defaultTimeout = -1;
    private LinkedList<TransactionStatus> tStatusStack   = new LinkedList<TransactionStatus>();
    //
    public boolean hasTransaction() {
        return !tStatusStack.isEmpty();
    }
    public boolean isTopTransaction(TransactionStatus status) {
        if (tStatusStack.isEmpty())
            return false;
        return this.tStatusStack.peek() == status;
    }
    //
    //
    //
    /**开启事务*/
    public final TransactionStatus getTransaction(TransactionBehavior behavior) throws TransactionDataAccessException {
        Hasor.assertIsNotNull(behavior);
        return getTransaction(behavior, TransactionLevel.ISOLATION_DEFAULT);
    };
    public final TransactionStatus getTransaction(TransactionBehavior behavior, TransactionLevel level) throws TransactionDataAccessException {
        Hasor.assertIsNotNull(behavior);
        Hasor.assertIsNotNull(level);
        Object transaction = doGetTransaction();//获取目前事务对象
        AbstractTransactionStatus defStatus = new AbstractTransactionStatus(behavior, level, transaction);
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
                this.suspend(transaction, defStatus);/*挂起当前事务*/
                this.processBegin(transaction, defStatus);/*开启一个新的事务*/
            }
            /*PROPAGATION_NESTED：嵌套事务*/
            if (behavior == PROPAGATION_NESTED) {
                defStatus.markHeldSavepoint();/*设置保存点*/
            }
            /*PROPAGATION_NOT_SUPPORTED：非事务方式*/
            if (behavior == PROPAGATION_NOT_SUPPORTED) {
                this.suspend(transaction, defStatus);/*挂起当前事务*/
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
    }
    /**使用一个新的连接开启一个新的事务作为当前事务。请确保在调用该方法时候当前不存在事务。*/
    private void processBegin(Object transaction, AbstractTransactionStatus defStatus) {
        try {
            doBegin(transaction, defStatus);
            this.tStatusStack.push(defStatus);/*入栈*/
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    /**判断当前事务对象是否已经处于事务中。该方法会用于评估事务传播属性的处理方式。*/
    protected abstract boolean isExistingTransaction(Object transaction);
    /**在当前连接上开启一个全新的事务*/
    protected abstract void doBegin(Object transaction, AbstractTransactionStatus defStatus) throws SQLException;
    //
    //
    //
    /**递交事务*/
    public final void commit(TransactionStatus status) throws TransactionDataAccessException {
        Object transaction = doGetTransaction();//获取底层维护的当前事务对象
        AbstractTransactionStatus defStatus = (AbstractTransactionStatus) status;
        /*已完毕，不需要处理*/
        if (defStatus.isCompleted())
            throw new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        /*回滚情况*/
        if (defStatus.isRollbackOnly()) {
            if (Hasor.isDebugLogger())
                Hasor.logDebug("Transactional code has requested rollback");
            rollBack(defStatus);
            return;
        }
        /*-------------------------------------------------------------
        | 1.无论何种传播形式，递交事务操作都会将 isCompleted 属性置为 true。
        | 2.如果事务状态中包含一个未处理的保存点。仅递交保存点，而非递交整个事务。
        | 3.事务 isNew 只有为 true 时才真正触发递交事务操作。
        ===============================================================*/
        try {
            prepareCommit(defStatus);
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint())
                defStatus.releaseHeldSavepoint();
            else if (defStatus.isNewConnection())
                doCommit(transaction, defStatus);
            //
        } catch (SQLException ex) {
            rollBack(defStatus);/*递交失败，回滚*/
            throw new TransactionDataAccessException("SQL Exception :", ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    /**递交前的预处理*/
    private void prepareCommit(AbstractTransactionStatus defStatus) {
        /*首先预处理的事务必须存在于管理器的事务栈内某一位置中，否则要处理的事务并非来源于该事务管理器。*/
        if (this.tStatusStack.contains(defStatus) == false)
            throw new IllegalTransactionStateException("This transaction is not derived from this Manager.");
        /*-------------------------------------------------------------
        | 如果预处理的事务并非位于栈顶，则进行弹栈操作。
        |--------------------------\
        | T5  ^   <-- pop-up       | 假定预处理的事务为 T4，那么：
        | T4  ^   <-- pop-up       | T5 事务会被先递交，然后是 T4
        | T3  .   <-- defStatus    | 接下来就完成了预处理。
        | T2                       |
        | T1                       |
        |--------------------------/
        |
        ===============================================================*/
        //
        TransactionStatus inStackStatus = null;
        while ((inStackStatus = this.tStatusStack.peek()) != defStatus)
            this.commit(inStackStatus);
    }
    /**处理当前底层数据库连接的事务递交操作。*/
    protected abstract void doCommit(Object transaction, AbstractTransactionStatus defStatus) throws SQLException;
    //
    //
    //
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
            prepareRollback(defStatus);
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint())
                defStatus.rollbackToHeldSavepoint();
            else if (defStatus.isNewConnection())
                doRollback(transaction, defStatus);
            //
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    /**回滚前的预处理*/
    private void prepareRollback(AbstractTransactionStatus defStatus) {
        /*首先预处理的事务必须存在于管理器的事务栈内某一位置中，否则要处理的事务并非来源于该事务管理器。*/
        if (this.tStatusStack.contains(defStatus) == false)
            throw new IllegalTransactionStateException("This transaction is not derived from this Manager.");
        /*-------------------------------------------------------------
        | 如果预处理的事务并非位于栈顶，则进行弹栈操作。
        |--------------------------\
        | T5  ^   <-- pop-up       | 假定预处理的事务为 T4，那么：
        | T4  ^   <-- pop-up       | T5 事务会被先回滚，然后是 T4
        | T3  .   <-- defStatus    | 接下来就完成了预处理。
        | T2                       |
        | T1                       |
        |--------------------------/
        |
        ===============================================================*/
        //
        TransactionStatus inStackStatus = null;
        while ((inStackStatus = this.tStatusStack.peek()) != defStatus)
            this.rollBack(inStackStatus);
    }
    /**处理当前底层数据库连接的事务回滚操作。*/
    protected abstract void doRollback(Object transaction, AbstractTransactionStatus defStatus) throws SQLException;
    //
    //
    //
    private static class SuspendedTransactionHolder {
        public Object transaction = null; /*挂起的底层事务对象*/
    }
    /**挂起当前事务*/
    protected final void suspend(Object transaction, AbstractTransactionStatus defStatus) {
        try {
            /*检查事务是否为栈顶事务*/
            prepareCheckStack(defStatus);
            /*创建 SuspendedTransactionHolder 对象，用于保存当前底层数据库连接以及事务对象*/
            doSuspend(transaction, defStatus);
            SuspendedTransactionHolder suspendedHolder = new SuspendedTransactionHolder();
            suspendedHolder.transaction = transaction;/*挂起的事务对象（来自于底层）*/
            defStatus.setSuspendHolder(suspendedHolder);
            //
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    /**挂起事务，子类需要重写该方法挂起transaction事务，并同时清空底层当前数据库连接，*/
    protected void doSuspend(Object transaction, AbstractTransactionStatus defStatus) throws SQLException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }
    /**恢复被挂起的事务，恢复挂起的事务时必须是当前事务请妥善处理当前事务之后在恢复挂起的事务*/
    protected final void resume(Object transaction, AbstractTransactionStatus defStatus) {
        if (defStatus.isCompleted() == false)
            throw new IllegalTransactionStateException("the Transaction has not completed.");
        try {
            /*检查事务是否为栈顶事务*/
            prepareCheckStack(defStatus);
            SuspendedTransactionHolder suspendedHolder = (SuspendedTransactionHolder) defStatus.getSuspendedTransactionHolder();
            doResume(suspendedHolder.transaction, defStatus);
        } catch (SQLException ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        }
    }
    /**恢复事务，恢复原本挂起的事务（第一个参数），并使用挂起的状态恢复当前数据库连接。*/
    protected void doResume(Object resumeTransaction, AbstractTransactionStatus defStatus) throws SQLException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }
    //
    //
    //
    /**检查正在处理的事务状态是否位于栈顶，否则抛出异常*/
    private void prepareCheckStack(AbstractTransactionStatus defStatus) {
        if (!this.isTopTransaction(defStatus))
            throw new IllegalTransactionStateException("the Transaction Status is not top in stack.");
    }
    /**commit,rollback。之后的清理工作，同时也负责恢复事务和操作事务堆栈。*/
    private void cleanupAfterCompletion(AbstractTransactionStatus defStatus) {
        /*清理的事务必须是位于栈顶*/
        prepareCheckStack(defStatus);
        /*标记完成*/
        defStatus.setCompleted();
        /*恢复挂起的事务*/
        if (defStatus.getSuspendedTransactionHolder() != null) {
            if (Hasor.isDebugLogger())
                Hasor.logDebug("Resuming suspended transaction after completion of inner transaction");
            resume(defStatus.getSuspendedTransactionHolder(), defStatus);
        }
    }
    /**获取当前事务管理器中存在的事务对象。*/
    protected abstract Object doGetTransaction();
}