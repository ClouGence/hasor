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
package net.hasor.jdbc.opface.core;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
/**
 * 
 * @version : 2013-10-16
 * @author 赵永春(zyc@hasor.net)
 */
public class JdbcAccessor {
    private DataSource dataSource;
    /**Set the JDBC DataSource to obtain connections from.*/
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**Return the DataSource used by this template.*/
    public DataSource getDataSource() {
        return this.dataSource;
    }
    /**获取一个数据库连接，JDBC 框架会从 DataSource 接口尝试获取一个新的连接资源给开发者。
     * 开发者需要自己维护连接的事务，并且要保证该资源可以被正常释放。*/
    protected Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}