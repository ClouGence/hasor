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
package org.hasor.test.simple.beans;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoAppContext;
import org.hasor.test.simple.beans.beans.NamesBean;
import org.hasor.test.simple.beans.beans.SingletonBean;
import org.hasor.test.simple.beans.customer.CustomerBean;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class Beans_Test {
    @Test
    public void testNamesBean() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testNamesBean<<--");
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/simple/beans/hasor-config.xml");
        appContext.start();
        //
        NamesBean b1 = appContext.getBean("name1");
        NamesBean b2 = appContext.getBean("name2");
        b1.foo();
        b2.foo();
    }
    @Test
    public void testSingletonBean() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testSingletonBean<<--");
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/simple/beans/hasor-config.xml");
        appContext.start();
        //
        SingletonBean b1 = appContext.getBean("singletonBean");
        SingletonBean b2 = appContext.getBean("singletonBean");
        b1.foo();
        b2.foo();
    }
    @Test
    public void testCustomerBean() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testCustomerBean<<--");
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/simple/beans/hasor-config.xml");
        appContext.start();
        //
        CustomerBean b1 = appContext.getBean("Customer");
        b1.foo();
    }
}