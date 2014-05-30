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
package net.test.simple.db._06_transaction.NEVER;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.TransactionBehavior;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.test.simple.db.AbstractJDBCTest;
import org.junit.Test;
/**
 * RROPAGATION_NESTED：嵌套事务
 *      条件：环境中没有事务。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class HaveTarn_NESTEDTest extends AbstractJDBCTest {
    @Test
    public void hasTarn_Test() throws IOException, URISyntaxException, SQLException {
        JdbcTemplate jdbc = this.getJdbcTemplate();
        TransactionManager tm = new DefaultTransactionManager(jdbc.getDataSource());
        //1.获取连接并创建事务
        Connection con = DataSourceUtils.getConnection(jdbc.getDataSource());
        con.setAutoCommit(false);
        System.out.println(jdbc.queryForInt("select count(*) from TB_User "));
        jdbc.execute("insert into TB_User values('18c48158','蒙奇.TD.雨果','belon','123','belon@hasor.net','2011-06-08 20:08:08');");//执行插入语句
        {
            //begin
            TransactionStatus status = tm.getTransaction(TransactionBehavior.PROPAGATION_NESTED);
            jdbc.execute("insert into TB_User values('deb4f4c8','安妮.TD.雨果','belon','123','belon@hasor.net','2011-06-08 20:08:08');");//执行插入语句
            System.out.println(jdbc.queryForInt("select count(*) from TB_User"));
            //rollBack
            tm.rollBack(status);
            System.out.println(jdbc.queryForInt("select count(*) from TB_User "));
        }
        DataSourceUtils.releaseConnection(con, jdbc.getDataSource());
    }
}