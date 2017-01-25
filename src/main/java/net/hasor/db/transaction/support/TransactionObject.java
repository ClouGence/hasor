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
import net.hasor.db.datasource.ConnectionHolder;
import net.hasor.db.datasource.SavepointManager;
import net.hasor.db.transaction.Isolation;

import javax.sql.DataSource;
import java.sql.SQLException;
/**
 *
 * @version : 2014-1-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransactionObject {
    private ConnectionHolder holder     = null;
    private DataSource       dataSource = null;
    private Isolation oriIsolationLevel; //创建事务对象时的隔离级别，当事物结束之后用以恢复隔离级别
    public TransactionObject(final ConnectionHolder holder, final Isolation oriIsolationLevel, final DataSource dataSource) throws SQLException {
        this.holder = holder;
        this.dataSource = dataSource;
        this.oriIsolationLevel = oriIsolationLevel;
    }
    public Isolation getOriIsolationLevel() {
        return this.oriIsolationLevel;
    }
    public ConnectionHolder getHolder() {
        return this.holder;
    }
    public DataSource getDataSource() {
        return this.dataSource;
    }
    public SavepointManager getSavepointManager() {
        return this.getHolder();
    }
    public void rollback() throws SQLException {
        if (this.holder.hasTransaction()) {
            this.holder.getConnection().rollback();//在AutoCommit情况下不执行事务操作（MYSQL强制在auto下执行该方法会引发异常）。
        }
    }
    public void commit() throws SQLException {
        if (this.holder.hasTransaction()) {
            this.holder.getConnection().commit();//在AutoCommit情况下不执行事务操作（MYSQL强制在auto下执行该方法会引发异常）。
        }
    }
    public boolean hasTransaction() throws SQLException {
        return this.holder.hasTransaction();
    }
    //
    private boolean recoverMark = false;
    public void beginTransaction() throws SQLException {
        if (!this.holder.hasTransaction()) {
            this.recoverMark = true;
        }
        this.holder.setTransaction();
    }
    public void stopTransaction() throws SQLException {
        if (!this.recoverMark) {
            return;
        }
        this.recoverMark = false;
        this.holder.cancelTransaction();
    }
}