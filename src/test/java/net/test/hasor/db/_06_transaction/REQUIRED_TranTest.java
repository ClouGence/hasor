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
import net.hasor.plugins.junit.ContextConfiguration;
import net.hasor.plugins.junit.HasorUnitRunner;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * REQUIRED：尝试加入已经存在的事务中，如果没有则开启一个新的事务。
 * @version : 2015年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class REQUIRED_TranTest extends AbstractNativesJDBCTest {
    @Test
    public void testHasTransactionalThrowT2() throws Throwable {
        System.out.println("--->>REQUIRED －> 前提：T1处于一个事务中，T2跟随T1。");
        System.out.println("--->>REQUIRED －> 执行：T2，在最后抛出一个异常，T1接住了这个异常，然后正常完成后续处理。");
        System.out.println("--->>REQUIRED －> 结论：T1开启了一个事务，并且处理了T2的异常。最总T1，T2数据全部生效。");
        System.out.println("--->>REQUIRED －> 结果：数据库应存在：“默罕默德”、“安妮.贝隆”、“吴广”、“赵飞燕”");
        System.out.println("--->>REQUIRED －>  - 共计 4 条记录。");
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
                    doTransactionalThrow();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                /*T1 - 赵飞燕*/
                insertUser_ZFY();
            }
        });
        //
        Thread.sleep(1000);
        printData();
    }
    @Test
    public void testHasTransactionalRollBackT1() throws Throwable {
        System.out.println("--->>REQUIRED －> 前提：T1处于一个事务中，T2跟随T1。");
        System.out.println("--->>REQUIRED －> 执行：T1在最后将事务回滚。");
        System.out.println("--->>REQUIRED －> 结论：因为T1，T2是同一个事务，最后T1吧事务回滚导致了，T1，T2数据都没有生效。");
        System.out.println("--->>REQUIRED －> 结果：数据库没有数据。");
        System.out.println("--->>REQUIRED －>  - 共计 0 条记录。");
        System.out.println();
        //
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
                //
                tranStatus.setRollbackOnly();
                System.out.println("rollback T1!");
            }
        });
        //
        Thread.sleep(1000);
        printData();
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.REQUIRED)
    public void doTransactionalThrow() throws Throwable {
        System.out.println("begin T2!");
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
        //
        throw new Exception("rollback T2.");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void doTransactional() throws Throwable {
        System.out.println("begin T2!");
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
        //
        System.out.println("commit T2.");
    }
}