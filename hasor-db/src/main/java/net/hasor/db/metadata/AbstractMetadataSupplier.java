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
package net.hasor.db.metadata;
import net.hasor.utils.ExceptionUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * MetadataSupplier 系列的公共类。
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractMetadataSupplier {
    protected final Supplier<Connection> connectSupplier;

    /** Connection will be proxy, Calling the close method in an AbstractMetadatasupplier subclass will be invalid. */
    public AbstractMetadataSupplier(Connection connection) {
        Connection conn = newProxyConnection(connection);
        this.connectSupplier = () -> conn;
    }

    /** Each time data is requested in the AbstractMetadatasupplier subclass a new Connection is created and then closed. */
    public AbstractMetadataSupplier(DataSource dataSource) {
        this.connectSupplier = () -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        };
    }

    protected static Connection newProxyConnection(Connection connection) {
        CloseIsNothingInvocationHandler handler = new CloseIsNothingInvocationHandler(connection);
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class, Closeable.class }, handler);
    }
}
