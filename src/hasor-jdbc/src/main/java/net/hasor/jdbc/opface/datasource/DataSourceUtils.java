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
package net.hasor.jdbc.opface.datasource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.jdbc.DataAccessException;
import org.more.util.ContextClassLoaderLocal;
/**
 * 
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceUtils {
    private static DefaultDataSourceUtilService                          defaultUtilService = new DefaultDataSourceUtilService();
    private static ContextClassLoaderLocal<DefaultDataSourceUtilService> utilServiceLocal   = new ContextClassLoaderLocal<DefaultDataSourceUtilService>();
    //
    /**申请连接*/
    public static Connection getConnection(DataSource dataSource) throws DataAccessException {
        DefaultDataSourceUtilService utilService = utilServiceLocal.get();
        utilService = (utilService == null) ? defaultUtilService : utilService;
        try {
            return utilService.getConnection(dataSource);
        } catch (SQLException e) {
            throw new DataAccessException("getConnection.", e);
        }
    };
    /**释放连接*/
    public static void releaseConnection(Connection con, DataSource dataSource) throws DataAccessException {
        DefaultDataSourceUtilService utilService = utilServiceLocal.get();
        utilService = (utilService == null) ? defaultUtilService : utilService;
        try {
            utilService.releaseConnection(con, dataSource);
        } catch (SQLException e) {
            throw new DataAccessException("releaseConnection.", e);
        }
    };
    protected static void changeDataSourceUtilService(DefaultDataSourceUtilService utilService) {
        utilServiceLocal.set(utilService);
    }
}