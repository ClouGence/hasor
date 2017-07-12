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
import net.example.domain.consumer.EchoService;
import net.example.domain.consumer.MessageService;
import net.example.domain.consumer.UserService;
import net.example.hasor.provider.EchoServiceImpl;
import net.example.hasor.provider.MessageServiceImpl;
import net.example.hasor.provider.UserServiceImpl;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.data.DataApiBinder;
import net.hasor.plugins.render.FreemarkerRender;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * Hasor API 引导式配置
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        apiBinder.setEncodingCharacter("utf-8", "utf-8");   //设置请求响应编码
        apiBinder.suffix("htm")//
                .bind(FreemarkerRender.class);              //设置 Freemarker 渲染器
        apiBinder.scanMappingTo();                          //扫描所有 @MappingTo 注解
        //
        this.loadDataSource(apiBinder);                     //连接数据库
        this.loadRpc(apiBinder.tryCast(RsfApiBinder.class));//发布分布式服务
    }
    //
    private void loadDataSource(ApiBinder apiBinder) throws Throwable {
        //
        // .数据库配置
        Environment env = apiBinder.getEnvironment();
        String driverString = env.evalString("%jdbc.driver%");
        String urlString = env.evalString("%jdbc.url%");
        String userString = env.evalString("%jdbc.user%");
        String pwdString = env.evalString("%jdbc.password%");
        //
        // .数据源
        int poolMaxSize = 40;
        logger.info("C3p0 Pool Info maxSize is ‘{}’ driver is ‘{}’ jdbcUrl is‘{}’",//
                poolMaxSize, driverString, urlString);
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverString);
        dataSource.setJdbcUrl(urlString);
        dataSource.setUser(userString);
        dataSource.setPassword(pwdString);
        dataSource.setMaxPoolSize(poolMaxSize);
        dataSource.setInitialPoolSize(3);
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        //
        // .数据库框架
        apiBinder.tryCast(DataApiBinder.class).addDataSource(dataSource);
    }
    //
    private void loadRpc(RsfApiBinder apiBinder) {
        //
        apiBinder.rsfService(EchoService.class).toInfo(         // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(EchoServiceImpl.class)       // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
        //
        apiBinder.rsfService(MessageService.class).toInfo(      // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(MessageServiceImpl.class)    // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
        //
        apiBinder.rsfService(UserService.class).toInfo(         // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(UserServiceImpl.class)       // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
    }
}