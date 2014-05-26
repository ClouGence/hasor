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
package net.test.simple._10_jdbc;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.hasor.quick.anno.AnnoStandardAppContext;
import org.junit.Before;
/***
 * 基本增删改查测试
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class AbstractJDBCTest {
    private static String config     = "net/test/simple/_10_jdbc/jdbc-config.xml";
    private AppContext    appContext = null;
    @Before
    public void initContext() throws IOException, URISyntaxException, SQLException {
        appContext = new AnnoStandardAppContext(config);
        appContext.start();
        /*装载 SQL 脚本文件*/
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        jdbc.loadSQL("net/test/simple/_10_jdbc/TB_User.sql");
        jdbc.loadSQL("net/test/simple/_10_jdbc/TB_User_Data.sql");
    }
    protected JdbcTemplate getJdbcTemplate() throws IOException, URISyntaxException {
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        return jdbc;
    }
    protected DataSource getDataSource() throws IOException, URISyntaxException {
        DataSource jdbc = appContext.getInstance(DataSource.class);
        return jdbc;
    }
}