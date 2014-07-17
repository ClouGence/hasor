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
package net.test.simple.db._06_transaction.natives.NEVER;
import static net.hasor.test.utils.HasorUnit.newID;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.test.simple.db.SimpleJDBCWarp;
import net.test.simple.db._06_transaction.natives.AbstractNativesJDBCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * RROPAGATION_NEVER：排除事务
 *   -条件：环境中没有事务，事务会以autoCommit方式处理。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = SimpleJDBCWarp.class)
public class NoTarn_NEVER_Test extends AbstractNativesJDBCTest {
    protected String watchTable() {
        return "TB_User";
    }
    @Test
    public void haveTarn_NEVER_Test() throws Exception {
        System.out.println("--->>haveTarn_NEVER_Test<<--");
        Thread.sleep(3000);
        /* 预期执行结果为：
         *   0.暂停3秒，监控线程打印全表数据.
         *   1.开启事务..
         *   2.新建‘安妮.贝隆’用户..
         *   3.暂停3秒，监控线程打印全表数据.(包含‘安妮.贝隆’).
         *   4.新建‘赵飞燕’用户..
         *   5.暂停3秒，监控线程打印全表数据.(包含‘赵飞燕’).
         *   6.回滚事务..
         *   7.暂停3秒，监控线程打印变更之后的全表数据(包含‘安妮.贝隆’、‘赵飞燕’).
         *   6.新建‘默罕默德’用户..
         *   7.暂停3秒，监控线程打印全表数据.(包含‘默罕默德’).
         */
        TransactionStatus tranStatus = begin(Propagation.NEVER);
        System.out.println("begin Transaction!");
        //T1
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        {
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
        System.out.println("rollBack Transaction!");
        rollBack(tranStatus);
        {
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(3000);
        }
    }
}