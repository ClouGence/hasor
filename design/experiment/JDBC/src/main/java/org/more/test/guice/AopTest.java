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
package org.more.test.guice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
/**
 * 
 * @version : 2013-11-22
 * @author 赵永春(zyc@hasor.net)
 */
public class AopTest {
    static class MyInterceptor implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("before.");
            Object returnData = invocation.proceed();
            System.out.println("after.");
            return returnData;
        }
    }
    static class MyModule implements Module {
        public void configure(Binder binder) {
            Matcher<Class> m = Matchers.inSubpackage("org.more.test");
            binder.bindInterceptor(m, Matchers.any(), new MyInterceptor());
        }
    }
    //
    public void foo() {
        System.out.println("Hello Word.");
    }
    //
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyModule());
        AopTest obj = injector.getInstance(AopTest.class);
        obj.foo();
    }
}