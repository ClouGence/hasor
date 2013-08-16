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
package org.hasor.test.bean;
import org.hasor.context.AppContext;
import org.hasor.test.AbstractTestContext;
import org.hasor.test.bean.beans.BaseTestBean;
import org.hasor.test.bean.beans.CustomerBean;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanTest extends AbstractTestContext {
    @Override
    protected void initContext(AppContext appContext) {}
    @Test
    public void createBean() throws Exception {
        /*获取Bean的三种方式*/
        BaseTestBean bean1 = this.getAppContext().getBean("AnnoA");
        BaseTestBean bean2 = this.getAppContext().getBean("BeanA");
        BaseTestBean bean3 = this.getAppContext().getInstance(BaseTestBean.class);
        CustomerBean custNean = this.getAppContext().getBean("Customer");//CustomerBean 是由CustomerBeanMod 注册的
        //
        bean1.foo();
        bean2.foo();
        bean3.foo();
        custNean.foo();
    }
}