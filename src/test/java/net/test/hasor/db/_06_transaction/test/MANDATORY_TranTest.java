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
package net.test.hasor.db._06_transaction.test;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.hasor.db.Transactional;
import net.hasor.db.transaction.Propagation;
import net.test.hasor.db._06_transaction.AbstractNativesJDBCTest;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.junit.ContextConfiguration;
import net.test.hasor.junit.HasorUnitRunner;
/**
* MANDATORY：要求环境中存在事物，如果不存在就抛异常
* @version : 2013-12-10
* @author 赵永春(zyc@hasor.net)
*/
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class MANDATORY_TranTest extends AbstractNativesJDBCTest {
    @Test
    @Transactional /*该注解保证了测试方法的执行是在事物中*/
    public void testHasTransactional() throws Throwable {
        System.out.println("--->>MANDATORY －> 测试条件，环境中存在事物。");
        System.out.println("--->>MANDATORY －>     数据库应存在：“默罕默德”、“安妮.贝隆”、“吴广”、“赵飞燕”");
        System.out.println("--->>MANDATORY －>     共计 4 条记录。");
        System.out.println();
        //
        System.out.println("begin T1!");
        /*T1 - 默罕默德*/
        this.insertUser_MHMD();
        /*T2 - 安妮.贝隆、吴广*/
        try {
            System.out.println("begin T2!");
            doTransactional();
            System.out.println("commit T2!");
        } catch (Exception e) {
            System.out.println("rollback T2! message = " + e.getMessage());
        } finally {
            printData();
            Thread.sleep(1000);
        }
        /*T1 - 赵飞燕*/
        insertUser_ZFY();
        System.out.println("commit T1!");
    }
    @Test
    public void testNoneTransactional() throws Throwable {
        System.out.println("--->>MANDATORY －> 测试条件，环境不存在事物。");
        System.out.println("--->>MANDATORY －>     数据库应存在：“默罕默德”、“赵飞燕”");
        System.out.println("--->>MANDATORY －>     共计 2 条记录。");
        System.out.println();
        //
        System.out.println("begin T1!");
        /*T1 - 默罕默德*/
        this.insertUser_MHMD();
        /*T2 - 安妮.贝隆、吴广*/
        try {
            System.out.println("begin T2!");
            doTransactional();
            System.out.println("commit T2!");
        } catch (Exception e) {
            System.out.println("rollback T2! message = " + e.getMessage());
        } finally {
            printData();
            Thread.sleep(1000);
        }
        /*T1 - 赵飞燕*/
        insertUser_ZFY();
        System.out.println("commit T1!");
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.MANDATORY)
    public void doTransactional() throws Throwable {
        System.out.println("--->>MANDATORY －> 测试条件，如果看到该日志代表环境中存在事物。<<--");
        //
        /*安妮.贝隆*/
        insertUser_ANBL();
        /*吴广*/
        insertUser_WG();
    }
}