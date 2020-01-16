package net.example.hasor;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.dataql.DimFragment;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExampleModule implements Module {
    protected static Logger logger = LoggerFactory.getLogger(ExampleModule.class);

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .数据库
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.hsqldb.jdbcDriver");
        dataSource.setJdbcUrl("jdbc:hsqldb:file:user.db;close_result=true;ifexists=true;");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        dataSource.setMaxPoolSize(10);
        dataSource.setInitialPoolSize(1);
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
        // .DataQL
        apiBinder.tryCast(QueryApiBinder.class).loadFragment(apiBinder.findClass(DimFragment.class));
        apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // 初始化内容数据库
        Map<String, String> loadMapper = new HashMap<>();
        loadMapper.put("MYOPTION", "/ddl_sql_option.sql");
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        List<String> tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;", String.class);
        for (String tableName : tables) {
            loadMapper.remove(tableName.toUpperCase());
        }
        //
        for (String loadItem : loadMapper.keySet()) {
            String mapperPath = loadMapper.get(loadItem);
            logger.info("loadSQL {} -> {}.", loadItem, mapperPath);
            jdbcTemplate.loadSQL("UTF-8", mapperPath);
            jdbcTemplate.executeUpdate("insert into MyOption (id,key,value,desc,create_time,modify_time) values (?,?,?,?,?,?)",//
                    UUID.randomUUID().toString(),   // id
                    "self",                                 // key
                    "self-value",                           // value
                    "self-desc",                            // desc
                    new Date(), new Date());
        }
    }
}
