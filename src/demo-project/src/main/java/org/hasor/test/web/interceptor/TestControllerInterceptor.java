/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.test.web.interceptor;
import net.hasor.web.controller.interceptor.ControllerInterceptor;
import net.hasor.web.controller.interceptor.ControllerInvocation;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class TestControllerInterceptor extends ControllerInterceptor {
    public Object invoke(ControllerInvocation invocation) throws Throwable {
        try {
            System.out.println("before Controller");
            return invocation.proceed();
        } catch (Exception e) {
            System.out.println("error Controller");
            throw e;
        } finally {
            System.out.println("after Controller");
        }
    }
}