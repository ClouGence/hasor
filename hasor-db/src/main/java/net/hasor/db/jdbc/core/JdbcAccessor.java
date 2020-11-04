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
package net.hasor.db.jdbc.core;
import net.hasor.db.transaction.TranManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.function.Function;

/**
 *
 * @version : 2013-10-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcAccessor {
    private DataSource                       dataSource;
    private Connection                       connection;
    private Function<DataSource, Connection> dsApply = TranManager::currentConnection;

    /**Return the DataSource used by this template.*/
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**Set the JDBC DataSource to obtain connections from.*/
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**Return the Connection used by this template.*/
    public Connection getConnection() {
        return this.connection;
    }

    /**Set the JDBC Connection to obtain connection from.*/
    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    public Function<DataSource, Connection> getDsApply() {
        return this.dsApply;
    }

    public void setDsApply(Function<DataSource, Connection> dsApply) {
        this.dsApply = dsApply;
    }

    protected Connection applyConnection(DataSource dataSource) {
        return this.dsApply.apply(dataSource);
    }
}