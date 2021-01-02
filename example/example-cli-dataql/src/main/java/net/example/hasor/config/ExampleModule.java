package net.example.hasor.config;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.spring.SpringModule;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.Tel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.*;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    protected static Logger               logger        = LoggerFactory.getLogger(ExampleModule.class);
    @Autowired
    private          ApplicationArguments applicationArguments;
    @Value("${spring.datasource.url}")
    private          String               dataSourceUrl = null;
    @Value("${spring.datasource.username}")
    private          String               username      = null;
    @Value("${spring.datasource.password}")
    private          String               password      = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .数据库
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(this.dataSourceUrl);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(5);
        apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
        // .DataQL
        //        apiBinder.tryCast(QueryApiBinder.class).loadFragment(apiBinder.findClass(DimFragment.class));
        //        apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
        //
        ConsoleApiBinder.HostBuilder hostBuilder = apiBinder.tryCast(ConsoleApiBinder.class).asHostWithSTDO().answerExit();
        hostBuilder.preCommand(this.applicationArguments.getSourceArgs()).loadExecutor(apiBinder.findClass(Tel.class));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // 初始化内容数据库
        Map<String, String> loadMapper = new HashMap<>();
        loadMapper.put("MY_OPTION", "/ddl_sql_option.sql");
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        List<String> tables = jdbcTemplate.queryForList("show tables;", String.class);//SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;
        for (String tableName : tables) {
            loadMapper.remove(tableName.toUpperCase());
        }
        //
        for (String loadItem : loadMapper.keySet()) {
            String mapperPath = loadMapper.get(loadItem);
            logger.info("loadSQL {} -> {}.", loadItem, mapperPath);
            jdbcTemplate.loadSQL("UTF-8", mapperPath);
            jdbcTemplate.executeUpdate("insert into `my_option`(`id`,`key`,`value`,`desc`,`create_time`,`modify_time`) values (?,?,?,?,?,?)",//
                    UUID.randomUUID().toString(),   // id
                    "self",                                 // key
                    "self-value",                           // value
                    "self-desc",                            // desc
                    new Date(), new Date());
        }
    }
}
