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
import javax.sql.DataSource;
import net.hasor.jdbc.exceptions.DataAccessException;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class ConnectionHolder {
    private int        referenceCount;
    private DataSource dataSource;
    private Connection connection;
    public ConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**增加引用计数,一个因为持有人已被请求。*/
    public synchronized void requested() {
        this.referenceCount++;
    }
    /**减少引用计数,一个因为持有人已被释放。*/
    public synchronized void released() {
        this.referenceCount--;
        if (!isOpen() && this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                throw new DataAccessException("cant not close connection.", e);
            } finally {
                this.connection = null;
            }
        }
    }
    public boolean isOpen() {
        if (referenceCount == 0)
            return false;
        return true;
    }
    /**获取连接*/
    public synchronized Connection getConnection() throws SQLException {
        if (this.isOpen() == false)
            return null;
        if (this.connection == null) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    };
}
