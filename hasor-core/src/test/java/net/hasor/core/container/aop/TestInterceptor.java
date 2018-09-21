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
package net.hasor.core.container.aop;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
/**
 * 一个 Aop 拦截器
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestInterceptor implements MethodInterceptor {
    private static boolean called  = false;
    private static boolean throwed = false;
    //
    public static boolean isCalled() {
        return called;
    }
    public static boolean isThrowed() {
        return throwed;
    }
    public static void resetInit() {
        called = false;
        throwed = false;
    }
    //
    public Object invoke(MethodInvocation invocation) throws Throwable {
        called = true;
        try {
            return invocation.proceed();
        } catch (Exception e) {
            throwed = true;
            throw e;
        }
    }
}