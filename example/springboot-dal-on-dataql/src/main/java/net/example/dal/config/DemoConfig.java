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
package net.example.dal.config;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.core.AppContext;
import net.hasor.dataql.DataQL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@Configuration(proxyBeanMethods = false)
public class DemoConfig {
    @Bean()
    public DataSource dataStore(//
            @Value("${spring.datasource.url}") String jdbcUrl,       //
            @Value("${spring.datasource.driver}") String driver,     //
            @Value("${spring.datasource.username}") String username, //
            @Value("${spring.datasource.password}") String password) throws SQLException {
        DruidDataSource druid = new DruidDataSource();
        druid.setUrl(jdbcUrl);
        druid.setDriverClassName(driver);
        druid.setUsername(username);
        druid.setPassword(password);
        druid.setMaxActive(50);
        druid.setMaxWait(3 * 1000);
        druid.setInitialSize(1);
        druid.setConnectionErrorRetryAttempts(1);
        druid.setBreakAfterAcquireFailure(true);
        druid.setTestOnBorrow(true);
        druid.setTestWhileIdle(true);
        druid.setFailFast(true);
        druid.init();
        return druid;
    }

    @Bean
    public DataQL dataQL(AppContext appContext) {
        return appContext.getInstance(DataQL.class);
    }
}