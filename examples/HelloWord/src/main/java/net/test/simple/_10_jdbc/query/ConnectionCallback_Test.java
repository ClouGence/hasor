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
package net.test.simple._10_jdbc.query;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.core.Hasor;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.jdbc.ConnectionCallback;
import net.hasor.jdbc.core.JdbcTemplate;
import net.hasor.jdbc.datasource.DataSourceUtils;
import net.hasor.jdbc.exceptions.DataAccessException;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class ConnectionCallback_Test {
    protected JdbcTemplate returnJdbcTemplate() throws Exception {
        try {
            AnnoStandardAppContext appContext = new AnnoStandardAppContext("org/hasor/test/jdbc/hsql-config.xml");
            appContext.start();
            /*测试 调用存储过程 */
            JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
            /*装载 SQL 脚本文件*/
            jdbc.loadSQL("net/test/simple/_09_jdbc/TB_User.sql");
            jdbc.loadSQL("net/test/simple/_09_jdbc/TB_User_Data.sql");
            return jdbc;
        } catch (Exception e) {
            throw e;
        }
    }
    @Test
    public void test_ConnectionThreadLocal() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_queryList_4_Object<<--");
        JdbcTemplate jdbc = returnJdbcTemplate();
        //
        /*测试 Connection 是否为线程唯一，如果线程内不唯一则有问题。 */
        final Connection localConn = DataSourceUtils.getConnection(jdbc.getDataSource());
        //
        jdbc.execute(new ConnectionCallback<Object>() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                // TODO Auto-generated method stub
                Hasor.logInfo("%s \t %s", localConn, con);
                return null;
            }
        });
        jdbc.execute(new ConnectionCallback<Object>() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                Hasor.logInfo("%s \t %s", localConn, con);
                return null;
            }
        });
        //
        System.out.println(jdbc.queryForInt("select count(*) from SYS_TB_User"));
        //
        DataSourceUtils.releaseConnection(localConn, jdbc.getDataSource());
    }
}