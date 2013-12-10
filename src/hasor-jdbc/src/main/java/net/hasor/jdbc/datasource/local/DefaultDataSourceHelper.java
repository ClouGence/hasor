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
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DefaultDataSourceHelper implements DataSourceHelper {
    private static final ThreadLocal<Map<DataSource, ConnectionHolder>> ResourcesLocal = new ThreadLocal<Map<DataSource, ConnectionHolder>>();
    static {
        ResourcesLocal.set(new HashMap<DataSource, ConnectionHolder>());
    }
    /**…Í«Î¡¨Ω”*/
    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null) {
            holder = this.createConnectionHolder(dataSource);
            ResourcesLocal.get().put(dataSource, holder);
        }
        holder.requested();
        return holder.getConnection();
    };
    /** Õ∑≈¡¨Ω”*/
    public void releaseConnection(Connection con, DataSource dataSource) throws SQLException {
        ConnectionHolder holder = ResourcesLocal.get().get(dataSource);
        if (holder == null)
            return;
        holder.released();
        if (!holder.isOpen())
            ResourcesLocal.get().remove(dataSource);
    };
    protected ConnectionHolder createConnectionHolder(DataSource dataSource) {
        return new ConnectionHolder(dataSource);
    }
}