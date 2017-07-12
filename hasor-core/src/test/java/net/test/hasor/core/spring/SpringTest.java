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
package net.test.hasor.core.spring;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * S
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringTest {
    private ApplicationContext applicationContext;
    @Before
    public void initSpring() {
        this.applicationContext = new ClassPathXmlApplicationContext("/spring/spring-hasor.xml");
    }
    //
    //
    @Test
    public void springTest() {
        Object obj1 = this.applicationContext.getBean("springBean");
        System.out.println("@@@@@@@@@@@@@@" + obj1);
        //
        Object obj2 = this.applicationContext.getBean("helloString");
        System.out.println("@@@@@@@@@@@@@@" + obj2);
        //
        Object obj3 = this.applicationContext.getBean("hasorBean");
        System.out.println("@@@@@@@@@@@@@@" + obj3);
    }
    @Test
    public void hasorTest() {
        //        AppContext appContext = Hasor.createAppContext(new SpringModule(this.applicationContext) {
        //            protected boolean isExportBean(String beanName) {
        //                return true;
        //            }
        //        });
        //        //
        //        HasorBean hasorBean = appContext.getInstance(HasorBean.class);
        //        System.out.println("@@@@@@@@@@@@@@" + hasorBean);
    }
}