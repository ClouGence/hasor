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
package net.hasor.db.transaction.support;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.db.datasource.SavepointManager;
import net.hasor.db.datasource.local.ConnectionHolder;
import net.hasor.db.transaction.Isolation;
/**
 * 
 * @version : 2014-1-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransactionObject {
    private ConnectionHolder holder     = null;
    private DataSource       dataSource = null;
    private Isolation oriIsolationLevel; //创建事务对象时的隔离级别，当事物结束之后用以恢复隔离级别
    public TransactionObject(ConnectionHolder holder, Isolation oriIsolationLevel, DataSource dataSource) {
        this.holder = holder;
        this.dataSource = dataSource;
        this.oriIsolationLevel = oriIsolationLevel;
    }
    public Isolation getOriIsolationLevel() {
        return oriIsolationLevel;
    }
    public ConnectionHolder getHolder() {
        return this.holder;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public SavepointManager getSavepointManager() {
        return this.getHolder();
    };
    public void rollback() throws SQLException {
        this.holder.getConnection().rollback();
    }
    public void commit() throws SQLException {
        this.holder.getConnection().commit();
    }
    public boolean hasTransaction() throws SQLException {
        return this.holder.hasTransaction();
    };
    public void begin() throws SQLException {
        Connection conn = this.holder.getConnection();
        boolean autoMark = conn.getAutoCommit();
        if (autoMark == true)
            conn.setAutoCommit(false);//将连接autoCommit设置为false，意义为手动递交事务。
    }
}