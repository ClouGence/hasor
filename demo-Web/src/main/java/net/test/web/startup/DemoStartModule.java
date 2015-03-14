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
package net.test.web.startup;
import java.beans.PropertyVetoException;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.core.StartModule;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.provider.SimpleDBModule;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.LoadHellper;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.hasor.search.client.rsf.SearchServer;
import net.hasor.search.client.rsf.SearchServerFactory;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.test.web.biz.user.action.UserAction;
import net.test.web.biz.user.search.SearchServices;
import org.more.logger.LoggerHelper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * 
 * @version : 2014年7月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class DemoStartModule extends WebModule implements StartModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //1.DB -- 使用 c3p0 连接池，连接内存 HSQL 数据库
        apiBinder.installModule(new SimpleDBModule("default", buildC3p0("hsql", apiBinder)));
        //2.MVC
        apiBinder.installModule(new ControllerModule() {
            protected void loadController(LoadHellper hellper) {
                hellper.loadType(UserAction.class);
            }
        });
        //3.RSF
        apiBinder.installModule(new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                // TODO Auto-generated method stub
            }
        });
        //4.Search
        Settings settings = apiBinder.getEnvironment().getSettings();
        String hostIP = settings.getString("demo-searchServer.host");
        int hostPort = settings.getInteger("demo-searchServer.port");
        String hostCoreName = settings.getString("demo-searchServer.coreName");
        SearchServerFactory factory = new SearchServerFactory();
        SearchServer server = factory.connect(hostIP, hostPort);
        apiBinder.bindType(SearchServices.class, new SearchServices(hostCoreName, server));
        //5.
    }
    public void onStart(AppContext appContext) throws Throwable {
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        //
        jdbc.loadSQL("UTF-8", "/net/test/data/TB_User.sql");
        jdbc.loadSQL("UTF-8", "/net/test/data/TB_User_Data.sql");
    }
    //
    //
    //
    private DataSource buildC3p0(String dbConfig, ApiBinder apiBinder) throws PropertyVetoException {
        //1.获取数据库连接配置信息
        Settings settings = apiBinder.getEnvironment().getSettings();
        String driverString = settings.getString("demo-jdbc-" + dbConfig + ".driver");
        String urlString = settings.getString("demo-jdbc-" + dbConfig + ".url");
        String userString = settings.getString("demo-jdbc-" + dbConfig + ".user");
        String pwdString = settings.getString("demo-jdbc-" + dbConfig + ".password");
        //2.创建数据库连接池
        int poolMaxSize = 200;
        LoggerHelper.logInfo("C3p0 Pool Info maxSize is ‘%s’ driver is ‘%s’ jdbcUrl is‘%s’", poolMaxSize, driverString, urlString);
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