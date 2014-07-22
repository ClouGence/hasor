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
package net.test.simple.db._06_transaction.natives;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.test.junit.DaemonThread;
import net.hasor.test.utils.HasorUnit;
import org.junit.Before;
import org.more.util.CommonCodeUtils;
import org.more.util.StringUtils;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractNativesJDBCTest implements AppContextAware {
    /*--------------------------------------------------------------------------------WatchThread*/
    /**监控线程使用的事务隔离级别*/
    protected Isolation getWatchThreadTransactionLevel() {
        return Isolation.valueOf(Connection.TRANSACTION_READ_COMMITTED);
    }
    /**要监控的表名*/
    protected String watchTable() {
        return "TB_User";
    }
    /**监视一张表的变化，当表的内容发生变化打印全表的内容。*/
    @DaemonThread
    public final void threadWatchTable() throws SQLException, NoSuchAlgorithmException, InterruptedException {
        String tableName = watchTable();
        if (StringUtils.isBlank(tableName))
            return;
        //
        String hashValue = "";
        DataSource dataSource = getDataSource();
        Connection conn = dataSource.getConnection();
        //设置隔离级别读取未提交的数据是不允许的。
        conn.setTransactionIsolation(getWatchThreadTransactionLevel().ordinal());
        while (true) {
            String selectSQL = "select * from " + tableName;
            String selectCountSQL = "select count(*) from " + tableName;
            //
            JdbcTemplate jdbc = new JdbcTemplate(conn);
            List<Map<String, Object>> dataList = jdbc.queryForList(selectSQL);
            int rowCount = jdbc.queryForInt(selectCountSQL);
            String logData = HasorUnit.printMapList(dataList, false);
            String localHashValue = CommonCodeUtils.MD5.getMD5(logData);
            if (!StringUtils.equals(hashValue, localHashValue)) {
                System.out.println(String.format("watch : -->Table ‘%s’ rowCount = %s.", tableName, rowCount));
                System.out.println(logData);
                hashValue = localHashValue;
            } else {
                System.out.println("watch : table no change.");
            }
            //
            Thread.sleep(500);
        }
    }
    /*--------------------------------------------------------------------------------------Utils*/
    //
    private AppContext appContext = null;
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public AppContext getAppContext() {
        return appContext;
    }
    protected JdbcTemplate getJdbcTemplate() {
        return appContext.getInstance(JdbcTemplate.class);
    }
    protected DataSource getDataSource() {
        return appContext.getInstance(DataSource.class);
    }
    /*---------------------------------------------------------------------------------------Tran*/
    //
    private TransactionManager transactionManager = null;
    @Before
    public void initTran() {
        this.transactionManager = Manager.getTransactionManager(this.getDataSource());
    }
    /**开始事物*/
    protected TransactionStatus begin(Propagation behavior) throws SQLException {
        return this.transactionManager.getTransaction(behavior);
    }
    /**递交事物*/
    protected void commit(TransactionStatus status) throws SQLException {
        this.transactionManager.commit(status);
    }
    /**回滚事物*/
    protected void rollBack(TransactionStatus status) throws SQLException {
        this.transactionManager.rollBack(status);
    }
    /*-----------------------------------------------------------------------------------InitData*/
    //
    @Before
    public void initData() throws SQLException {
        this.getJdbcTemplate().execute("delete from TB_User;");
    }
}
////2.基类配置
//@Before
//public void initContext() throws IOException, URISyntaxException, SQLException {
//    this.appContext = HasorFactory.createAppContext("net/test/simple/db/jdbc-config.xml", new SimpleJDBCWarp());
//    /*装载 SQL 脚本文件*/
//    JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
//    if (jdbc.tableExist("TB_User") == false) {
//        //jdbc.execute("drop table TB_User");
//        jdbc.loadSQL("UTF-8", "net/test/simple/db/TB_User.sql");
//        jdbc.loadSQL("UTF-8", "net/test/simple/db/TB_User_Data.sql");
//    }
//}