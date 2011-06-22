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
package org.test.more.beans.xml;
import org.junit.Test;
import org.more.hypha.context.app.HyphaApplicationContext;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * @version 2010-10-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlTest {
    @Test
    public void test() {
        try {
            test_0();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public void test_0() throws Throwable {
        System.out.println("start...");
        XmlDefineResource xdr = new XmlDefineResource();
        xdr.addSource("org/test/more/beans/xml/class-beans-test-config.xml");
        //        xdr.addSource("/org/test/more/beans/xml/ref-beans-test-config.xml");
        //        xdr.addSource("/org/test/more/beans/xml/script-beans-test-config.xml");
        //        xdr.addSource("/org/test/more/beans/xml/template-beans-test-config.xml");
        //        xdr.addSource("/org/test/more/beans/xml/var-beans-test-config.xml");
        xdr.loadDefine();
        //
        HyphaApplicationContext app = new HyphaApplicationContext(xdr);
        app.init();
        System.out.println("----------------------------------");
        //
        //
        //
//        for (String n : app.getBeanDefinitionIDs())
//            if (app.getBeanDefinition(n).isAbstract() == false) {
//                System.out.println("\t" + n);
//                app.getBean(n);
//                System.out.println("***********************************");
//            }
        app.getBean("c_bean_10");
        //
        System.gc();
    }
}