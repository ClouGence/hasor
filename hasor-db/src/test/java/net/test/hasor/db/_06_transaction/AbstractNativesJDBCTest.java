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
package net.test.hasor.db._06_transaction;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.test.hasor.db.junit.DaemonThread;
import net.test.hasor.db.junit.HasorUnit;
import org.junit.Before;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.test.hasor.db.junit.HasorUnit.newID;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractNativesJDBCTest {
    @Inject
    protected            AppContext    appContext   = null;
    @Inject
    protected            DataSource    dataSource   = null;
    private static final AtomicInteger signalObject = new AtomicInteger(0);
    //
    @Before
    public void initData() throws SQLException, IOException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        try {
            jdbcTemplate.execute("drop table TB_User");
        } catch (Exception e) {
            e.printStackTrace();
        }
        jdbcTemplate.loadSQL("TB_User.sql");
    }
    //
    //
    /*--------------------------------------------------------------------------------WatchThread*/
    /**新增用户：默罕默德*/
    protected void insertUser_MHMD() throws SQLException, InterruptedException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
        System.out.println("insert new User ‘默罕默德’...");
        jdbcTemplate.update(insertUser, newID());//执行插入语句
        Thread.sleep(500);
    }
    /**新增用户：赵飞燕*/
    protected void insertUser_ZFY() throws SQLException, InterruptedException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
        System.out.println("insert new User ‘赵飞燕’...");
        jdbcTemplate.update(insertUser, newID());//执行插入语句
        Thread.sleep(500);
    }
    /**新增用户：安妮.贝隆*/
    protected void insertUser_ANBL() throws SQLException, InterruptedException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
        System.out.println("insert new User ‘安妮.贝隆’...");
        jdbcTemplate.update(insertUser, newID());//执行插入语句
        Thread.sleep(500);
    }
    /**新增用户：吴广*/
    protected void insertUser_WG() throws SQLException, InterruptedException {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        String insertUser = "insert into TB_User values(?,'吴广','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
        System.out.println("insert new User ‘吴广’...");
        jdbcTemplate.update(insertUser, newID());//执行插入语句
        Thread.sleep(500);
    }
    //
    //
    /**通知监控线程打印数据*/
    protected void printData() throws InterruptedException {
        signalObject.getAndIncrement();
        Thread.sleep(1000);
    }
    /**监视一张表的变化，当表的内容发生变化打印全表的内容。*/
    @DaemonThread
    public final void threadWatchTable() throws SQLException, NoSuchAlgorithmException, InterruptedException {
        try {
            Connection conn = dataSource.getConnection();
            //设置隔离级别读取未提交的数据是不允许的。
            conn.setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            while (true) {
                Thread.sleep(100);
                if (signalObject.get() % 2 == 1) {
                    String selectSQL = "select * from TB_User";
                    JdbcTemplate jdbc = new JdbcTemplate(conn);
                    List<Map<String, Object>> dataList = jdbc.queryForList(selectSQL);
                    System.out.print("监控线程：");
                    HasorUnit.printMapList(dataList);
                    signalObject.getAndIncrement();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}