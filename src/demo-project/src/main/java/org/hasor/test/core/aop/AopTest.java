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
package org.hasor.test.core.aop;
import net.hasor.core.AppContext;
import org.hasor.test.AbstractTestContext;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AopTest extends AbstractTestContext {
    protected void initContext(AppContext appContext) {}
    @Test
    public void aopTest() {
        /*获取Bean的三种方式*/
        AopBean bean = this.getAppContext().getInstance(AopBean.class);
        System.out.println(bean.fooA("p1"));
        System.out.println(bean.fooB("p2"));
    }
}