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
import net.hasor.quick.anno.AnnoStandardAppContext;
import net.test.simple._03_aop.class_lv.ClassLv_FooBean;
import net.test.simple._03_aop.method_lv.MethodLv_FooBean;
import org.junit.Test;
/**
 * 测试 Aop
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestAop {
    /*方法级，拦截器测试*/
    @Test
    public void testMethodAop() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testMethodAop<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext();
        appContext.start();
        //
        MethodLv_FooBean fooBean = appContext.getInstance(MethodLv_FooBean.class);
        fooBean.fooCall();
    }
    /*类级别，拦截器测试*/
    @Test
    public void testClassAop() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testClassAop<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext();
        appContext.start();
        //
        ClassLv_FooBean fooBean = appContext.getInstance(ClassLv_FooBean.class);
        fooBean.fooCall();
    }
}
