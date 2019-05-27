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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春 (zyc@hasor.net)
 */
public class TranManager extends DataSourceManager {
    private final static ThreadLocal<ConcurrentMap<DataSource, JdbcTransactionManager>> managerMap;
    private final static ThreadLocal<ConcurrentMap<DataSource, ConnectionHolder>>       currentMap;

    static {
        managerMap = new ThreadLocal<ConcurrentMap<DataSource, JdbcTransactionManager>>() {
            protected ConcurrentMap<DataSource, JdbcTransactionManager> initialValue() {
                return new ConcurrentHashMap<DataSource, JdbcTransactionManager>();
            }
        };
        currentMap = new ThreadLocal<ConcurrentMap<DataSource, ConnectionHolder>>() {
            protected ConcurrentMap<DataSource, ConnectionHolder> initialValue() {
                return new ConcurrentHashMap<DataSource, ConnectionHolder>();
            }
        };
    }

    //
    public static ConnectionHolder currentConnectionHolder(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        ConcurrentMap<DataSource, ConnectionHolder> localMap = currentMap.get();
        ConnectionHolder holder = localMap.get(dataSource);
        if (holder == null) {
            holder = localMap.putIfAbsent(dataSource, genConnectionHolder(dataSource));
            holder = localMap.get(dataSource);
        }
        return holder;
    }
    public static Connection currentConnection(DataSource dataSource) {
        ConnectionHolder holder = currentConnectionHolder(dataSource);
        return newProxyConnection(holder);
    }
    /**改变当前{@link ConnectionHolder}*/
    protected static void currentConnection(DataSource dataSource, ConnectionHolder holder) {
        ConcurrentMap<DataSource, ConnectionHolder> localMap = currentMap.get();
        if (holder == null) {
            if (localMap.containsKey(dataSource)) {
                localMap.remove(dataSource);
            }
        } else {
            localMap.put(dataSource, holder);
        }
    }
    //
    /**获取事务管理器*/
    private static synchronized JdbcTransactionManager getTransactionManager(final DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        ConcurrentMap<DataSource, JdbcTransactionManager> localMap = managerMap.get();
        JdbcTransactionManager manager = localMap.get(dataSource);
        if (manager == null) {
            manager = localMap.putIfAbsent(dataSource, new JdbcTransactionManager(dataSource) {
            });
            manager = localMap.get(dataSource);
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
        if (manager == null) {
            return null;
        }
        return manager.getTransactionTemplate();
    }
}