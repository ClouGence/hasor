/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.web.startup.module;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Settings;
import net.hasor.db.provider.SimpleDBModule;
import com.mchange.v2.c3p0.ComboPooledDataSource;
/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //1.获取数据库连接配置信息
        Settings settings = apiBinder.getSettings();
        String driverString = settings.getString("demo-jdbc-mysql.driver");
        String urlString = settings.getString("demo-jdbc-mysql.url");
        String userString = settings.getString("demo-jdbc-mysql.user");
        String pwdString = settings.getString("demo-jdbc-mysql.password");
        //2.创建数据库连接池
        int poolMaxSize = 200;
        Hasor.logInfo("C3p0 Pool Info maxSize is ‘%s’ driver is ‘%s’ jdbcUrl is‘%s’", poolMaxSize, driverString, urlString);
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverString);
        dataSource.setJdbcUrl(urlString);
        dataSource.setUser(userString);
        dataSource.setPassword(pwdString);
        dataSource.setMaxPoolSize(poolMaxSize);
        dataSource.setInitialPoolSize(1);
        //dataSource.setAutomaticTestTable("DB_TEST_ATest001");
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        //3.启用Hasor DB 插件
        apiBinder.installModule(new SimpleDBModule("default", dataSource));
    }
}