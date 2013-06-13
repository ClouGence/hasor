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
package org.platform.jmx;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
/**
 * 
 * @version : 2013-5-7
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //        HtmlAdaptorServer s;
        //        MBeanServer server = MBeanServerFactory.createMBeanServer();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        TestApi mxBean = new TestApiMBeanImpl();
        server.registerMBean(mxBean, new ObjectName("serve:id=Server"));
        System.in.read();
    }
}
