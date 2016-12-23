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
import net.hasor.db.Transactional;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import net.hasor.plugins.junit.ContextConfiguration;
import net.hasor.plugins.junit.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * NEVER：如果当前没有事务存在，就以非事务方式执行；如果有，就抛出异常。
 * @version : 2015年11月15日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class NEVER_TranTest extends AbstractNativesJDBCTest {
    @Test
    public void testHasTransactional() throws Throwable {
        System.out.println("--->>NEVER －> 前提：T1在一个事务中，T2要求环境中不能存在事务。");
        System.out.println("--->>NEVER －> 执行：T1在事务中正常执行，当调用T2时，因不满足T2要求非事务的条件，而导致异常抛出。");
        System.out.println("--->>NEVER －> 结论：T1的数据全部被录入，T2没有被执行。反而抛出了异常。");
        System.out.println("--->>NEVER －> 结果：数据库应存在：“默罕默德”、“赵飞燕”");
        System.out.println("--->>NEVER －>  - 共计 2 条记录。");
        System.out.println();
        //
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute(new TransactionCallbackWithoutResult() {
            public void doTransactionWithoutResult(TransactionStatus tranStatus) throws Throwable {
                System.out.println("begin T1!");
                /*T1 - 默罕默德*/
                insertUser_MHMD();
                /*T2 - 安妮.贝隆、吴广*/
                try {
                    doTransactional();
                } catch (Exception e) {
                    System.out.println("T2 error = " + e.getMessage());
                } finally {
                    Thread.sleep(500);
                }
                /*T1 - 赵飞燕*/
                insertUser_ZFY();
                System.out.println("commit T1!");
            }
        });
        //
        Thread.sleep(1000);
        printData();
    }
    @Test
    public void testNoneTransactional() throws Throwable {
        System.out.println("--->>NEVER －> 前提：T1不存在事务，T2要求环境中不能存在事务。");
        System.out.println("--->>NEVER －> 执行：两个事务都顺利执行完毕。");
        System.out.println("--->>NEVER －> 结论：T1满足了T2非事务运行的条件，因此因此两个事务都可以正常执行。");
        System.out.println("--->>NEVER －> 结果：数据库应存在：“默罕默德”、“安妮.贝隆”、“吴广”、“赵飞燕”");
        System.out.println("--->>NEVER －>  - 共计 4 条记录。");
        System.out.println();
        //
        System.out.println("begin T1!");
        /*T1 - 默罕默德*/
        insertUser_MHMD();
        /*T2 - 安妮.贝隆、吴广*/
        doTransactional();
        /*T1 - 赵飞燕*/
        insertUser_ZFY();
        System.out.println("commit T1!");
        //
        Thread.sleep(1000);
        printData();
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.NEVER)
    public void doTransactional() throws Throwable {
        System.out.println("begin T2!");
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
        System.out.println("commit T2!");
    }
}