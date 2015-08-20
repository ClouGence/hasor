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
package net.hasor.rsf.center.core.dao;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.StartModule;
import net.hasor.core.XmlNode;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.plugins.tran.interceptor.TranInterceptorModule;
import net.hasor.rsf.center.core.mybatis.SqlExecutorTemplate;
import net.hasor.rsf.center.core.mybatis.SqlExecutorTemplateProvider;
import net.hasor.rsf.center.domain.constant.WorkMode;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class DaoModule extends WebModule implements StartModule {
    private WorkMode workAt = null;
    public DaoModule(WorkMode workAt) {
        this.workAt = workAt;
    }
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //Dao
        Set<Class<?>> daoSet = apiBinder.getEnvironment().findClass(Dao.class);
        for (Class<?> daoType : daoSet) {
            apiBinder.bindType(daoType);
        }
        //DataSource
        Settings settings = apiBinder.getEnvironment().getSettings();
        String driverString = settings.getString("rsfCenter.jdbcConfig.driver");
        String urlString = settings.getString("rsfCenter.jdbcConfig.url");
        String userString = settings.getString("rsfCenter.jdbcConfig.username");
        String pwdString = settings.getString("rsfCenter.jdbcConfig.password");
        if (WorkMode.Alone == workAt) {
            driverString = "org.hsqldb.jdbcDriver";
            urlString = "jdbc:hsqldb:mem:rsf_memdb";
            userString = "sa";
            pwdString = "";
        }
        DataSource dataSource = createDataSource(driverString, urlString, userString, pwdString);
        Reader reader = Resources.getResourceAsReader("ibatis-sqlmap.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        this.configDataSource(apiBinder, dataSource, sessionFactory);
    }
    public void onStart(AppContext appContext) throws Throwable {
        Environment env = appContext.getEnvironment();
        Settings settings = env.getSettings();
        //
        //Alone模式
        if (WorkMode.Alone == workAt) {
            logger.info("rsf workAt {} , initialize memdb.", workAt);
            XmlNode xmlNode = settings.getXmlNode("rsfCenter.memInitialize");
            if (xmlNode == null || xmlNode.getChildren("sqlScript") == null) {
                throw new IOException("read config error,`rsfCenter.memInitialize` node is not exist.");
            }
            List<XmlNode> xmlNodes = xmlNode.getChildren("sqlScript");
            if (xmlNodes != null) {
                logger.info("sqlScript count = {}", xmlNodes.size());
                JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
                for (XmlNode node : xmlNodes) {
                    String scriptName = node.getText().trim();
                    try {
                        logger.info("sqlScript `{}` do...", scriptName);
                        jdbcTemplate.loadSQL("UTF-8", scriptName);
                        logger.info("sqlScript `{}` finish.", scriptName);
                    } catch (Throwable e) {
                        logger.error("sqlScript `{}` run error =>{}.", scriptName, e);
                        throw e;
                    }
                }
            }
        }
    }
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
    protected void configDataSource(ApiBinder apiBinder, DataSource dataSource, SqlSessionFactory sessionFactory) throws Throwable {
        //1.绑定DataSource接口实现
        apiBinder.bindType(DataSource.class).toInstance(dataSource);
        //2.绑定JdbcTemplate接口实现
        apiBinder.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(dataSource));
        //3.启用默认事务拦截器
        apiBinder.installModule(new TranInterceptorModule(dataSource));
        //4.绑定myBatis接口实现
        apiBinder.bindType(SqlExecutorTemplate.class).toProvider(new SqlExecutorTemplateProvider(sessionFactory, dataSource));
    }
}