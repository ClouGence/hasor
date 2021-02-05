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
package net.hasor.test.core.aop.anno;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassAnnoInterceptor implements MethodInterceptor {
    private final Map<String, List<String>> callInfo = new HashMap<>();

    public Map<String, List<String>> getCallInfo() {
        return callInfo;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        invocation.getMethod();
        invocation.getArguments();
        invocation.getThis();
        //
        String methodName = invocation.getMethod().getName();
        List<String> stringList = callInfo.computeIfAbsent(methodName, k -> new ArrayList<>());
        //
        try {
            stringList.add("BEFORE");
            Object proceed = invocation.proceed();
            stringList.add("AFTER");
            return proceed;
        } catch (Exception e) {
            stringList.add("THROW");
            throw e;
        }
    }
}
