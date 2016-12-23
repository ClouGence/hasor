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
 * 
 * @version : 2015年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "jdbc-config.xml", loadModules = SingleDataSourceWarp.class)
public class EmptyTranTest extends AbstractNativesJDBCTest {
    @Test
    public void emptyTestHasTransactional() throws Throwable {
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute(new TransactionCallbackWithoutResult() {
            public void doTransactionWithoutResult(TransactionStatus tranStatus) throws Throwable {
                System.out.println("begin T1!");
                doTransactional();
                System.out.println("commit T2!");
            }
        });
    }
    @Test
    public void emptyTestNoneTransactional() throws Throwable {
        doTransactional();
    }
    //
    //
    //
    //
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void doTransactional() throws Throwable {
        System.out.println("begin T2!");
        //
        System.out.println("commit T2!");
    }
}