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

import java.util.ArrayList;
import java.util.List;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class CheckBaseType0Interceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        List<?> proceed = (List<?>) invocation.proceed();
        ArrayList<Object> result = new ArrayList<Object>();
        result.add("Before");
        result.addAll(proceed);
        result.add("After");
        return result;
    }
}