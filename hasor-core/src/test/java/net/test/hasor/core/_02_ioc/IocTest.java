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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.hasor.core.pojos.CustomBean2;
import net.test.hasor.core.pojos.InitBean;
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
 * @author 赵永春 (zyc@hasor.net)
 */
public class IocTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    /* 当同一个类型声明了不同实例时候，你需要通过名称注入你想要的*/
    @Test
    public void annoIocTest() throws NoSuchMethodException {
        System.out.println("--->>annoNameIocTest<<--");
        AppContext appContext = Hasor.createAppContext();
        CustomBean2 instance = appContext.getInstance(CustomBean2.class.getConstructor(InitBean.class));
        logger.debug("---------------------------------------------");
        //
    }
}