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
import net.hasor.jdbc.datasource.DataSourceHelper;
/**
 * 
 * @version : 2013-12-2
 * @author 赵永春(zyc@hasor.net)
 */
public class LocalDataSourceHelper implements DataSourceHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionSequence>> ResourcesLocal;
    static {
        ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionSequence>>() {
            protected Map<DataSource, ConnectionSequence> initialValue() {
                return new HashMap<DataSource, ConnectionSequence>();
            }
        };
    }
    /**申请连接，如果当前连接存在则返回当前连接*/
    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionSequence conSeq = getConnectionSequence(dataSource);/*获取序列*/
        ConnectionHolder connHolder = conSeq.currentHolder();/*获取当前Holder*/
        connHolder.requested();/*引用计数+1*/
        return connHolder.getConnection();/*返回连接*/
    };
    /**释放连接*/
    public void releaseConnection(Connection con, DataSource dataSource) throws SQLException {
        ConnectionSequence conSeq = getConnectionSequence(dataSource);//获取序列
        ConnectionHolder holder = conSeq.currentHolder();/*获取当前Holder*/
        if (holder != null)
            holder.released();/*引用计数-1*/
    };
    public Connection currentConnection(DataSource dataSource) throws SQLException {
        ConnectionSequence conSeq = getConnectionSequence(dataSource);//获取序列
        ConnectionHolder holder = conSeq.currentHolder();/*获取当前Holder*/
        return holder.getConnection();/*返回连接*/
    }
    /**获取ConnectionSequence*/
    public ConnectionSequence getConnectionSequence(DataSource dataSource) {
        ConnectionSequence conSeq = ResourcesLocal.get().get(dataSource);
        /*构建序列*/
        if (conSeq == null) {
            conSeq = createConnectionSequence();
            ResourcesLocal.get().put(dataSource, conSeq);
        }
        /*新建ConnectionHolder*/
        if (conSeq.currentHolder() == null) {
            conSeq.currentHolder(this.createConnectionHolder(dataSource));
        }
        return conSeq;
    }
    /**创建ConnectionSequence对象*/
    protected ConnectionSequence createConnectionSequence() {
        return new ConnectionSequence();
    }
    /**创建ConnectionHolder对象*/
    protected ConnectionHolder createConnectionHolder(DataSource dataSource) {
        return new ConnectionHolder(dataSource);
    }
}