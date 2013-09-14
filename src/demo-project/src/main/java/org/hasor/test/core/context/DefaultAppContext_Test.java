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
package org.hasor.test.core.context;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.context.AnnoAppContext;
import net.hasor.core.context.DefaultAppContext;
import net.hasor.core.context.StandardAppContext;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultAppContext_Test {
    @Test
    public void testDefaultAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDefaultAppContext<<--");
        DefaultAppContext appContext = new DefaultAppContext();
        //
        appContext.addModule(new TestMode_2());
        appContext.addModule(new TestMode_3());
        appContext.addModule(new TestMode_1());
        //
        appContext.start();
    }
    @Test
    public void testStandardAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testStandardAppContext<<--");
        StandardAppContext appContext = new StandardAppContext("org/hasor/test/core/context/hasor-config.xml");
        //
        appContext.addModule(new TestMode_2());
        appContext.addModule(new TestMode_3());
        appContext.addModule(new TestMode_1());
        //
        appContext.start();
    }
    @Test
    public void testAnnoAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testAnnoAppContext<<--");
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/core/context/hasor-config.xml");
        //
        appContext.start();
    }
}
class TestMode_1 implements Module {
    public void init(ApiBinder apiBinder) {
        System.out.println("testMode_1");
        /*注册一个Integet对象，名称为theTime*/
        apiBinder.newBean("theTime").bindType(Integer.class).toInstance(456);
    }
    public void start(AppContext appContext) {
        System.out.println("start->testMode_1");
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}
class TestMode_2 implements Module {
    public void init(ApiBinder apiBinder) {
        apiBinder.moduleSettings().afterMe(TestMode_1.class);
        //
        System.out.println("testMode_2");
    }
    public void start(AppContext appContext) {
        System.out.println("start->testMode_2");
        /*获取，Module1注册的Integet对象，名称为theTime*/
        System.out.println(appContext.getBean("theTime"));
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}
class TestMode_3 implements Module {
    public void init(ApiBinder apiBinder) {
        apiBinder.moduleSettings().afterMe(TestMode_2.class);
        //
        System.out.println("testMode_3");
    }
    public void start(AppContext appContext) {
        System.out.println("start->testMode_2");
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}