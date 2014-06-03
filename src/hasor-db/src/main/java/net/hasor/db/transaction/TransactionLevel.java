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
package net.hasor.db.transaction;
import java.sql.Connection;
import org.more.classcode.FormatException;
/**
 * 事务隔离级别
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public enum TransactionLevel {
    /**默认事务隔离级别，具体使用的数据库事务隔离级别由底层决定。
     * @see java.sql.Connection*/
    ISOLATION_DEFAULT(-1),
    /**
     * 脏读
     * <p>允许脏读取，但不允许更新丢失。如果一个事务已经开始写数据，
     * 则另外一个事务则不允许同时进行写操作，但允许其他事务读此行数据。
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     */
    ISOLATION_READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    /**
     * 不可重复读
     * <p>允许不可重复读取，但不允许脏读取。读取数据的事务允许其他事务继续访问该行数据，
     * 但是未提交的写事务将会禁止其他事务访问该行。
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     */
    ISOLATION_READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    /**
     * 可重复读取 
     * <p>禁止不可重复读取和脏读，但是有时可能出现幻影数据。
     * 读取数据的事务将会禁止写事务（但允许读事务），写事务则禁止任何其他事务。
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     */
    ISOLATION_REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    /**
     * 同步事务
     * <p>提供严格的事务隔离。它要求事务序列化执行，事务只能一个接着一个地执行，但不能并发执行。
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     */
    ISOLATION_SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);
    //
    private int value;
    TransactionLevel(int value) {
        this.value = value;
    }
    protected int value() {
        return this.value;
    }
    public static TransactionLevel valueOf(int value) {
        switch (value) {
        case -1:
            return TransactionLevel.ISOLATION_DEFAULT;
        case Connection.TRANSACTION_READ_UNCOMMITTED:
            return TransactionLevel.ISOLATION_READ_UNCOMMITTED;
        case Connection.TRANSACTION_READ_COMMITTED:
            return TransactionLevel.ISOLATION_READ_COMMITTED;
        case Connection.TRANSACTION_REPEATABLE_READ:
            return TransactionLevel.ISOLATION_REPEATABLE_READ;
        case Connection.TRANSACTION_SERIALIZABLE:
            return TransactionLevel.ISOLATION_SERIALIZABLE;
        }
        throw new FormatException(String.format("Connection ISOLATION error level %s.", value));
    }
}