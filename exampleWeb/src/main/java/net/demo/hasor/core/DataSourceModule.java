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
package net.demo.hasor.core;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.demo.hasor.domain.DBConstant;
import net.hasor.core.*;
import net.hasor.db.DBModule;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
/**
 *
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceModule implements LifeModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        Environment env = apiBinder.getEnvironment();
        Settings settings = env.getSettings();
        String driverString = env.evalString(settings.getString("jdbcSettings.jdbcDriver", ""));
        String urlString = env.evalString(settings.getString("jdbcSettings.jdbcURL", ""));
        String userString = env.evalString(settings.getString("jdbcSettings.userName", ""));
        String pwdString = env.evalString(settings.getString("jdbcSettings.userPassword", ""));
        //
        DataSource dataSource = createDataSource(driverString, urlString, userString, pwdString);
        apiBinder.installModule(new DBModule(DBConstant.DB_HSQL, dataSource));
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        JdbcTemplate jdbcTemplate = appContext.findBindingBean(DBConstant.DB_HSQL, JdbcTemplate.class);
        jdbcTemplate.loadSQL("UTF-8", "/META-INF/ddl_sql_version_info.sql");
        jdbcTemplate.loadSQL("UTF-8", "/META-INF/init_sql_version_info.sql");
        logger.info("loadSQL");
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        // TODO Auto-generated method stub
    }
    //
    private DataSource createDataSource(String driverString, String urlString, String userString, String pwdString) throws PropertyVetoException {
        int poolMaxSize = 40;
        logger.info("C3p0 Pool Info maxSize is ‘{}’ driver is ‘{}’ jdbcUrl is‘{}’", poolMaxSize, driverString, urlString);
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
        return dataSource;
    }
}