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
package net.test.hasor.core._02_ioc;
import com.alibaba.fastjson.JSON;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.core._01_bean.pojo.PojoBean;
import net.test.hasor.core._01_bean.pojo.PojoInfo;
import net.test.hasor.core._02_ioc.example.AnnoIocBean;
import net.test.hasor.core._02_ioc.example.CustomIocBean;
import net.test.hasor.core._02_ioc.example.NameIocBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 编码方式注入：
 *
 * 1.annoNameIocTest
 *      当同一个类型声明了不同实例时候，你需要通过名称注入你想要的。
 * 2.annoIocTest
 *      一般的注解用法。
 *
 * @version : 2015年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class AnnoIocTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    /* 当同一个类型声明了不同实例时候，你需要通过名称注入你想要的*/
    @Test
    public void annoIocTest() {
        System.out.println("--->>annoNameIocTest<<--");
        AppContext appContext = Hasor.createAppContext("simple-config.xml", new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(PojoInfo.class).idWith("beanA").to(PojoBean.class).injectValue("name", "娇娇");
                apiBinder.bindType(PojoInfo.class).idWith("beanB").to(PojoBean.class).injectValue("name", "花花");
            }
        });
        logger.debug("---------------------------------------------");
        //
        NameIocBean myBean1 = appContext.getInstance(NameIocBean.class);
        logger.debug("myBean : " + JSON.toJSONString(myBean1));
        assert myBean1.getIocBeanA().getName().equals("娇娇");
        assert myBean1.getIocBeanB().getName().equals("花花");
        //
        CustomIocBean myBean2 = appContext.getInstance(CustomIocBean.class);
        logger.debug("myBean : " + JSON.toJSONString(myBean2));
        assert myBean2.iocBeanTest == null;
        assert myBean2.iocBean != null;
        //
        AnnoIocBean myBean3 = appContext.getInstance(AnnoIocBean.class);
        logger.debug("myBean : " + JSON.toJSONString(myBean3));
        assert myBean3.getMyName().equals("赵永春"); // <- 来源于 simple-config.xml
    }
}