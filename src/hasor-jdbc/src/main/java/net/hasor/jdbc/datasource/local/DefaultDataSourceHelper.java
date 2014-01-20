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
package net.hasor.jdbc.datasource.local;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.jdbc.datasource.SavepointDataSourceHelper;
import net.hasor.jdbc.datasource.SavepointManager;
/**
 * 
 * @version : 2013-12-2
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultDataSourceHelper implements SavepointDataSourceHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionHolder>> ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionHolder>>();
    private static void initLocal() {
        if (ResourcesLocal.get() == null)
            ResourcesLocal.set(new HashMap<DataSource, ConnectionHolder>());
    }
    /**申请连接，如果当前连接存在则返回当前连接*/
    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionHolder holder = getConnectionHolder(dataSource);
        holder.requested();
        return holder.getConnection();
    };
    /**释放连接*/
    public void releaseConnection(Connection con, DataSource dataSource) throws SQLException {
        initLocal();
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null)
            return;
        holder.released();
        if (!holder.isOpen())
            ResourcesLocal.get().remove(dataSource);
    };
    public Connection currentConnection(DataSource dataSource) throws SQLException {
        ConnectionHolder holder = getConnectionHolder(dataSource);
        return holder.getConnection();
    }
    public SavepointManager getSavepointManager(DataSource dataSource) throws SQLException {
        return getConnectionHolder(dataSource);
    }
    /**获取ConnectionHolder*/
    protected ConnectionHolder getConnectionHolder(DataSource dataSource) {
        initLocal();
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null) {
            holder = this.createConnectionHolder(dataSource);
            ResourcesLocal.get().put(dataSource, holder);
        }
        return holder;
    }
    /**创建ConnectionHolder对象*/
    protected ConnectionHolder createConnectionHolder(DataSource dataSource) {
        return new ConnectionHolder(dataSource);
    }
}