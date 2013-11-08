/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.jdbc.transaction._;
import java.sql.Connection;
import javax.sql.DataSource;
import net.hasor.Hasor;
/**
 * 
 * @version : 2013-10-30
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DataSourceUtils {
    /**…Í«Î¡¨Ω”*/
    public static Connection getConnection(DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource, "No DataSource specified");
        ConnectionHolder conHolder = TransactionSynchronizationManager.getConnectionHolder(dataSource);
        if (conHolder != null && (conHolder.hasConnection() || conHolder.hasTransaction())) {
            conHolder.requested();
            return conHolder.getConnection();
        }
        // Else we either got no holder or an empty thread-bound holder here.
        Hasor.debug("Fetching JDBC Connection from DataSource");
        Connection con = dataSource.getConnection();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Hasor.debug("Registering transaction synchronization for JDBC Connection");
            // Use same Connection for further JDBC actions within the transaction.
            // Thread-bound object will get removed by synchronization at transaction completion.
            ConnectionHolder holderToUse = conHolder;
            if (holderToUse == null) {
                holderToUse = new ConnectionHolder(con);
            } else {
                holderToUse.setConnection(con);
            }
            holderToUse.requested();
            TransactionSynchronizationManager.registerSynchronization(new ConnectionSynchronization(holderToUse, dataSource));
            holderToUse.setSynchronizedWithTransaction(true);
            if (holderToUse != conHolder) {
                TransactionSynchronizationManager.bindResource(dataSource, holderToUse);
            }
        }
        return con;
    }
    /** Õ∑≈¡¨Ω”*/
    public static void releaseConnection(Connection con, DataSource dataSource) {
        // TODO Auto-generated method stub
    }
}