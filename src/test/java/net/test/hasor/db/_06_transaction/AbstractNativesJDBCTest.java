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
package net.test.hasor.db._06_transaction;
import static net.test.hasor.junit.HasorUnit.newID;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.test.hasor.junit.DaemonThread;
import net.test.hasor.junit.HasorUnit;
import org.junit.Before;
import org.more.util.StringUtils;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractNativesJDBCTest {
    @Inject
    protected AppContext appContext = null;
    @Inject
    protected DataSource dataSource = null;
    //
    @Before
    public void initData() throws SQLException, IOException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        boolean hasTab = jdbcTemplate.tableExist("TB_User");
        if (hasTab == false) {
            jdbcTemplate.loadSQL("TB_User.sql");
        }
        jdbcTemplate.execute("delete from TB_User;");
    }
    //
    // - 事务1
    protected void doTransactionalA(final JdbcTemplate jdbcTemplate) throws Throwable {
        {
            /*默罕默德*/
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            signalObject.getAndIncrement();
            Thread.sleep(1000);
        }
        {
            /*安妮.贝隆、吴广*/
            doTransactionalB(jdbcTemplate);
        }
        {
            /*赵飞燕*/
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            signalObject.getAndIncrement();
            Thread.sleep(1000);
        }
    }
    //
    // - 事务2
    protected void doTransactionalB(JdbcTemplate jdbcTemplate) throws Throwable {
        System.out.println("begin T2!");
        Thread.sleep(1000);
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            signalObject.getAndIncrement();
            Thread.sleep(1000);
        }
        {
            String insertUser = "insert into TB_User values(?,'吴广','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘吴广’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            signalObject.getAndIncrement();
            Thread.sleep(1000);
        }
        System.out.println("commit T2!");
        Thread.sleep(1000);
    }
    //
    //
    /*--------------------------------------------------------------------------------WatchThread*/
    /**监视一张表的变化，当表的内容发生变化打印全表的内容。*/
    @DaemonThread
    public final void threadWatchTable() throws SQLException, NoSuchAlgorithmException, InterruptedException {
        String tableName = "TB_User";
        if (StringUtils.isBlank(tableName))
            return;
        //
        Connection conn = dataSource.getConnection();
        //设置隔离级别读取未提交的数据是不允许的。
        conn.setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
        while (true) {
            Thread.sleep(100);
            if (signalObject.get() % 2 == 1) {
                String selectSQL = "select * from " + tableName;
                JdbcTemplate jdbc = new JdbcTemplate(conn);
                List<Map<String, Object>> dataList = jdbc.queryForList(selectSQL);
                HasorUnit.printMapList(dataList);
                signalObject.getAndIncrement();
            }
        }
    }
}