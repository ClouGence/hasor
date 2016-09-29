///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.demo.hasor.core;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
//import net.demo.hasor.core.mybatis.SqlExecutorTemplate;
//import net.demo.hasor.core.mybatis.SqlExecutorTemplateProvider;
//import net.demo.hasor.domain.AppConstant;
//import net.demo.hasor.manager.EnvironmentConfig;
//import net.hasor.core.*;
//import net.hasor.db.DBModule;
//import net.hasor.db.jdbc.core.JdbcTemplate;
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.more.util.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sql.DataSource;
//import java.beans.PropertyVetoException;
//import java.io.Reader;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
///**
// * 数据库链接 & DAO
// * @version : 2016年1月10日
// * @author 赵永春(zyc@hasor.net)
// */
//public class DataSourceModule implements LifeModule {
//    protected Logger logger = LoggerFactory.getLogger(getClass());
//    @Override
//    public void loadModule(ApiBinder apiBinder) throws Throwable {
//        // .内置数据(数据源一)
//        DataSource dataSource = createDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:example_memdb", "sa", "");
//        apiBinder.installModule(new DBModule(AppConstant.DB_HSQL, dataSource));
//        //
//        // .外置数据(数据源二)
//        Environment env = apiBinder.getEnvironment();
//        Settings settings = env.getSettings();
//        String driverString = env.evalString(settings.getString("jdbcSettings.jdbcDriver", ""));
//        String urlString = env.evalString(settings.getString("jdbcSettings.jdbcURL", ""));
//        String userString = env.evalString(settings.getString("jdbcSettings.userName", ""));
//        String pwdString = env.evalString(settings.getString("jdbcSettings.userPassword", ""));
//        //
//        DataSource mysqlDataSource = createDataSource(driverString, urlString, userString, pwdString);
//        apiBinder.installModule(new DBModule(AppConstant.DB_MYSQL, mysqlDataSource));
//        //
//        // .绑定myBatis接口实现(数据源二)
//        Reader reader = Resources.getResourceAsReader("ibatis-sqlmap.xml");
//        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
//        apiBinder.bindType(SqlExecutorTemplate.class).toProvider(new SqlExecutorTemplateProvider(sessionFactory, mysqlDataSource));
//        //
//    }
//    @Override
//    public void onStart(AppContext appContext) throws Throwable {
//        logger.info("loadSQL");
//        // .加载内置数据
//        JdbcTemplate jdbcTemplate = appContext.findBindingBean(AppConstant.DB_HSQL, JdbcTemplate.class);
//        jdbcTemplate.loadSQL("UTF-8", "/META-INF/sqlinner/ddl_sql_version_info.sql");
//        jdbcTemplate.loadSQL("UTF-8", "/META-INF/sqlinner/init_sql_version_info.sql");
//        //
//        // .日常环境下特殊处理
//        EnvironmentConfig config = appContext.getInstance(EnvironmentConfig.class);
//        if (StringUtils.equalsIgnoreCase("daily", config.getEnvType())) {
//            logger.info("loadSQL for daily.");
//            JdbcTemplate dailyTemplate = appContext.findBindingBean(AppConstant.DB_MYSQL, JdbcTemplate.class);
//            //
//            Map<String, String> loadMapper = new HashMap<String, String>();
//            loadMapper.put("USER_INFO", "/META-INF/sqlddl/ddl_sql_user_info.sql");
//            loadMapper.put("USER_SOURCE", "/META-INF/sqlddl/ddl_sql_user_source.sql");
//            //
//            List<String> tables = dailyTemplate.queryForList("SHOW TABLES LIKE '%';", String.class);
//            for (String tableName : tables) {
//                String tabKey = tableName.toUpperCase();
//                if (loadMapper.containsKey(tabKey)) {
//                    loadMapper.remove(tabKey);
//                }
//            }
//            //
//            for (String loadItem : loadMapper.keySet()) {
//                String mapperPath = loadMapper.get(loadItem);
//                logger.info("loadSQL for daily. {} -> {}.", loadItem, mapperPath);
//                dailyTemplate.loadSQL("UTF-8", mapperPath);
//            }
//            //
//            logger.info("loadSQL for daily. -> finish.");
//        }
//        //
//        logger.info("loadSQL -> finish.");
//    }
//    @Override
//    public void onStop(AppContext appContext) throws Throwable {
//        // TODO Auto-generated method stub
//    }
//    //
//    private DataSource createDataSource(String driverString, String urlString, String userString, String pwdString) throws PropertyVetoException {
//        int poolMaxSize = 40;
//        logger.info("C3p0 Pool Info maxSize is ‘{}’ driver is ‘{}’ jdbcUrl is‘{}’", poolMaxSize, driverString, urlString);
//        ComboPooledDataSource dataSource = new ComboPooledDataSource();
//        dataSource.setDriverClass(driverString);
//        dataSource.setJdbcUrl(urlString);
//        dataSource.setUser(userString);
//        dataSource.setPassword(pwdString);
//        dataSource.setMaxPoolSize(poolMaxSize);
//        dataSource.setInitialPoolSize(3);
//        dataSource.setAutomaticTestTable("DB_TEST_FORM_C3P0");
//        dataSource.setIdleConnectionTestPeriod(18000);
//        dataSource.setCheckoutTimeout(3000);
//        dataSource.setTestConnectionOnCheckin(true);
//        dataSource.setAcquireRetryDelay(1000);
//        dataSource.setAcquireRetryAttempts(30);
//        dataSource.setAcquireIncrement(1);
//        dataSource.setMaxIdleTime(25000);
//        return dataSource;
//    }
//}