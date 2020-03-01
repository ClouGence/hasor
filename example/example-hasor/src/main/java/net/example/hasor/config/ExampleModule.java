package net.example.hasor.config;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.core.Module;
import net.hasor.dataql.DimFragment;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@DimModule
@Component
public class ExampleModule implements Module {
    protected static Logger logger = LoggerFactory.getLogger(ExampleModule.class);

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .数据库
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://daily.db.hasor.net:3306/example");
        dataSource.setUsername("example");
        dataSource.setPassword("LonkP-jW3@ptJPxePf");
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(5);
        apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
        // .DataQL
        apiBinder.tryCast(QueryApiBinder.class).loadFragment(apiBinder.findClass(DimFragment.class));
        apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
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
