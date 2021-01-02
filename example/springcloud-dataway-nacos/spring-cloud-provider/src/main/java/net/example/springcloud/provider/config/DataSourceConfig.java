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
package net.example.springcloud.provider.config;
import com.alibaba.druid.pool.DruidDataSource;
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
public class DataSourceConfig {
    // 数据仓库 1
    @Bean("dataDs1")
    public DataSource data1Store(//
            @Value("${spring.datasource.db1.url}") String jdbcUrl,       //
            @Value("${spring.datasource.db1.driver}") String driver,     //
            @Value("${spring.datasource.db1.username}") String username, //
            @Value("${spring.datasource.db1.password}") String password) throws SQLException {
        return this.buildDataSource(jdbcUrl, driver, username, password);
    }

    // 数据仓库 2
    @Bean("dataDs2")
    public DataSource data2Store(//
            @Value("${spring.datasource.db2.url}") String jdbcUrl,       //
            @Value("${spring.datasource.db2.driver}") String driver,     //
            @Value("${spring.datasource.db2.username}") String username, //
            @Value("${spring.datasource.db2.password}") String password) throws SQLException {
        return this.buildDataSource(jdbcUrl, driver, username, password);
    }

    private DataSource buildDataSource(String jdbcUrl, String driver, String username, String password) throws SQLException {
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
}