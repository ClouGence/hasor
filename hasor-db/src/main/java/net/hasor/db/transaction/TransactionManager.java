/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.db.transaction;
import java.sql.SQLException;

/**
 * 数据源的事务管理器。
 * @version : 2013-10-30
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TransactionManager {
    /**开启事务，使用默认事务隔离级别。
     * @see Propagation
     * @see TransactionManager#getTransaction(Propagation, Isolation)*/
    public default TransactionStatus getTransaction(Propagation behavior) throws SQLException {
        return this.getTransaction(behavior, Isolation.DEFAULT);
    }

    /**开启事务
     * @see Propagation
     * @see java.sql.Connection#setTransactionIsolation(int)*/
    public TransactionStatus getTransaction(Propagation behavior, Isolation level) throws SQLException;

    /**递交事务
     * <p>如果递交的事务并不处于事务堆栈顶端，会同时递交该事务的后面其它事务。*/
    public void commit(TransactionStatus status) throws SQLException;

    /**回滚事务*/
    public void rollBack(TransactionStatus status) throws SQLException;

    /**是否存在未处理完的事务（包括被挂起的事务）。*/
    public boolean hasTransaction();

    /**测试事务状态是否位于栈顶。*/
    public boolean isTopTransaction(TransactionStatus status);
}