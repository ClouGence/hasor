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
package net.test.simple.db._06_transaction.simple.MANDATORY;
import static net.hasor.test.utils.HasorUnit.newID;
import java.sql.SQLException;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.simple.Transactional;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.test.simple.db.SimpleJDBCWarp;
import net.test.simple.db._06_transaction.natives.AbstractNativesJDBCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * PROPAGATION_MANDATORY：要求环境中存在事务
 *   -条件：环境中没有事务，事务管理器会引发异常。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = SimpleJDBCWarp.class)
public class NoTarn_MANDATORY_Test extends AbstractNativesJDBCTest {
    @Test
    public void noTarn_MANDATORY_Test() throws Exception {
        System.out.println("--->>NoTarn_MANDATORY_Test<<--");
        /* 在开启事务时引发异常，因为环境中不存在事务。 */
        try {
            //T2
            Thread.sleep(1000);
            this.executeTransactional();
            Thread.sleep(1000);
            throw new Exception("测试未通过。");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    //
    //
    @Transactional(propagation = Propagation.MANDATORY)
    public void executeTransactional() throws Exception {
        String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
        System.out.println("insert new User ‘安妮.贝隆’...");
        this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
        System.out.println("commit Transaction!");
    }
}