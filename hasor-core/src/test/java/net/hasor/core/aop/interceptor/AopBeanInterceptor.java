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
package net.hasor.core.aop.interceptor;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.util.List;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AopBeanInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        invocation.getMethod();
        invocation.getArguments();
        invocation.getThis();
        //
        if (!invocation.getMethod().getName().equalsIgnoreCase("doInit")) {
            return invocation.proceed();
        }
        //
        try {
            ((List<String>) invocation.getArguments()[0]).add("BEFORE");
            return invocation.proceed();
        } finally {
            ((List<String>) invocation.getArguments()[0]).add("AFTER");
        }
    }
}