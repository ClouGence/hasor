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
package org.test.more.hypha.xml;
import org.junit.Test;
import org.more.hypha.aop.AopService;
import org.more.hypha.context.app.DefaultApplicationContext;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * @version 2010-10-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlTest {
    @Test
    public void test() throws Throwable {
        test_0();
    }
    public void test_0() throws Throwable {
        System.out.println("start...");
        XmlDefineResource xdr = new XmlDefineResource();
        //
        xdr.addSource("org/test/more/hypha/xml/class-beans-test-config.xml");
        xdr.addSource("/org/test/more/hypha/xml/ref-beans-test-config.xml");
        xdr.addSource("/org/test/more/hypha/xml/template-beans-test-config.xml");
        //xdr.addSource("/org/test/more/hypha/xml/script-beans-test-config.xml");
        xdr.addSource("/org/test/more/hypha/xml/var-beans-test-config.xml");
        xdr.addSource("/org/test/more/hypha/xml/aop-test-config.xml");
        xdr.addSource("/org/test/more/hypha/xml/anno-test-config.xml");
        xdr.loadDefine();
        //
        DefaultApplicationContext app = new DefaultApplicationContext(xdr);
        app.init();
        System.out.println("----------------------------------");
        //
        for (String n : app.getBeanDefinitionIDs()) {
            System.out.println("\t" + n);
            if (app.getBeanDefinition(n).isAbstract() == false) {
                Object obj = app.getBean(n);
                //if (obj != null)
                //System.out.print(obj.getClass() + "\t");
                //System.out.println(obj);
                // System.out.println("***********************************");
            }
        }
        //
        //
        AopService aopConfig = (AopService) app.getService(AopService.class);
        System.out.println(aopConfig);
        //
        System.gc();
    }
}