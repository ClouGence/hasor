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
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import org.more.util.CommonCodeUtils;
import org.more.util.StringUtils;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
//@RunWith(MyTestRunner.class)
public abstract class AbstractJDBCTest extends AbstractHasorUnit {
    protected abstract DataSource getWatchThreadDataSource();
    protected abstract Isolation getWatchThreadTransactionLevel();
    //
    private Thread watchThread = null;
    /**监视一张表的变化，当表的内容发生变化打印全表的内容。*/
    protected void watchTable(final String tableName) {
        this.watchThread = new Thread(new Runnable() {
            public void run() {
                try {
                    _run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            private void _run() throws Throwable {
                String hashValue = "";
                DataSource dataSource = getWatchThreadDataSource();
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
                    String logData = printMapList(dataList, false);
                    String localHashValue = CommonCodeUtils.MD5.getMD5(logData);
                    if (!StringUtils.equals(hashValue, localHashValue)) {
                        System.out.println(String.format("watch : -->Table ‘%s’ rowCount = %s.", tableName, rowCount));
                        System.out.println(logData);
                        hashValue = localHashValue;
                    } else {
                        System.out.println("watch : table no change.");
                    }
                    //
                    Thread.sleep(1000);
                }
            }
        });
        this.watchThread.setDaemon(true);
        this.watchThread.start();
    }
}