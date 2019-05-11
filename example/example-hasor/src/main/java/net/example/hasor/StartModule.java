package net.example.hasor;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.core.exts.boot.BootBinder;
import net.hasor.core.exts.boot.BootLauncher;
import net.hasor.core.exts.boot.CommandLauncher;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
//
public class StartModule implements LifeModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static void main(String[] args) {
        BootLauncher.run(StartModule.class, args);
    }
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .数据库配置
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.hsqldb.jdbcDriver");
        dataSource.setJdbcUrl("jdbc:hsqldb:file:user.db");
        dataSource.setUser("sa");
        dataSource.setPassword("");
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
        //
        // .命令发现
        Set<Class<?>> aClassSet = apiBinder.findClass(MyCmd.class);
        for (Class<?> aClass : aClassSet) {
            if (CommandLauncher.class.isAssignableFrom(aClass)) {
                MyCmd myCmd = aClass.getAnnotation(MyCmd.class);
                apiBinder.tryCast(BootBinder.class).addCommand(0, myCmd.value(), (Class<? extends CommandLauncher>) aClass);
            }
        }
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // 初始化内容数据库
        Map<String, String> loadMapper = new HashMap<String, String>();
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
            logger.info("loadSQL for daily. {} -> {}.", loadItem, mapperPath);
            jdbcTemplate.loadSQL("UTF-8", mapperPath);
            jdbcTemplate.executeUpdate("insert into MyOption (id,key,value,desc,create_time,modify_time) values (?,?,?,?,?,?)",//
                    UUID.randomUUID().toString(),   // id
                    "self",                                // key
                    "self-value",                         // value
                    "self-desc",                           // desc
                    new Date(), new Date());
        }
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
    }
}