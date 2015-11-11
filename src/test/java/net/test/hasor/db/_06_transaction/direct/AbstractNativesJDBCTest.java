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
package net.test.hasor.db._06_transaction.direct;
import static net.test.hasor.test.utils.HasorUnit.newID;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallback;
import net.hasor.db.transaction.TransactionStatus;
import net.test.hasor.db._06_transaction.plugins.AbstractSimpleJDBCTest;
/***
 * 数据库测试程序基类，监控线程
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractNativesJDBCTest extends AbstractSimpleJDBCTest {
    protected abstract Propagation testPropagation();
    //
    // - 事务1
    protected void doTransactionalA(final JdbcTemplate jdbcTemplate) throws Throwable {
        {
            /*默罕默德*/
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        this.tranTemplate.execute(new TransactionCallback<Void>() {
            /*安妮.贝隆、吴广*/
            public Void doTransaction(TransactionStatus tranStatus) throws Throwable {
                doTransactionalB(jdbcTemplate);
                return null;
            }
        }, testPropagation());
        {
            /*赵飞燕*/
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
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
            Thread.sleep(1000);
        }
        {
            String insertUser = "insert into TB_User values(?,'吴广','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘吴广’...");
            jdbcTemplate.update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        System.out.println("commit T2!");
        Thread.sleep(1000);
    }
}