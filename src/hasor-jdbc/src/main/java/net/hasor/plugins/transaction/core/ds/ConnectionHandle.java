/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.plugins.transaction.core.ds;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.DataSource;
import net.hasor.plugins.transaction.core.SavepointManager;
/**
 * 
 * @version : 2013-10-17
 * @author 赵永春(zyc@hasor.net)
 */
public class ConnectionHandle implements SavepointManager {
    private Connection connection;
    private DataSource useDataSource;
    //
    private boolean    transactionActive;
    private int        referenceCount;
    // 
    public ConnectionHandle(DataSource dataSource) {
        this.useDataSource = useDataSource;
    }
    //
    //
    //
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
    /**当引用计数大于 0 时，返回 true。*/
    public boolean isOpen() {
        return (this.referenceCount > 0);
    }
    //
    //---------------------------------------------------------------------------Savepoint
    public static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
    private int                savepointCounter      = 0;
    private Boolean            savepointsSupported;
    /**返回 JDBC 驱动是否支持保存点。*/
    public boolean supportsSavepoints() throws SQLException {
        if (this.savepointsSupported == null)
            this.savepointsSupported = getConnection().getMetaData().supportsSavepoints();
        return this.savepointsSupported;
    }
    /**使用一个全新的名称创建一个保存点。*/
    public Savepoint createSavepoint() throws SQLException {
        this.savepointCounter++;
        return getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + this.savepointCounter);
    }
    public void rollbackToSavepoint(Savepoint savepoint) throws SQLException {
        getConnection().rollback(savepoint);
    }
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getConnection().releaseSavepoint(savepoint);
    };
    //
    //---------------------------------------------------------------------------Savepoint
    public Connection getConnection() {
        if (this.connection == null)
            this.connection = this.useDataSource.getConnection();
        return this.connection;
    };
    /**当前连接的事务是否被激活。*/
    public boolean isTransactionActive() {
        return transactionActive;
    }
}