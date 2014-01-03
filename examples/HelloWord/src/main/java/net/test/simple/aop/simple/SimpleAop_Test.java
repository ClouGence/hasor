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
package net.test.simple.aop.simple;
import java.io.IOException;
import net.hasor.core.AppContext;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.plugins.aop.Aop;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class SimpleAop_Test {
    @Aop(MyInterceptor.class)
    public void foo1() {
        System.out.println("Hello Word.");
    }
    public void foo2() {
        System.out.println("Hello Word.");
    }
    //
    static class MyInterceptor implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("before.");
            Object returnData = invocation.proceed();
            System.out.println("after.");
            return returnData;
        }
    }
    public static void main(String[] args) throws IOException {
        AppContext appContext = new AnnoStandardAppContext();
        appContext.start();
        //
        SimpleAop_Test obj = appContext.getInstance(SimpleAop_Test.class);
        obj.foo1();
        obj.foo2();
    }
}