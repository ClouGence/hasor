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
package net.test.hasor.core._02_ioc;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.core._01_bean.pojo.PojoBean;
import net.test.hasor.core._01_bean.pojo.PojoInfo;
import net.test.hasor.core._02_ioc.pojo.AnnoIocBean;
import net.test.hasor.core._02_ioc.pojo.NameAnnoIocBean;
import org.junit.Test;
import org.more.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注解方式注入：
 * 
 * 1.annoTypeIocTest
 *      注解方式注入。
 * 2.annoNameIocTest
 *      当同一个类型声明了不同实例时候，你需要通过名称注入你想要的。
 * 
 * @version : 2015年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class AnnoIocTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    /* 注解方式注入 */
    @Test
    public void annoTypeIocTest() {
        System.out.println("--->>annoTypeIocTest<<--");
        AppContext appContext = Hasor.createAppContext();
        //
        AnnoIocBean myBean = appContext.getInstance(AnnoIocBean.class);
        logger.debug(JSON.toString(myBean));
    }
    //
    /* 当同一个类型声明了不同实例时候，你需要通过名称注入你想要的*/
    @Test
    public void annoNameIocTest() {
        System.out.println("--->>annoNameIocTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(PojoInfo.class).nameWith("beanA").to(PojoBean.class).injectValue("name", "娇娇");
                apiBinder.bindType(PojoInfo.class).nameWith("beanB").to(PojoBean.class).injectValue("name", "花花");
            }
        });
        //
        NameAnnoIocBean myBean = appContext.getInstance(NameAnnoIocBean.class);
        logger.debug(JSON.toString(myBean));
    }
}