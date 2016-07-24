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
package net.test.hasor.spring;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.plugins.spring.SpringModule;
import net.test.hasor.spring.bean.HasorBean;
import net.test.hasor.spring.bean.SpringBean;
import net.test.hasor.spring.event.tohasor.SpringEventPublisher;
import net.test.hasor.spring.event.tospring.HasorEventPublisher;
/**
 * 
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringTest {
    private ApplicationContext applicationContext;
    @Before
    public void initSpring() {
        this.applicationContext = new ClassPathXmlApplicationContext("spring/spring-bean.xml");
    }
    //
    //
    @Test
    public void springPublisherEvent() {
        SpringEventPublisher springPublisher = (SpringEventPublisher) applicationContext.getBean("springEventPublisher");
        //
        springPublisher.publishSyncEvent();//通过Spring发送事件给Hasor
    }
    @Test
    public void hasorPublisherEvent() {
        AppContext appContext = (AppContext) applicationContext.getBean("hasor");
        //
        HasorEventPublisher hasorPublisher = appContext.getInstance(HasorEventPublisher.class);
        hasorPublisher.publishEvent();//通过Hasor发送事件给Spring
    }
    @Test
    public void springTest() {
        SpringBean obj1 = (SpringBean) this.applicationContext.getBean("springBean");
        System.out.println("@@@@@@@@@@@@@@" + obj1);
        //
        Object obj2 = this.applicationContext.getBean("helloString");
        System.out.println("@@@@@@@@@@@@@@" + obj2);
    }
    @Test
    public void hasorTest() {
        AppContext appContext = Hasor.createAppContext(new SpringModule(this.applicationContext) {
            protected boolean isExportBean(String beanName) {
                return true;
            }
        });
        //
        HasorBean hasorBean = appContext.getInstance(HasorBean.class);
        System.out.println("@@@@@@@@@@@@@@" + hasorBean);
    }
}