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
package org.hasor.test.jdbc.single;
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
public class ThreadLocal_Test {
    @Test
    public void threadLocalTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>threadLocalTest<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("org/hasor/test/jdbc/hsql-config.xml");
        appContext.start();
        //
        /*测试 Connection 是否为线程唯一，如果线程内不唯一则有问题。 */
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
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
        //
        jdbc.execute("create table SYS_TB_User (userUUID nvarchar(36) not null , userName nvarchar(20) null);");
        jdbc.execute("insert into SYS_TB_User (userUUID,userName) values('AA','测试用户a');");
        jdbc.execute("insert into SYS_TB_User (userUUID,userName) values('BB','测试用户b');");
        jdbc.execute("insert into SYS_TB_User (userUUID,userName) values('CC','测试用户c');");
        jdbc.execute("insert into SYS_TB_User (userUUID,userName) values('DD','测试用户d');");
        //
        //
        System.out.println(jdbc.queryForInt("select count(*) from SYS_TB_User"));
        //
        DataSourceUtils.releaseConnection(localConn, jdbc.getDataSource());
    }
}