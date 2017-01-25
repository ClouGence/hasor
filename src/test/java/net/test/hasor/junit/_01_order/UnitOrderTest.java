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
package net.test.hasor.junit._01_order;
import net.hasor.plugins.junit.HasorUnitRunner;
import net.hasor.plugins.junit.TestOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
@RunWith(HasorUnitRunner.class)
public class UnitOrderTest {
    //
    @Test()
    @TestOrder(0)
    public void hello_0() throws Exception {
        System.out.println("Hello 0 > " + this.hashCode());
    }
    @Test()
    @TestOrder(2)
    public void hello_2() throws Exception {
        System.out.println("Hello 2 > " + this.hashCode());
    }
    @Test()
    @TestOrder(1)
    public void hello_1() throws Exception {
        System.out.println("Hello 1 > " + this.hashCode());
    }
}