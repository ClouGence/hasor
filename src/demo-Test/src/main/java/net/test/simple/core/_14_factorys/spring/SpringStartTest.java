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
package net.test.simple.core._14_factorys.spring;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.context.factorys.spring.SpringRegisterFactoryCreater;
import net.test.simple.core._03_beans.pojo.PojoBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
/**
 * 本示列演示如何启动 Hasor 框架。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpringStartTest {
    @Test
    public void springStartTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>springStartTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new SpringRegisterFactoryCreater(), new Module() {
            @Override
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                PojoBean pojo = new PojoBean();
                pojo.setName("马大帅");
                apiBinder.bindType(PojoBean.class).nameWith("myBean").toInstance(pojo);
                //
                apiBinder.defineBean("define").bindType(PojoBean.class).toInstance(pojo);
            }
        });
        //
        ApplicationContext spring = appContext.getInstance(ApplicationContext.class);
        PojoBean pbean = (PojoBean) spring.getBean("myBean");
        System.out.println(pbean.getName());
        //
        PojoBean define = (PojoBean) spring.getBean("define");
        System.out.println(define.getName());
    }
}