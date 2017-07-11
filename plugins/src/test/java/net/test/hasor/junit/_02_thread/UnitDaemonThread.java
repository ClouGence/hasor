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
package net.test.hasor.junit._02_thread;
import net.hasor.plugins.junit.DaemonThread;
import net.hasor.plugins.junit.HasorUnitRunner;
import net.hasor.plugins.junit.TestOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
@RunWith(HasorUnitRunner.class)
public class UnitDaemonThread {
    @DaemonThread
    public void daemonThread() throws Exception {
        /*后台监控线程，当Test启动的时候会启动该线程，当Test结束则线程结束。*/
        while (true) {
            System.out.println("daemonThread > " + this.hashCode());
            Thread.sleep(100);
        }
    }
    //
    @Before
    public void before() throws Exception {
        Thread.sleep(1000);
        System.out.println("before > " + this.hashCode());
    }
    //
    @Test()
    @TestOrder(0)
    public void hello1() throws Exception {
        Thread.sleep(1000);
        System.out.println("Hello1 > " + this.hashCode());
        Thread.sleep(1000);
    }
    @Test()
    @TestOrder(2)
    public void hello2() throws Exception {
        Thread.sleep(1000);
        System.out.println("Hello2 > " + this.hashCode());
        Thread.sleep(1000);
    }
}