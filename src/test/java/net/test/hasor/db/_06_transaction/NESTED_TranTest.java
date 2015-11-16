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
package net.test.hasor.db._06_transaction;
import net.hasor.db.Transactional;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import net.test.hasor.junit.ContextConfiguration;
import net.test.hasor.junit.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * NESTED：在当前事务中通过Savepoint方式开启一个子事务。
 * @version : 2015年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class NESTED_TranTest extends AbstractNativesJDBCTest {
    @Test
    public void testHasTransactional() throws Throwable {
        System.out.println("--->>SUPPORTS －> 前提：T1处于一个事务中，T2跟随T1。");
        System.out.println("--->>SUPPORTS －> 执行：T2，在最后抛出一个异常最后导致T1，T2全部回滚。");
        System.out.println("--->>SUPPORTS －> 结论：T1开启了一个事务，T2策略为跟随T1，但是因为T2异常回滚导致T1，T2全部回滚。");
        System.out.println("--->>SUPPORTS －> 结果：数据库没有数据。");
        System.out.println("--->>SUPPORTS －>  - 共计 0 条记录。");
        System.out.println();
        //
        try {
            TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
            temp.execute(new TransactionCallbackWithoutResult() {
                public void doTransactionWithoutResult(TransactionStatus tranStatus) throws Throwable {
                    System.out.println("begin T1!");
                    /*T1 - 默罕默德*/
                    insertUser_MHMD();
                    /*T2 - 安妮.贝隆、吴广*/
                    doTransactional();
                    /*T1 - 赵飞燕*/
                    insertUser_ZFY();
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //
        Thread.sleep(1000);
        printData();
    }
    @Test
    public void testNoneTransactional() throws Throwable {
        System.out.println("--->>SUPPORTS －> 前提：T1没有事务，T2跟随T1。");
        System.out.println("--->>SUPPORTS －> 执行：T2，在最后抛出一个异常，但是T1没有使用事务，T2跟随T1也没有使用事务。");
        System.out.println("--->>SUPPORTS －> 结论：虽然T2引发了一个异常打算回滚事务，但是因为跟随的T1本身就没有事务，因此4条数据全部录入。");
        System.out.println("--->>SUPPORTS －> 结果：数据库应存在：“默罕默德”、“安妮.贝隆”、“吴广”、“赵飞燕”");
        System.out.println("--->>SUPPORTS －>  - 共计 4 条记录。");
        System.out.println();
        //
        System.out.println("begin T1!");
        /*T1 - 默罕默德*/
        insertUser_MHMD();
        /*T2 - 安妮.贝隆、吴广*/
        try {
            doTransactional();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /*T1 - 赵飞燕*/
        insertUser_ZFY();
        //
        //
        Thread.sleep(1000);
        printData();
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.NESTED)
    public void doTransactional() throws Throwable {
        System.out.println("begin T2!");
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
        //
        throw new Exception("rollback T2.");
    }
}