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
package net.test.hasor.core._03_aop.simple;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
/**
 * 一个 Aop 拦截器
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class SimpleInterceptor implements MethodInterceptor {
    public static boolean called = false;
    public Object invoke(MethodInvocation invocation) throws Throwable {
        called = true;
        try {
            System.out.println("before... " + invocation.getMethod().getName());
            Object returnData = invocation.proceed();
            System.out.println("after...");
            return returnData;
        } catch (Exception e) {
            System.out.println("throw...");
            throw e;
        }
    }
}