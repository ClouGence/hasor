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
package net.hasor.jdbc.transaction;
import net.hasor.jdbc.TransactionDataAccessException;
/**
 * 数据源的事务管理器。
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public interface TransactionManager {
    /**开启事务，使用默认事务隔离级别*/
    public TransactionStatus getTransaction(TransactionBehavior behavior) throws TransactionDataAccessException;
    /**开启事务，使用指定的事务隔离级别*/
    public TransactionStatus getTransaction(TransactionBehavior behavior, TransactionLevel level) throws TransactionDataAccessException;
    /**递交事务
     * <p>如果递交的事务并不处于事务堆栈顶端，会同时递交该事务的后面其它事务。*/
    public void commit(TransactionStatus status) throws TransactionDataAccessException;
    /**回滚事务*/
    public void rollBack(TransactionStatus status) throws TransactionDataAccessException;
    //
    //
    //
    /**是否存在未处理完的事务（包括被挂起的事务）。*/
    public boolean hasTransaction();
    /**事务管理器中事务堆栈大小。
     * <p>当使用 <code>getTransaction</code> 方法创建了事务之后，事务就会被压入堆栈。
     * 每次创建的新事务都会被压入堆栈，事务的处理会按照事务堆栈先进后出顺序进行。*/
    public int getTransactionStackSize();
    /**获取事务管理器当前的事务堆栈。*/
    public TransactionStatus[] getTransactionStatusStack();
}