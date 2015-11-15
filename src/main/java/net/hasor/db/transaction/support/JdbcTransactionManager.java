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
package net.hasor.db.transaction.support;
import static net.hasor.db.transaction.Propagation.MANDATORY;
import static net.hasor.db.transaction.Propagation.NESTED;
import static net.hasor.db.transaction.Propagation.NEVER;
import static net.hasor.db.transaction.Propagation.NOT_SUPPORTED;
import static net.hasor.db.transaction.Propagation.REQUIRED;
import static net.hasor.db.transaction.Propagation.REQUIRES_NEW;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.db.datasource.DSManager;
import net.hasor.db.datasource.local.ConnectionHolder;
import net.hasor.db.datasource.local.ConnectionSequence;
import net.hasor.db.datasource.local.LocalDataSourceHelper;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JdbcTransactionManager implements TransactionManager {
    protected Logger                          logger              = LoggerFactory.getLogger(getClass());
    private LinkedList<JdbcTransactionStatus> tStatusStack        = new LinkedList<JdbcTransactionStatus>();
    private DataSource                        dataSource          = null;
    private TransactionTemplateManager        transactionTemplate = null;
    protected JdbcTransactionManager(final DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource);
        this.dataSource = dataSource;
        this.transactionTemplate = new TransactionTemplateManager(this);
    }
    //
    //
    /**获取当前事务管理器管理的数据源对象。*/
    public DataSource getDataSource() {
        return this.dataSource;
    };
    /**是否存在未处理完的事务（包括被挂起的事务）。*/
    @Override
    public boolean hasTransaction() {
        return !this.tStatusStack.isEmpty();
    }
    /**测试事务状态是否位于栈顶。*/
    @Override
    public boolean isTopTransaction(final TransactionStatus status) {
        if (this.tStatusStack.isEmpty()) {
            return false;
        }
        return this.tStatusStack.peek() == status;
    }
    //
    //
    /**开启事务*/
    @Override
    public final TransactionStatus getTransaction(final Propagation behavior) throws SQLException {
        return this.getTransaction(behavior, Isolation.DEFAULT);
    };
    /**开启事务*/
    @Override
    public final TransactionStatus getTransaction(final Propagation behavior, final Isolation level) throws SQLException {
        Hasor.assertIsNotNull(behavior);
        Hasor.assertIsNotNull(level);
        //1.获取连接
        JdbcTransactionStatus defStatus = new JdbcTransactionStatus(behavior, level);
        defStatus.setTranConn(this.doGetConnection(defStatus));
        this.tStatusStack.addFirst(defStatus);/*入栈*/
        /*-------------------------------------------------------------
        |                      环境已经存在事务
        |
        | REQUIRED     ：加入已有事务（不处理）
        | REQUIRES_NEW ：独立事务（挂起当前事务，开启新事务）
        | NESTED       ：嵌套事务（设置保存点）
        | SUPPORTS     ：跟随环境（不处理）
        | NOT_SUPPORTED：非事务方式（仅挂起当前事务）
        | NEVER        ：排除事务（异常）
        | MANDATORY    ：强制要求事务（不处理）
        ===============================================================*/
        if (this.isExistingTransaction(defStatus) == true) {
            /*REQUIRES_NEW：独立事务*/
            if (behavior == REQUIRES_NEW) {
                this.suspend(defStatus);/*挂起当前事务*/
                this.doBegin(defStatus);/*开启新事务*/
            }
            /*NESTED：嵌套事务*/
            if (behavior == NESTED) {
                defStatus.markHeldSavepoint();/*设置保存点*/
            }
            /*NOT_SUPPORTED：非事务方式*/
            if (behavior == NOT_SUPPORTED) {
                this.suspend(defStatus);/*挂起事务*/
            }
            /*NEVER：排除事务*/
            if (behavior == NEVER) {
                this.cleanupAfterCompletion(defStatus);
                throw new SQLException("Existing transaction found for transaction marked with propagation 'never'");
            }
            return defStatus;
        }
        /*-------------------------------------------------------------
        |                      环境不经存在事务
        |
        | REQUIRED     ：加入已有事务（开启新事务）
        | REQUIRES_NEW ：独立事务（开启新事务）
        | NESTED       ：嵌套事务（开启新事务）
        | SUPPORTS     ：跟随环境（不处理）
        | NOT_SUPPORTED：非事务方式（不处理）
        | NEVER        ：排除事务（不处理）
        | MANDATORY    ：强制要求事务（异常）
        ===============================================================*/
        /*REQUIRED：加入已有事务*/
        if (behavior == REQUIRED ||
                /*REQUIRES_NEW：独立事务*/
                behavior == REQUIRES_NEW ||
                /*NESTED：嵌套事务*/
                behavior == NESTED) {
            this.doBegin(defStatus);/*开启新事务*/
        }
        /*MANDATORY：强制要求事务*/
        if (behavior == MANDATORY) {
            this.cleanupAfterCompletion(defStatus);
            throw new SQLException("No existing transaction found for transaction marked with propagation 'mandatory'");
        }
        return defStatus;
    }
    /**判断连接对象是否处于事务中，该方法会用于评估事务传播属性的处理方式。 */
    private boolean isExistingTransaction(final JdbcTransactionStatus defStatus) throws SQLException {
        return defStatus.getTranConn().hasTransaction();
    };
    /**初始化一个新的连接，并开启事务。*/
    protected void doBegin(final JdbcTransactionStatus defStatus) throws SQLException {
        TransactionObject tranConn = defStatus.getTranConn();
        tranConn.beginTransaction();
    }
    //
    //
    /**递交事务*/
    @Override
    public final void commit(final TransactionStatus status) throws SQLException {
        JdbcTransactionStatus defStatus = (JdbcTransactionStatus) status;
        /*已完毕，不需要处理*/
        if (defStatus.isCompleted()) {
            throw new SQLException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        /*回滚情况*/
        if (defStatus.isReadOnly() || defStatus.isRollbackOnly()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Transactional code has requested rollback");
            }
            this.rollBack(defStatus);
            return;
        }
        /*-------------------------------------------------------------
        | 1.无论何种传播形式，递交事务操作都会将 isCompleted 属性置为 true。
        | 2.如果事务状态中包含一个未处理的保存点。仅递交保存点，而非递交整个事务。
        | 3.事务 isNew 只有为 true 时才真正触发递交事务操作。
        ===============================================================*/
        try {
            this.prepareCommit(defStatus);
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint()) {
                defStatus.releaseHeldSavepoint();
            } else if (defStatus.isNewConnection()) {
                this.doCommit(defStatus);
            }
            //
        } catch (SQLException ex) {
            this.doRollback(defStatus);/*递交失败，回滚*/
            throw ex;
        } finally {
            this.cleanupAfterCompletion(defStatus);
        }
    }
    /**递交前的预处理*/
    private void prepareCommit(final JdbcTransactionStatus defStatus) throws SQLException {
        /*首先预处理的事务必须存在于管理器的事务栈内某一位置中，否则要处理的事务并非来源于该事务管理器。*/
        if (this.tStatusStack.contains(defStatus) == false)
            throw new SQLException("This transaction is not derived from this Manager.");
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
        while ((inStackStatus = this.tStatusStack.peek()) != defStatus) {
            this.commit(inStackStatus);
        }
    }
    /**处理当前底层数据库连接的事务递交操作。*/
    protected void doCommit(final JdbcTransactionStatus defStatus) throws SQLException {
        TransactionObject tranObject = defStatus.getTranConn();
        tranObject.commit();
    };
    //
    //
    /**回滚事务*/
    @Override
    public final void rollBack(final TransactionStatus status) throws SQLException {
        JdbcTransactionStatus defStatus = (JdbcTransactionStatus) status;
        /*已完毕，不需要处理*/
        if (defStatus.isCompleted()) {
            throw new SQLException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        /*-------------------------------------------------------------
        | 1.无论何种传播形式，递交事务操作都会将 isCompleted 属性置为 true。
        | 2.如果事务状态中包含一个未处理的保存点。仅回滚保存点，而非回滚整个事务。
        | 3.事务 isNew 只有为 true 时才真正触发回滚事务操作。
        ===============================================================*/
        try {
            this.prepareRollback(defStatus);
            /*如果包含保存点，在递交事务时只处理保存点*/
            if (defStatus.hasSavepoint()) {
                defStatus.rollbackToHeldSavepoint();
            } else if (defStatus.isNewConnection()) {
                this.doRollback(defStatus);
            }
            //
        } catch (SQLException ex) {
            this.doRollback(defStatus);
            throw ex;
        } finally {
            this.cleanupAfterCompletion(defStatus);
        }
    }
    /**回滚前的预处理*/
    private void prepareRollback(final JdbcTransactionStatus defStatus) throws SQLException {
        /*首先预处理的事务必须存在于管理器的事务栈内某一位置中，否则要处理的事务并非来源于该事务管理器。*/
        if (this.tStatusStack.contains(defStatus) == false) {
            throw new SQLException("This transaction is not derived from this Manager.");
        }
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
        while ((inStackStatus = this.tStatusStack.peek()) != defStatus) {
            this.rollBack(inStackStatus);
        }
    }
    /**处理当前底层数据库连接的事务回滚操作。*/
    protected void doRollback(final JdbcTransactionStatus defStatus) throws SQLException {
        TransactionObject tranObject = defStatus.getTranConn();
        tranObject.rollback();
    };
    //
    //
    /**挂起事务。*/
    protected final void suspend(final JdbcTransactionStatus defStatus) throws SQLException {
        /*事务已经被挂起*/
        if (defStatus.isSuspend() == true) {
            throw new SQLException("the Transaction has Suspend.");
        }
        //
        /*是否为栈顶事务*/
        this.prepareCheckStack(defStatus);
        /*挂起事务*/
        TransactionObject tranConn = defStatus.getTranConn();
        defStatus.setSuspendConn(tranConn);/*挂起*/
        SyncTransactionManager.clearSync(this.getDataSource());/*清除线程上的同步事务*/
        defStatus.setTranConn(this.doGetConnection(defStatus));/*重新申请数据库连接*/
    }
    /**恢复被挂起的事务。*/
    protected final void resume(final JdbcTransactionStatus defStatus) throws SQLException {
        if (defStatus.isCompleted() == false) {
            throw new SQLException("the Transaction has not completed.");
        }
        if (defStatus.isSuspend() == false) {
            throw new SQLException("the Transaction has not Suspend.");
        }
        //
        /*检查事务是否为栈顶事务*/
        this.prepareCheckStack(defStatus);
        /*恢复挂起的事务*/
        if (defStatus.isSuspend() == true) {
            SyncTransactionManager.clearSync(this.getDataSource());/*清除线程上的同步事务*/
            TransactionObject tranConn = defStatus.getSuspendConn();/*取得挂起的数据库连接*/
            SyncTransactionManager.setSync(tranConn);/*设置线程的数据库连接*/
            defStatus.setTranConn(tranConn);
            defStatus.setSuspendConn(null);
        }
    }
    //
    //
    /**检查正在处理的事务状态是否位于栈顶，否则抛出异常*/
    private void prepareCheckStack(final JdbcTransactionStatus defStatus) throws SQLException {
        if (!this.isTopTransaction(defStatus)) {
            throw new SQLException("the Transaction Status is not top in stack.");
        }
    }
    /**commit,rollback。之后的清理工作，同时也负责恢复事务和操作事务堆栈。*/
    private void cleanupAfterCompletion(final JdbcTransactionStatus defStatus) throws SQLException {
        /*清理的事务必须是位于栈顶*/
        this.prepareCheckStack(defStatus);
        /*标记完成*/
        defStatus.setCompleted();
        /*释放资源*/
        /*恢复当时的隔离级别*/
        Isolation transactionIsolation = defStatus.getTranConn().getOriIsolationLevel();
        if (transactionIsolation != null) {
            defStatus.getTranConn().getHolder().getConnection().setTransactionIsolation(transactionIsolation.ordinal());
        }
        defStatus.getTranConn().getHolder().released();//ref--
        defStatus.getTranConn().stopTransaction();
        /*恢复挂起的事务*/
        if (defStatus.isSuspend()) {
            this.resume(defStatus);
        }
        /*清理defStatus*/
        this.tStatusStack.removeFirst();
        //
        defStatus.setTranConn(null);
        defStatus.setSuspendConn(null);
    }
    //
    //
    //
    /**获取数据库连接（线程绑定的）*/
    protected TransactionObject doGetConnection(final JdbcTransactionStatus defStatus) throws SQLException {
        LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DSManager.getDataSourceHelper();
        ConnectionSequence connSeq = localHelper.getConnectionSequence(this.getDataSource());
        ConnectionHolder holder = connSeq.currentHolder();
        if (holder.isOpen() == false || holder.hasTransaction() == false) {
            defStatus.markNewConnection();/*新事物，新连接*/
        }
        holder.requested();
        //下面两行代码用于保存当前Connection的隔离级别，并且设置新的隔离级别。
        int isolationLevel = holder.getConnection().getTransactionIsolation();
        Isolation level = null;
        if (defStatus.getIsolationLevel() != Isolation.DEFAULT) {
            holder.getConnection().setTransactionIsolation(defStatus.getIsolationLevel().ordinal());
            level = Isolation.valueOf(isolationLevel);
        }
        //
        return new TransactionObject(holder, level, this.getDataSource());
    }
    /**获取对应的{@link TransactionTemplate}。*/
    public TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }
}
/** */
class SyncTransactionManager {
    public static void setSync(final TransactionObject tranConn) {
        LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DSManager.getDataSourceHelper();
        ConnectionSequence connSeq = localHelper.getConnectionSequence(tranConn.getDataSource());
        connSeq.pop();
    }
    public static void clearSync(final DataSource dataSource) {
        LocalDataSourceHelper localHelper = (LocalDataSourceHelper) DSManager.getDataSourceHelper();
        ConnectionSequence connSeq = localHelper.getConnectionSequence(dataSource);
        connSeq.push(null);
    }
}