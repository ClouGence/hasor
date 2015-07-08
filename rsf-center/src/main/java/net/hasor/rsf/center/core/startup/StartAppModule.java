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
package net.hasor.rsf.center.core.startup;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.core.StartModule;
import net.hasor.core.XmlNode;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.interceptor.simple.SimpleTranInterceptorModule;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.Validation;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.LoadHellper;
import net.hasor.rsf.center.core.freemarker.FreemarkerHttpServlet;
import net.hasor.rsf.center.core.freemarker.loader.DirTemplateLoader;
import net.hasor.rsf.center.core.mybatis.SqlExecutorTemplate;
import net.hasor.rsf.center.core.mybatis.SqlExecutorTemplateProvider;
import net.hasor.rsf.center.domain.dao.Dao;
import net.hasor.rsf.center.domain.valid.ValidDefine;
import net.hasor.web.WebApiBinder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import freemarker.template.Configuration;
/**
 * WebMVC各组件初始化配置。
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartAppModule extends ControllerModule implements StartModule {
    public static final String DataSource_MEM = "mem";
    public static final String DataSource_DB  = "db";
    //
    @Override
    protected void loadController(LoadHellper helper) throws Throwable {
        WebApiBinder apiBinder = helper.apiBinder();
        //1.Dao
        Set<Class<?>> daoSet = apiBinder.getEnvironment().findClass(Dao.class);
        for (Class<?> daoType : daoSet) {
            apiBinder.bindType(daoType);
        }
        //2.Valid
        Set<Class<?>> validSet = apiBinder.getEnvironment().findClass(ValidDefine.class);
        for (Class<?> validType : validSet) {
            if (Validation.class.isAssignableFrom(validType)) {
                ValidDefine validDefine = validType.getAnnotation(ValidDefine.class);
                helper.loadValidation(validDefine.value(), (Class<Validation>) validType);
            }
        }
        //3.Controller
        Set<Class<?>> controllerSet = apiBinder.getEnvironment().findClass(ModelController.class);
        for (Class<?> controllerType : controllerSet) {
            if (controllerType.isAnnotationPresent(MappingTo.class)) {
                helper.loadType((Class<? extends ModelController>) controllerType);
            }
        }
        //4.Freemarker
        String realPath = helper.apiBinder().getServletContext().getRealPath("/");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("utf-8");
        configuration.setOutputEncoding("utf-8");
        configuration.setLocalizedLookup(true);
        configuration.setTemplateLoader(new DirTemplateLoader(new File(realPath)));
        helper.apiBinder().bindType(Configuration.class).toInstance(configuration);
        helper.apiBinder().serve("*.htm", "*.html").with(FreemarkerHttpServlet.class);
        //5.MyBatis
        {
            //HSQL
            String driverString = "org.hsqldb.jdbcDriver";
            String urlString = "jdbc:hsqldb:mem:rsf_memdb";
            String userString = "sa";
            String pwdString = "";
            DataSource dataSource = createDataSource(driverString, urlString, userString, pwdString);
            Reader reader = Resources.getResourceAsReader("ibatis-mem-sqlmap.xml");
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
            this.configDataSource(apiBinder, dataSource, DataSource_MEM, sessionFactory);
        }
        {
            //MySQL
            Settings settings = apiBinder.getEnvironment().getSettings();
            String driverString = settings.getString("rsfCenter.jdbcConfig.driver");
            String urlString = settings.getString("rsfCenter.jdbcConfig.url");
            String userString = settings.getString("rsfCenter.jdbcConfig.username");
            String pwdString = settings.getString("rsfCenter.jdbcConfig.password");
            DataSource dataSource = createDataSource(driverString, urlString, userString, pwdString);
            Reader reader = Resources.getResourceAsReader("ibatis-db-sqlmap.xml");
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
            this.configDataSource(apiBinder, dataSource, DataSource_DB, sessionFactory);
        }
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        Settings settings = appContext.getEnvironment().getSettings();
        XmlNode xmlNode = settings.getXmlNode("rsfCenter.memInitialize");
        if (xmlNode == null || xmlNode.getChildren("sqlScript") == null) {
            throw new IOException("read config error,`rsfCenter.memInitialize` node is not exist.");
        }
        List<XmlNode> xmlNodes = xmlNode.getChildren("sqlScript");
        if (xmlNodes != null) {
            logger.info("sqlScript count = {}", xmlNodes.size());
            JdbcTemplate jdbcTemplate = appContext.findBindingBean(DataSource_MEM, JdbcTemplate.class);
            for (XmlNode node : xmlNodes) {
                String scriptName = node.getText().trim();
                try {
                    logger.info("sqlScript `{}` do...", scriptName);
                    jdbcTemplate.loadSQL(scriptName);
                    logger.info("sqlScript `{}` finish.", scriptName);
                } catch (Throwable e) {
                    logger.error("sqlScript `{}` run error =>{}.", scriptName, e);
                    throw e;
                }
            }
        }
    }
    //
    //
    //
    protected void configDataSource(ApiBinder apiBinder, DataSource dataSource, String dsName, SqlSessionFactory sessionFactory) throws Throwable {
        //1.绑定DataSource接口实现
        apiBinder.bindType(DataSource.class).nameWith(dsName).toInstance(dataSource);
        //2.绑定JdbcTemplate接口实现
        apiBinder.bindType(JdbcTemplate.class).nameWith(dsName).toProvider(new JdbcTemplateProvider(dataSource));
        //3.启用默认事务拦截器
        apiBinder.installModule(new SimpleTranInterceptorModule(dataSource));
        //4.绑定myBatis接口实现
        apiBinder.bindType(SqlExecutorTemplate.class).nameWith(dsName).toProvider(new SqlExecutorTemplateProvider(sessionFactory, dataSource));
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