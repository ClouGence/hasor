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
package net.hasor.jdbc.datasource.factory;
import javax.sql.DataSource;
import net.hasor.Hasor;
import net.hasor.core.Environment;
import net.hasor.core.XmlNode;
import net.hasor.jdbc.datasource.DataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * 
 * @version : 2013-9-16
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class C3p0Factory implements DataSourceFactory {
    public DataSource createDataSource(Environment env, XmlNode configElement) throws Throwable {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //
        String driverString = configElement.getXmlNode("driver").getText();//<driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</driver>
        String urlString = configElement.getXmlNode("url").getText();//<url>jdbc:sqlserver://10.200.15.100;DatabaseName=NOE_ESTUDY</url>
        String userString = configElement.getXmlNode("user").getText();//<user>sa</user>
        String pwdString = configElement.getXmlNode("password").getText();//<password>abc123!@#</password>
        int poolMaxSize = 200;
        //
        Hasor.logInfo("C3p0 Pool Info maxSize is °Æ%s°Ø driver is °Æ%s°Ø jdbcUrl is°Æ%s°Ø", poolMaxSize, driverString, urlString);
        //
        dataSource.setDriverClass(driverString);
        dataSource.setJdbcUrl(urlString);
        dataSource.setUser(userString);
        dataSource.setPassword(pwdString);
        dataSource.setMaxPoolSize(poolMaxSize);
        dataSource.setInitialPoolSize(1);
        //
        dataSource.setAutomaticTestTable("DB_TEST_ATest001");
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        return dataSource;
    }
}