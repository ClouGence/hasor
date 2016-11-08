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
package test.net.hasor.rsf.spring;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import test.net.hasor.rsf.services.EchoService;

import java.io.IOException;
/**
 * S
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringTest {
    @Test
    public void springConsumerTest() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/consumer-config.xml");
        EchoService echoService = (EchoService) applicationContext.getBean("echoService");
        for (int i = 0; i < 20; i++) {
            try {
                String res = echoService.sayHello("Hello Word for Invoker");
                System.out.println("invoker -> " + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void springProviderTest() throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/provider-config.xml");
        Object obj1 = applicationContext.getBean("echoService");
        System.out.println("@@@@@@@@@@@@@@" + obj1);
        //
        System.in.read();
    }
}