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
package net.hasor.jdbc.transaction.connection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
/**
 * 
 * @version : 2013-10-16
 * @author 赵永春(zyc@hasor.net)
 */
public class ConnectionHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionHandle>> ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionHandle>>();
    //
    /**当前操作的数据源中是否激活了事务。*/
    public static boolean hasTransactionActive() {
        Map<DataSource, ConnectionHandle> mapDS = ResourcesLocal.get();
        if (mapDS == null || mapDS.isEmpty())
            return false;
        for (ConnectionHandle ch : mapDS.values())
            if (ch.isTransactionActive())
                return true;
        return false;
    };
    //
    /**指定的数据源在当前线程中是否激活了事务。*/
    public static boolean hasTransactionActive(DataSource dataSource) {
        Map<DataSource, ConnectionHandle> mapDS = ResourcesLocal.get();
        ConnectionHandle ch = mapDS.get(dataSource);
        return (ch == null) ? false : ch.isTransactionActive();
    };
    //
    /**释放连接*/
    public static void releaseConnection(Connection target, DataSource dataSource) {
        Map<DataSource, ConnectionHandle> dsMap = ResourcesLocal.get();
        if (dsMap == null)
            return;
        ConnectionHandle connHandle = dsMap.get(dataSource);
        if (connHandle == null)
            return;
        //
        connHandle.released();
    }
    //
    /**申请连接*/
    public static Connection getConnection(DataSource dataSource) {
        ConnectionHandle connHandle = null;
        Map<DataSource, ConnectionHandle> dsMap = ResourcesLocal.get();
        if (dsMap == null) {
            dsMap = new HashMap<DataSource, ConnectionHandle>();
            ResourcesLocal.set(dsMap);
        }
        connHandle = dsMap.get(dataSource);
        if (connHandle == null)
            connHandle = new ConnectionHandle(dataSource);
        //
        Connection conn = connHandle.getConnection();
        connHandle.requested();
        return conn;
    }
}