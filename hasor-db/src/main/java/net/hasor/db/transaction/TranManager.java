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
import net.hasor.db.datasource.ConnectionHolder;
import net.hasor.db.datasource.DataSourceManager;
import net.hasor.db.transaction.support.JdbcTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春 (zyc@hasor.net)
 */
public class TranManager extends DataSourceManager {
    private final static ThreadLocal<Map<DataSource, JdbcTransactionManager>> managerMap = ThreadLocal.withInitial(ConcurrentHashMap::new);
    private final static ThreadLocal<Map<DataSource, ConnectionHolder>>       currentMap = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static ConnectionHolder currentConnectionHolder(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        Map<DataSource, ConnectionHolder> localMap = currentMap.get();
        ConnectionHolder holder = localMap.get(dataSource);
        if (holder == null) {
            holder = localMap.putIfAbsent(dataSource, newConnectionHolder(dataSource));
            holder = localMap.get(dataSource);
        }
        return holder;
    }

    /** 该方法会拿到一个当前 Connection 的引用，在使用完毕之后必须要 close 它。否则会产生引用泄漏。 */
    public static Connection currentConnection(DataSource dataSource) {
        ConnectionHolder holder = currentConnectionHolder(dataSource);
        return newProxyConnection(holder);
    }

    /**改变当前{@link ConnectionHolder}*/
    protected static void currentConnection(DataSource dataSource, ConnectionHolder holder) {
        Map<DataSource, ConnectionHolder> localMap = currentMap.get();
        if (holder == null) {
            localMap.remove(dataSource);
        } else {
            localMap.put(dataSource, holder);
        }
    }

    /**获取事务管理器*/
    private static synchronized JdbcTransactionManager getTransactionManager(final DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        Map<DataSource, JdbcTransactionManager> localMap = managerMap.get();
        JdbcTransactionManager manager = localMap.get(dataSource);
        if (manager == null) {
            manager = new JdbcTransactionManager(dataSource);
            localMap.put(dataSource, manager);
        }
        return manager;
    }

    /**获取{@link TransactionManager}*/
    public static synchronized TransactionManager getManager(DataSource dataSource) {
        return getTransactionManager(dataSource);
    }

    /**获取{@link TransactionTemplate}*/
    public static synchronized TransactionTemplate getTemplate(DataSource dataSource) {
        JdbcTransactionManager manager = getTransactionManager(dataSource);
        return manager.getTransactionTemplate();
    }
}
