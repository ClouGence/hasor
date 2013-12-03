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
package net.hasor.plugins.transaction._;
import javax.sql.DataSource;
import net.hasor.plugins.transaction.TransactionManager;
import net.hasor.plugins.transaction.core.ds.ConnectionHandle;
/**
 * 
 * @version : 2013-6-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransactionSynchronizationManager {
    //
    /**为某个数据源创建一个事务管理器。*/
    public static TransactionManager getTransactionManager(DataSource dataSource) {
        // TODO Auto-generated method stub
        return null;
    }
    //
    //
    public static ConnectionHandle getConnectionHandle() {
        return null;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //    public static ConnectionHolder getConnectionHolder(DataSource dataSource) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //
    //
    //    public static ConnectionHolder getConnectionHolder(DataSource dataSource) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //    /**当前操作的数据源中是否激活了事务。*/
    //    public static boolean hasTransactionActive() {
    //        Map<DataSource, ConnectionHandle> mapDS = ResourcesLocal.get();
    //        if (mapDS == null || mapDS.isEmpty())
    //            return false;
    //        for (ConnectionHandle ch : mapDS.values())
    //            if (ch.isTransactionActive())
    //                return true;
    //        return false;
    //    }; 
    //    //
    //    /**指定的数据源在当前线程中是否激活了事务。*/
    //    public static boolean hasTransactionActive(DataSource dataSource) {
    //        Map<DataSource, ConnectionHandle> mapDS = ResourcesLocal.get();
    //        ConnectionHandle ch = mapDS.get(dataSource);
    //        return (ch == null) ? false : ch.isTransactionActive();
    //    };
}