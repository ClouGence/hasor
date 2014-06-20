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
package net.test.simple.db._06_transaction.REQUIRES_NEW;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.support.JdbcTransactionManager;
import net.test.simple.db.AbstractSimpleJDBCTest;
import org.junit.Test;
/**
 * RROPAGATION_REQUIRES_NEW：独立事务
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class Tarn_REQUIRED_New_Test extends AbstractSimpleJDBCTest {
    /*条件：环境中没有事务。*/
    @Test
    public void noTarn_Test() throws IOException, URISyntaxException, SQLException {
        JdbcTemplate jdbc = this.getJdbcTemplate();
        TransactionManager tm = new JdbcTransactionManager(jdbc.getDataSource());
        {
            //begin
            TransactionStatus status = tm.getTransaction(Propagation.RROPAGATION_REQUIRES_NEW);
            jdbc.execute("insert into TB_User values('deb4f4c8','安妮.TD.雨果','belon','123','belon@hasor.net','2011-06-08 20:08:08');");//执行插入语句
            System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8'"));
            //commit
            //status.setReadOnly();//这是这个事务为只读事务（所有递交操作会被回滚）
            tm.commit(status);
        }
        System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8'"));
    }
    /*条件：环境中存在事务。*/
    @Test
    public void hasTarn_Test() throws IOException, URISyntaxException, SQLException {
        JdbcTemplate jdbc = this.getJdbcTemplate();
        TransactionManager tm = new JdbcTransactionManager(jdbc.getDataSource());
        //1.获取连接并创建事务
        Connection con = DataSourceUtils.getConnection(jdbc.getDataSource());
        con.setAutoCommit(false);
        {
            //begin
            TransactionStatus status = tm.getTransaction(Propagation.RROPAGATION_REQUIRES_NEW);
            jdbc.execute("insert into TB_User values('deb4f4c8','安妮.TD.雨果','belon','123','belon@hasor.net','2011-06-08 20:08:08');");//执行插入语句
            System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8'"));
            //commit
            tm.commit(status);
            System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8'"));
        }
        DataSourceUtils.releaseConnection(con, jdbc.getDataSource());
    }
}