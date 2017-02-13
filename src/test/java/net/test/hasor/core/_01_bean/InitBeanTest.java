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
package net.test.hasor.core._01_bean;
import com.alibaba.fastjson.JSON;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.core._01_bean.pojo.InitBean;
import net.test.hasor.core._01_bean.pojo.InitBean2;
import net.test.hasor.core._01_bean.pojo.StartInitBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 1.initTest
 *      当容器启动自动调用单例Bean的init方法。
 *
 * @version : 2015年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class InitBeanTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    //
    /* @init 注解  */
    @Test
    public void initBeanTest() {
        System.out.println("--->>initBeanTest<<--");
        AppContext appContext = Hasor.createAppContext();
        logger.debug("---------------------------------------------");
        //
        InitBean myBean1 = appContext.getInstance(InitBean.class);
        logger.debug(JSON.toJSONString(myBean1));
        assert myBean1.called;
        //
        InitBean2 myBean2 = appContext.getInstance(InitBean2.class);
        logger.debug(JSON.toJSONString(myBean2));
        assert myBean2.called;
    }
    /* Bean */
    @Test
    public void initTest() {
        System.out.println("--->>beanTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(StartInitBean.class);
            }
        });
        logger.debug("---------------------------------------------");
        //
        assert StartInitBean.called;
        StartInitBean.called = false;
    }
}