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
package org.platform.jdbc.datasource;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.platform.Assert;
/**
 * 
 * @version : 2013-5-8
 * @author 赵永春 (zyc@byshell.org) 
 */
public class DataSourceUtils {
    /**获取数据库连接对象。*/
    public static Connection getConnection(DataSource dataSource) {
        return null;
        // TODO Auto-generated method stub 
    }
    /**释放数据库连接对象。*/
    public static void releaseConnection(Connection con, DataSource dataSource) {
        // TODO Auto-generated method stub
    }
    public static void applyTimeout(Statement statement, DataSource dataSource, int queryTimeout) {
        Assert.notNull(statement, "No Statement specified");
        Assert.notNull(dataSource, "No DataSource specified");
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        if (holder != null && holder.hasTimeout()) {
            // Remaining transaction timeout overrides specified value.
            stmt.setQueryTimeout(holder.getTimeToLiveInSeconds());
        } else if (timeout > 0) {
            // No current transaction timeout -> apply specified value.
            stmt.setQueryTimeout(timeout);
        }
    }
}