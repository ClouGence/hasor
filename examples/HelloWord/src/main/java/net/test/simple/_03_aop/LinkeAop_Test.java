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
package net.test.simple._03_aop;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoStandardAppContext;
import net.test.simple._03_aop.global_lv.AopBean_ClassLv;
import net.test.simple._03_aop.global_lv.AopBean_MethodLv;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class LinkeAop_Test {
    @Test
    public void testAopBeanClassLv() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testAopBeanClassLv<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/aop/aop-config.xml");
        appContext.start();
        //
        AopBean_ClassLv bean = appContext.getInstance(AopBean_ClassLv.class);
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooB("fooB"));
    }
    @Test
    public void testAopBeanMethodLv() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testAopBeanMethodLv<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/aop/aop-config.xml");
        appContext.start();
        //
        AopBean_MethodLv bean = appContext.getInstance(AopBean_MethodLv.class);
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooB("fooB"));
    }
    @Test
    public void testAopBeanGlobalLv() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testAopBeanGlobalLv<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/aop/aop-config.xml");
        appContext.start();
        //
        AopBean_MethodLv bean = appContext.getInstance(AopBean_MethodLv.class);
        System.out.println(bean.fooA("fooA"));
        System.out.println(bean.fooB("fooB"));
    }
}