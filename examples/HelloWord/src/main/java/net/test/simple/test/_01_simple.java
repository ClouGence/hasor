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
package net.test.simple.test;
import net.hasor.test.junit.DaemonThread;
import net.hasor.test.junit.TestOrder;
import net.hasor.test.runner.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
@RunWith(HasorUnitRunner.class)
public class _01_simple {
    @DaemonThread
    public void daemonThread() {
        System.out.println("daemonThread");
    }
    //
    @Test()
    @TestOrder(0)
    public void hello1() {
        System.out.println("Hello1");
    }
    @Test()
    @TestOrder(2)
    public void hello2() {
        System.out.println("Hello2");
    }
}