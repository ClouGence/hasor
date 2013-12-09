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
package net.hasor.jdbc.datasource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
/**
 * 
 * @version : 2013-12-2
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultDataSourceHelper implements DataSourceHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionHolder>> ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionHolder>>();
    static {
        ResourcesLocal.set(new HashMap<DataSource, DefaultDataSourceHelper.ConnectionHolder>());
    }
    /**申请连接*/
    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null) {
            holder = new ConnectionHolder(dataSource);
            ResourcesLocal.get().put(dataSource, holder);
        }
        holder.requested();
        return holder.getConnection();
    };
    /**释放连接*/
    public void releaseConnection(Connection con, DataSource dataSource) throws SQLException {
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null)
            return;
        holder.released();
        if (!holder.isOpen())
            ResourcesLocal.get().remove(dataSource);
    };
    private static class ConnectionHolder {
        private int referenceCount;
        public ConnectionHolder(DataSource dataSource) {
            // TODO Auto-generated constructor stub
        }
        /**增加引用计数,一个因为持有人已被请求。*/
        public void requested() {
            this.referenceCount++;
        }
        /**减少引用计数,一个因为持有人已被释放。*/
        public void released() {
            this.referenceCount--;
            if (!isOpen() && this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        }
        public boolean isOpen() {
            if (referenceCount == 0 && this.connection == null)
                // TODO Auto-generated method stub
                return false;
        }
        /**获取连接*/
        public Connection getConnection() {};
    }
}