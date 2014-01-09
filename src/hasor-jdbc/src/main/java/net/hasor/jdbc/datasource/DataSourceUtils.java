/*
 * Copyright 2002-2006 the original author or authors.
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
package net.hasor.jdbc.datasource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.jdbc.datasource.local.DefaultDataSourceHelper;
import net.hasor.jdbc.exceptions.DataAccessException;
import org.more.util.ContextClassLoaderLocal;
/**
 * 
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceUtils {
    private static class ServiceLocal extends ContextClassLoaderLocal<DataSourceHelper> {
        protected DataSourceHelper initialValue() {
            return new DefaultDataSourceHelper();
        }
    }
    private static ServiceLocal utilServiceLocal = new ServiceLocal();
    //
    /**申请连接*/
    public static Connection getConnection(DataSource dataSource) throws DataAccessException {
        DataSourceHelper utilService = utilServiceLocal.get();
        try {
            Connection conn = utilService.getConnection(dataSource);
            if (conn == null)
                throw new DataAccessException("getConnection. return null.");
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("getConnection.", e);
        }
    };
    /**释放连接*/
    public static void releaseConnection(Connection con, DataSource dataSource) throws DataAccessException {
        DataSourceHelper utilService = utilServiceLocal.get();
        try {
            utilService.releaseConnection(con, dataSource);
        } catch (SQLException e) {
            throw new DataAccessException("releaseConnection.", e);
        }
    };
    /**获得某个数据源的当前连接*/
    public static Connection currentConnection(DataSource dataSource) throws DataAccessException {
        try {
            DataSourceHelper utilService = utilServiceLocal.get();
            return utilService.currentConnection(dataSource);
        } catch (SQLException e) {
            throw new DataAccessException("currentConnection.", e);
        }
    };
    /**获取DataSourceHelper*/
    public static DataSourceHelper getDataSourceHelper() {
        return utilServiceLocal.get();
    }
    /**更换默认DataSourceHelper*/
    protected static void changeDataSourceUtilService(DataSourceHelper utilService) {
        utilServiceLocal.set(utilService);
    }
}