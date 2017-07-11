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
package net.hasor.core.exts.aop;
import net.hasor.core.AppContext;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class AopChainInvocation implements MethodInvocation {
    private MethodInterceptor[] beforeInterceptor = null;
    private MethodInvocation    invocation        = null;
    private int                 index             = -1;
    //
    public AopChainInvocation(AppContext appContext, List<Class<? extends MethodInterceptor>> interTypeList, MethodInvocation invocation) {
        List<MethodInterceptor> beforeList = new ArrayList<MethodInterceptor>();
        for (Class<? extends MethodInterceptor> interType : interTypeList) {
            if (interType != null) {
                beforeList.add(appContext.getInstance(interType));
            }
        }
        this.beforeInterceptor = beforeList.toArray(new MethodInterceptor[beforeList.size()]);
        this.invocation = invocation;
    }
    public Object invoke(MethodInvocation invocation) throws Throwable {
        index++;
        if (index < beforeInterceptor.length) {
            return beforeInterceptor[index].invoke(this);
        } else {
            return invocation.proceed();
        }
    }
    //-----------------------------------------------------------
    public Object[] getArguments() {
        return invocation.getArguments();
    }
    public Object proceed() throws Throwable {
        return this.invoke(this.invocation);
    }
    public Object getThis() {
        return invocation.getThis();
    }
    public Method getMethod() {
        return invocation.getMethod();
    }
}