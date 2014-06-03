/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple.db;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Settings;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.TransactionLevel;
import org.junit.Before;
import com.mchange.v2.c3p0.ComboPooledDataSource;
/***
 * 数据库测试程序基类
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractSimpleJDBCTest extends AbstractJDBCTest {
    private AppContext appContext = null;
    protected DataSource getWatchThreadDataSource() {
        return getDataSource();
    }
    protected TransactionLevel getWatchThreadTransactionLevel() {
        return TransactionLevel.valueOf(Connection.TRANSACTION_READ_COMMITTED);
    }
    @Before
    public void initContext() throws IOException, URISyntaxException, SQLException {
        this.appContext = Hasor.createAppContext("net/test/simple/db/jdbc-config.xml", new SimpleJDBCWarp());
        /*装载 SQL 脚本文件*/
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        if (jdbc.tableExist("TB_User") == false) {
            //jdbc.execute("drop table TB_User");
            jdbc.loadSQL("UTF-8", "net/test/simple/db/TB_User.sql");
            jdbc.loadSQL("UTF-8", "net/test/simple/db/TB_User_Data.sql");
        }
    }
    protected JdbcTemplate getJdbcTemplate() {
        return appContext.getInstance(JdbcTemplate.class);
    }
    protected DataSource getDataSource() {
        return appContext.getInstance(DataSource.class);
    }
}
class SimpleJDBCWarp implements Module {
    public void init(ApiBinder apiBinder) throws Throwable {
        //1.获取数据库连接配置信息
        Settings settings = apiBinder.getSettings();
        String driverString = settings.getString("hasor-jdbc.driver");
        String urlString = settings.getString("hasor-jdbc.url");
        String userString = settings.getString("hasor-jdbc.user");
        String pwdString = settings.getString("hasor-jdbc.password");
        int poolMaxSize = 200;
        Hasor.logInfo("C3p0 Pool Info maxSize is ‘%s’ driver is ‘%s’ jdbcUrl is‘%s’", poolMaxSize, driverString, urlString);
        //
        //2.创建数据库连接池
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverString);
        dataSource.setJdbcUrl(urlString);
        dataSource.setUser(userString);
        dataSource.setPassword(pwdString);
        dataSource.setMaxPoolSize(poolMaxSize);
        dataSource.setInitialPoolSize(1);
        //dataSource.setAutomaticTestTable("DB_TEST_ATest001");
        dataSource.setIdleConnectionTestPeriod(18000);
        dataSource.setCheckoutTimeout(3000);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(30);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(25000);
        //
        //3.绑定DataSource接口实现
        apiBinder.bindingType(DataSource.class).toInstance(dataSource);
        //4.绑定JdbcTemplate接口实现
        apiBinder.bindingType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(dataSource));
    }
    public void start(AppContext appContext) throws Throwable {
        Hasor.logInfo("JDBCWarp started!");
    }
}