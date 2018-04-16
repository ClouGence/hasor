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
package net.example.hasor.core;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.core.Settings;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.annotation.MappingTo;

import java.util.Set;
/**
 * Hasor API 引导式配置
 * @version : 2015年12月25日
 * @author 赵永春 (zyc@hasor.net)
 */
public class StartModule extends WebModule implements LifeModule {
    /** init 阶段 */
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        // 设置请求响应编码
        apiBinder.setEncodingCharacter("UTF-8", "UTF-8");
        //
        // 设置freemarker渲染器
        apiBinder.suffix("htm").bind(MyFreemarkerRender.class);
        //
        // 扫描所有带有 @MappingTo 特征类
        Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
        // 对 aClass 集合进行发现并自动配置控制器
        apiBinder.looking4MappingTo(aClass);
        //
        // .数据库配置
        Settings settings = apiBinder.getEnvironment().getSettings();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.hsqldb.jdbcDriver");
        dataSource.setJdbcUrl(settings.getString("myApp.jdbcURL"));
        dataSource.setUser(settings.getString("myApp.userName"));
        dataSource.setPassword(settings.getString("myApp.userPassword"));
        dataSource.setMaxPoolSize(40);
        dataSource.setInitialPoolSize(3);
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
    }
    /** start 阶段 */
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // 初始化内容数据库
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        jdbcTemplate.loadSQL("utf-8", "/ddl_sql_user.sql");
        //
        jdbcTemplate.execute("insert into UserInfo values(1,'admin','pwd','administrator',now(),now())");
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
    }
}