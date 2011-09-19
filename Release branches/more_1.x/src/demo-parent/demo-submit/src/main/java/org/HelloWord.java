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
package org;
import org.junit.Test;
import org.more.hypha.context.app.ClassPathApplicationContext;
import org.more.services.submit.SubmitService;
/**
 * 
 * Date : 2009-12-11
 * @author Administrator
 */
public class HelloWord {
    @Test
    public void main() throws Throwable {
        ClassPathApplicationContext context = new ClassPathApplicationContext("more-config.xml");
        context.init();
        //
        SubmitService submit = context.getService(SubmitService.class);//获得生成的SubmitContext对象。
        //=================================================================调用XML配置文件中的Action配置。
        System.out.println(submit.getActionObject("custom://org.custom.CustomBean.testCustom").doAction());
        System.out.println(submit.getActionObject("custom://hi_0").doAction());
        //
        System.out.println(submit.getActionObject("guice://org.guice.GuiceBean.testGuice").doAction());
        System.out.println(submit.getActionObject("guice://hi_1").doAction());
        //
        System.out.println(submit.getActionObject("hypha://org.hypha.AnnoHyphaBean.testHypha").doAction());
        System.out.println(submit.getActionObject("hypha://hi_2").doAction());
        System.out.println(submit.getActionObject("hypha://org.hypha.HyphaBean.testHypha").doAction());
        System.out.println(submit.getActionObject("hypha://hi_3").doAction());
        //
        System.out.println(submit.getActionObject("simple://org.simple.SimpleBean.testSimple").doAction());
        System.out.println(submit.getActionObject("simple://hi_4").doAction());
        //
        System.out.println(submit.getActionObject("spring://org.spring.AnnoSpringBean.testSpring").doAction());
        System.out.println(submit.getActionObject("spring://hi_5").doAction());
        System.out.println(submit.getActionObject("spring://org.spring.SpringBean.testSpring").doAction());
        System.out.println(submit.getActionObject("spring://hi_6").doAction());
        //
    }
}