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
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.hasor.db.Transactional;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import net.test.hasor.junit.ContextConfiguration;
import net.test.hasor.junit.HasorUnit;
import net.test.hasor.junit.HasorUnitRunner;
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
        System.out.println("--->>SUPPORTS －> 前提：T1处于一个事务中，T2开启一个子事务。");
        System.out.println("--->>SUPPORTS －> 执行：T2在执行完毕之后，通知监控线程打印数据库记录。结果无任何输出。");
        System.out.println("--->>SUPPORTS －> 结论：因为T1、T2位于一个Connection，虽然T2递交了事务，但是主事务T1还未递交因此监控线程无法看到递交的数据。");
        System.out.println("--->>SUPPORTS －>      在T2执行完毕之后，由T1发起一次数据查询操作，得到了已经插入的三条数据。");
        System.out.println("--->>SUPPORTS －> 结果：数据库没有数据。");
        System.out.println("--->>SUPPORTS －>  - 共计 0 条记录。");
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
                String selectSQL = "select * from TB_User";
                JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(selectSQL);
                System.out.print("从T1中查询的数据：");
                HasorUnit.printMapList(dataList);
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
    @Test
    public void testNoneTransactional() throws Throwable {
        s
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
        System.out.println("commit T2!");
    }
}