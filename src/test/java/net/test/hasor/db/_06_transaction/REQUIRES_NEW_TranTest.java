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
import org.junit.Test;
import org.junit.runner.RunWith;
import net.hasor.db.Transactional;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import net.hasor.junit.ContextConfiguration;
import net.hasor.junit.HasorUnitRunner;
/**
 * REQUIRES_NEW：将挂起当前存在的事务挂起（如果存在的话）。 并且开启一个全新的事务，新事务与已存在的事务之间彼此没有关系。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class REQUIRES_NEW_TranTest extends AbstractNativesJDBCTest {
    @Test
    public void testHasTransactionalRollBackT1() throws Throwable {
        System.out.println("--->>REQUIRES_NEW －> 前提：T1处于一个事务中，T2拥有自己的独立事务。");
        System.out.println("--->>REQUIRES_NEW －> 执行：T1，在执行完T2，之后将T1自身进行了回滚。");
        System.out.println("--->>REQUIRES_NEW －> 结论：因为T1，T2彼此都是独立事务，互不影响，因此T1虽然作为父事务但是并没有影响到T2。");
        System.out.println("--->>REQUIRES_NEW －> 结果：数据库应存在：“安妮.贝隆”、“吴广”");
        System.out.println("--->>REQUIRES_NEW －>  - 共计 2 条记录。");
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
                    //
                    tranStatus.setRollbackOnly();
                    System.out.println("rollback T1!");
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
    public void testHasTransactionalRollBackT2() throws Throwable {
        System.out.println("--->>REQUIRES_NEW －> 前提：T1处于一个事务中，T2开启一个子事务。");
        System.out.println("--->>REQUIRES_NEW －> 执行：T2在执行完毕之后，通知监控线程打印数据库记录。2条数据“安妮.贝隆”、“吴广”");
        System.out.println("--->>REQUIRES_NEW －> 结论：因为T1、分别位于自己独立的事务中。在T2递交了之后监控线程自然可以查询到。");
        System.out.println("--->>REQUIRES_NEW －> 结果：T1在递交之前的数据查询有2条记录，之后则有 4 条。");
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
                Thread.sleep(1000);
                System.out.println();
                System.out.println();
                System.out.print("触发一次监控线程的查询.");
                printData();
                System.out.println("commit T1!");
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
