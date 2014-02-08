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
package net.hasor.plugins.transaction.support;
import static net.hasor.plugins.transaction.TransactionBehavior.PROPAGATION_MANDATORY;
import static net.hasor.plugins.transaction.TransactionBehavior.PROPAGATION_NESTED;
import static net.hasor.plugins.transaction.TransactionBehavior.PROPAGATION_NEVER;
import static net.hasor.plugins.transaction.TransactionBehavior.PROPAGATION_NOT_SUPPORTED;
import static net.hasor.plugins.transaction.TransactionBehavior.PROPAGATION_REQUIRED;
import static net.hasor.plugins.transaction.TransactionBehavior.RROPAGATION_REQUIRES_NEW;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.jdbc.datasource.DataSourceUtils;
import net.hasor.jdbc.datasource.local.ConnectionSequence;
import net.hasor.jdbc.datasource.local.LocalDataSourceHelper;
import net.hasor.jdbc.exceptions.IllegalTransactionStateException;
import net.hasor.jdbc.exceptions.TransactionDataAccessException;
import net.hasor.plugins.transaction.TransactionBehavior;
import net.hasor.plugins.transaction.TransactionLevel;
import net.hasor.plugins.transaction.TransactionManager;
import net.hasor.plugins.transaction.TransactionStatus;
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
public abstract class DefaultTransactionManager implements TransactionManager {
    private int                                  defaultTimeout = -1;
    private LinkedList<DefaultTransactionStatus> tStatusStack   = new LinkedList<DefaultTransactionStatus>();
    private DataSource                           dataSource     = null;
    public DefaultTransactionManager(DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource);
        this.dataSource = dataSource;
    }
    //
    //
    /**获取当前事务管理器管理的数据源对象。*/
    public DataSource getDataSource() {
        return this.dataSource;
    };
    /**是否存在未处理完的事务（包括被挂起的事务）。*/
    public boolean hasTransaction() {
        return !tStatusStack.isEmpty();
    }
    /**测试事务状态是否位于栈顶。*/
    public boolean isTopTransaction(TransactionStatus status) {
        if (tStatusStack.isEmpty())
            return false;
        return this.tStatusStack.peek() == status;
    }
    //
    //
    /**开启事务*/
    public final TransactionStatus getTransaction(TransactionBehavior behavior) throws TransactionDataAccessException {
        return getTransaction(behavior, TransactionLevel.ISOLATION_DEFAULT);
    };
    /**开启事务*/
    public final TransactionStatus getTransaction(TransactionBehavior behavior, TransactionLevel level) throws TransactionDataAccessException {
        Hasor.assertIsNotNull(behavior);
        Hasor.assertIsNotNull(level);
        //
        TransactionObject tranConn = doGetConnection();/*获取新的连接（线程绑定的）*/
        DefaultTransactionStatus defStatus = new DefaultTransactionStatus(behavior, level, tranConn);
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
        if (this.isExistingTransaction(tranConn) == true) {
            /*RROPAGATION_REQUIRES_NEW：独立事务*/
            if (behavior == RROPAGATION_REQUIRES_NEW) {
                this.suspend(defStatus);/*挂起事务*/
                tranConn = doGetConnection();/*重新申请连接*/
                defStatus.setTranConn(tranConn);
                this.doBegin(defStatus);/*开启新事务*/
            }
            /*PROPAGATION_NESTED：嵌套事务*/
            if (behavior == PROPAGATION_NESTED) {
                defStatus.markHeldSavepoint();/*设置保存点*/
            }
            /*PROPAGATION_NOT_SUPPORTED：非事务方式*/
            if (behavior == PROPAGATION_NOT_SUPPORTED) {
                this.suspend(defStatus);/*挂起事务*/
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
            this.doBegin(defStatus);/*开启新事务*/
        }
        /*PROPAGATION_MANDATORY：强制要求事务*/
        if (behavior == PROPAGATION_MANDATORY)
            throw new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'");
        return defStatus;
    }
    /**使用defStatus中的连接开启一个全新的事务。*/
    protected void doBegin(DefaultTransactionStatus defStatus) {
        try {
            TransactionObject tranConn = defStatus.getTranConn();
            tranConn.beginTransaction();
            defStatus.markNewConnection();/*新事物，新连接*/
            this.tStatusStack.push(defStatus);/*入栈*/
            //
        } catch (Throwable ex) {
            throw new TransactionDataAccessException(ex);
        }
    }
    /**判断连接对象是否处于事务中，该方法会用于评估事务传播属性的处理方式。 */
    private boolean isExistingTransaction(TransactionObject tranConn) throws TransactionDataAccessException {
        try {
            return tranConn.hasTransaction();
        } catch (Throwable e) {
            throw new TransactionDataAccessException(e);
        }
    };
    //
    //
    /**递交事务*/
    public final void commit(TransactionStatus status) throws TransactionDataAccessException {
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
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
                doCommit(defStatus);
            //
        } catch (Throwable ex) {
            rollBack(defStatus);/*递交失败，回滚*/
            throw new TransactionDataAccessException(ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    /**递交前的预处理*/
    private void prepareCommit(DefaultTransactionStatus defStatus) {
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
    protected void doCommit(DefaultTransactionStatus defStatus) throws SQLException {
        TransactionObject tranObject = defStatus.getTranConn();
        tranObject.commit();
    };
    //
    //
    /**回滚事务*/
    public final void rollBack(TransactionStatus status) throws TransactionDataAccessException {
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
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
                doRollback(defStatus);
            //
        } catch (Throwable ex) {
            throw new TransactionDataAccessException("SQL Exception :", ex);
        } finally {
            cleanupAfterCompletion(defStatus);
        }
    }
    /**回滚前的预处理*/
    private void prepareRollback(DefaultTransactionStatus defStatus) {
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
    protected void doRollback(DefaultTransactionStatus defStatus) throws SQLException {
        TransactionObject tranObject = defStatus.getTranConn();
        tranObject.rollback();
    };
    //
    //
    /**挂起事务。*/
    protected final void suspend(DefaultTransactionStatus defStatus) {
        /*检查事务是否为栈顶事务*/
        prepareCheckStack(defStatus);
        /*挂起事务*/
        if (defStatus.isSuspend() == false) {
            defStatus.setSuspendConn(defStatus.getTranConn());
            defStatus.setTranConn(null);
            //暂时储藏当前线程的数据库连接
            SyncTransactionManager.inStack(this.getDataSource());
        }
    }
    /**恢复被挂起的事务。*/
    protected final void resume(DefaultTransactionStatus defStatus) {
        if (defStatus.isCompleted() == false)
            throw new IllegalTransactionStateException("the Transaction has not completed.");
        if (defStatus.isSuspend() == false)
            throw new IllegalTransactionStateException("the Transaction has not Suspend.");
        //
        /*检查事务是否为栈顶事务*/
        prepareCheckStack(defStatus);
        /*恢复挂起的事务*/
        if (defStatus.isSuspend() == true) {
            TransactionObject tranConn = defStatus.getSuspendConn();
            defStatus.setTranConn(tranConn);
            defStatus.setSuspendConn(null);
            //恢复储藏的数据库连接
            SyncTransactionManager.outStack(this.getDataSource());
        }
    }
    //
    //
    /**检查正在处理的事务状态是否位于栈顶，否则抛出异常*/
    private void prepareCheckStack(DefaultTransactionStatus defStatus) {
        if (!this.isTopTransaction(defStatus))
            throw new IllegalTransactionStateException("the Transaction Status is not top in stack.");
    }
    /**commit,rollback。之后的清理工作，同时也负责恢复事务和操作事务堆栈。*/
    private void cleanupAfterCompletion(DefaultTransactionStatus defStatus) {
        /*清理的事务必须是位于栈顶*/
        prepareCheckStack(defStatus);
        /*标记完成*/
        defStatus.setCompleted();
        /*出栈*/
        this.tStatusStack.pop();
        /*恢复挂起的事务*/
        if (defStatus.isSuspend())
            this.resume(defStatus);
        /*释放资源*/
        if (defStatus.isNewConnection())
            this.doReleaseConnection(defStatus.getTranConn());
        /*清理defStatus*/
        defStatus.setTranConn(null);
        defStatus.setSuspendConn(null);
    }
    //
    //
    /**获取连接（线程绑定的）*/
    protected TransactionObject doGetConnection() {
        LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DataSourceUtils.getDataSourceHelper();
        return SyncTransactionManager.getTransaction(getDataSource());
    };
    /**获取连接（线程绑定的）*/
    protected void doReleaseConnection(TransactionObject tranObject) {
        return SyncTransactionManager.getTransaction(getDataSource());
    };
}
/** */
class SyncTransactionManager {
    /**储藏*/
    public static void inStack(DataSource dataSource) {
        try {
            LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DataSourceUtils.getDataSourceHelper();
            localHelper.getConnectionSequence(dataSource).push(null);/*清除线程上的事务，将事务挂起到*/
            //2.重新取得当前连接
            localHelper.getConnection(dataSource);
        } catch (SQLException e) {
            throw new TransactionDataAccessException(e);
        }
    }
    /**恢复*/
    public static void outStack(DataSource dataSource) {
        //
        LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DataSourceUtils.getDataSourceHelper();
        ConnectionSequence connSeq = localHelper.getConnectionSequence(dataSource);
        connSeq.pop();/*1.*/
        connSeq.pop();/*2.*/
    }
}