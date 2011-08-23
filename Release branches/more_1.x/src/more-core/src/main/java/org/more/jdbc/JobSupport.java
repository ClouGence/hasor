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
package org.more.jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
/**
 * 用于在业务层中使用Dao的支撑环境，该环境中可以获得JdbcDaoSupport对象以操作数据库。
 * 同时如果TransactionManager拦截器负责拦截DaoSupport那么还可以获得事务控制的功能。
 * finalize方法会调用releaseJdbcDaoSupport方法以释放资源。
 * @version 2009-12-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class JobSupport {
    //========================================================================================Field
    private JdbcDaoSupport jdbcDaoSupport; //
    private DataSource     dataSource;    //
    //==========================================================================================Job
    /**获取DaoSupport所使用的Jdbc数据源。*/
    public DataSource getDataSource() {
        return dataSource;
    }
    /**设置DaoSupport所使用的Jdbc数据源。*/
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**获取调用Jdbc的dao操作环境如果不存在JdbcDaoSupport对象DaoSupport将试图创建一个，当配置了事务控制拦截器之后JdbcDaoSupport对象由拦截器负责创建。*/
    protected JdbcDaoSupport getJdbcDaoSupport() throws SQLException {
        if (jdbcDaoSupport == null)
            jdbcDaoSupport = new JdbcDaoSupport(this.dataSource.getConnection());
        return jdbcDaoSupport;
    }
    /** 获取数据库连接 */
    protected Connection getConnection() throws SQLException {
        return this.getJdbcDaoSupport().connection;
    }
    /**设置jdbc工具*/
    void setJdbcDaoSupport(JdbcDaoSupport jdbcDaoSupport) {
        this.jdbcDaoSupport = jdbcDaoSupport;
    }
    /**释放Dao支撑环境该方法会导致事务的递交操作，当再次调用getJdbcDaoSupport方法时Dao支撑环境将再次被重新创建，该方法主要用于手动释放连接资源。*/
    public void releaseJdbcDaoSupport() throws SQLException {
        this.jdbcDaoSupport.connection.commit();
        this.jdbcDaoSupport.connection.close();
        this.jdbcDaoSupport = null;
    }
    /**finalize方法会调用releaseJdbcDaoSupport方法以释放资源。*/
    protected void finalize() throws Throwable {
        try {
            this.releaseJdbcDaoSupport();
        } catch (Exception e) {}
    }
}