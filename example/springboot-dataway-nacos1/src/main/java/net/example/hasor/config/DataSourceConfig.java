package net.example.hasor.config;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.JdbcUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.Charsets;
import net.hasor.utils.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

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