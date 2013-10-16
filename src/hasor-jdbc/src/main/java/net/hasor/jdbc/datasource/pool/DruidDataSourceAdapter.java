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
package net.hasor.jdbc.datasource.pool;
import javax.sql.DataSource;
import net.hasor.Hasor;
import net.hasor.core.XmlNode;
import net.hasor.jdbc.datasource.DataSourceResources;
import com.alibaba.druid.pool.DruidDataSource;
/**
 * 
 * @version : 2013-10-8
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DruidDataSourceAdapter implements DataSourceResources {
    //
    public DataSource getDataSource(XmlNode config) throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        //
        String driverString = config.getXmlNode("driver").getText();//<driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</driver>
        String urlString = config.getXmlNode("url").getText();//<url>jdbc:sqlserver://10.200.15.100;DatabaseName=NOE_ESTUDY</url>
        String userString = config.getXmlNode("user").getText();//<user>sa</user>
        String pwdString = config.getXmlNode("password").getText();//<password>abc123!@#</password>
        int poolMaxSize = 200;
        //
        Hasor.info("Druid Pool Info maxSize is °Æ%s°Ø driver is °Æ%s°Ø jdbcUrl is°Æ%s°Ø", poolMaxSize, driverString, urlString);
        //
        dataSource.setDriverClassName(driverString);
        dataSource.setUrl(urlString);
        dataSource.setUsername(userString);
        dataSource.setPassword(pwdString);
        dataSource.setFilters("stat");
        dataSource.setMaxActive(poolMaxSize);
        dataSource.setInitialSize(1);
        dataSource.setMaxWait(60000);
        dataSource.setMinIdle(1);
        dataSource.setTimeBetweenEvictionRunsMillis(3000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        //
        dataSource.init();
        return dataSource;
    }
}