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
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import net.hasor.db.transaction.interceptor.Transactional;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import net.test.hasor.db.junit.ContextConfiguration;
import net.test.hasor.db.junit.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * NOT_SUPPORTED：如果当前没有事务存在，就以非事务方式执行；如果有，就将当前事务挂起。
 * @version : 2015年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class NOT_SUPPORTED_TranTest extends AbstractNativesJDBCTest {
    @Test
    public void testHasTransactional() throws Throwable {
        System.out.println("--->>NOT_SUPPORTED －> 前提：T1处于一个事务中，T2要求非事务。");
        System.out.println("--->>NOT_SUPPORTED －> 执行：T2在执行期间抛出一个异常，最终导致T1回滚。");
        System.out.println("--->>NOT_SUPPORTED －> 结论：T1录入的数据全部回滚、T2因为是非事务方式运行，所以T2数据生效。");
        System.out.println("--->>NOT_SUPPORTED －> 结果：数据库应存在：“安妮.贝隆”、“吴广”");
        System.out.println("--->>NOT_SUPPORTED －>  - 共计 2 条记录。");
        System.out.println();
        //
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute(new TransactionCallbackWithoutResult() {
            public void doTransactionWithoutResult(TransactionStatus tranStatus) throws Throwable {
                try {
                    System.out.println("begin T1!");
                    /*T1 - 默罕默德*/
                    insertUser_MHMD();
                    /*T2 - 安妮.贝隆、吴广*/
                    doTransactional();
                } catch (Exception e) {
                    System.out.println("rollback T1!");
                    tranStatus.setRollbackOnly();/*所有T1的数据全部回滚，不管是以录入数据库的还是将要录入数据库的*/
                } finally {
                    /*T1 - 赵飞燕*/
                    insertUser_ZFY();
                }
            }
        });
        //
        Thread.sleep(1000);
        printData();
    }
    @Test
    public void testNoneTransactional() throws Throwable {
        System.out.println("--->>NOT_SUPPORTED －> 前提：T1没有事务，T2要求非事务。");
        System.out.println("--->>NOT_SUPPORTED －> 执行：T2在执行期间抛出一个异常。");
        System.out.println("--->>NOT_SUPPORTED －> 结论：因为T1,T2都是已非事务方式运行，虽然T2抛出了异常但是不影响(数据库默认自动事务)事务的递交。");
        System.out.println("--->>NOT_SUPPORTED －> 结果：数据库应存在：“默罕默德”、“安妮.贝隆”、“吴广”、“赵飞燕”");
        System.out.println("--->>NOT_SUPPORTED －>  - 共计 4 条记录。");
        System.out.println();
        //
        try {
            System.out.println("begin T1!");
            /*T1 - 默罕默德*/
            insertUser_MHMD();
            /*T2 - 安妮.贝隆、吴广*/
            doTransactional();
        } catch (Exception e) {
            System.out.println("T2 has an error =" + e.getMessage());
        } finally {
            /*T1 - 赵飞燕*/
            insertUser_ZFY();
            System.out.println("finish T1!");
        }
        //
        Thread.sleep(1000);
        printData();
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void doTransactional() throws Throwable {
        System.out.println("begin T2!");
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
        //
        throw new Exception("throw exception in T2!");
    }
}