/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class ConnectionHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionHandle>> ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionHandle>>();
    //
    //
    //
    //
    //
    //
    //
    /** Õ∑≈¡¨Ω”*/
    public static void releaseConnection(Connection target, DataSource dataSource) {
        ConnectionHandle connHandle = getConnectionHandle(dataSource);
        if (connHandle == null && hasTransactionActive(dataSource) == false)
            return target.close();
        //
        connHandle.released();
    }
    /**…Í«Î¡¨Ω”*/
    public static Connection getConnection(DataSource dataSource) {
        ConnectionHandle connHandle = getConnectionHandle(dataSource);
        if (connHandle == null && hasTransactionActive(dataSource) == false)
            return dataSource.getConnection();
        //
        if (connHandle == null) {
            connHandle = new ConnectionHandle(dataSource);
            ResourcesLocal.get().put(dataSource, connHandle);
        }
        connHandle.requested();
        return connHandle.getConnection();
    }
    private static ConnectionHandle getConnectionHandle(DataSource dataSource) {
        Map<DataSource, ConnectionHandle> dsMap = ResourcesLocal.get();
        if (dsMap == null) {
            dsMap = new HashMap<DataSource, ConnectionHandle>();
            ResourcesLocal.set(dsMap);
        }
        return dsMap.get(dataSource);
    }
}