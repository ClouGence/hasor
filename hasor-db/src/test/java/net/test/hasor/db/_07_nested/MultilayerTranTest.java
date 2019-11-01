///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.test.hasor.db._07_nested;
//import net.hasor.db.transaction.Propagation;
//import net.hasor.db.transaction.TransactionManager;
//import net.hasor.db.transaction.TransactionStatus;
//import net.test.hasor.db._06_transaction.AbstractNativesJDBCTest;
//import org.junit.Test;
//
///**
// * 多层嵌套事务，只递交其中一层的容错。
// * @version : 2015年11月17日
// * @author 赵永春(zyc @ hasor.net)
// */
////@RunWith(HasorUnitRunner.class)
////@ContextConfiguration(value = "jdbc-config.properties", loadModules = SingleDataSourceWarp.class)
//public class MultilayerTranTest extends AbstractNativesJDBCTest {
//    @Test
//    public void abc() {
//
//    }
//    public void testHasTransactional() throws Throwable {
//        //
//        TransactionManager temp = appContext.getInstance(TransactionManager.class);
//        //
//        TransactionStatus t1 = temp.getTransaction(Propagation.NESTED);
//        TransactionStatus t2 = temp.getTransaction(Propagation.NESTED);
//        TransactionStatus t3 = temp.getTransaction(Propagation.NESTED);
//        TransactionStatus t4 = temp.getTransaction(Propagation.NESTED);
//        TransactionStatus t5 = temp.getTransaction(Propagation.NESTED);
//        TransactionStatus t6 = temp.getTransaction(Propagation.NESTED);
//        //
//        temp.commit(t1);
//        //
//        Thread.sleep(1000);
//    }
//}