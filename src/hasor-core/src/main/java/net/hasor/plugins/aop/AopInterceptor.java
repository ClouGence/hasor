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
package net.hasor.plugins.aop;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.AppContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 拦截器
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
class AopInterceptor implements MethodInterceptor {
    private GetContext                                            getContext           = null;
    private Map<Method, List<Class<? extends MethodInterceptor>>> methodInterceptorMap = new HashMap<Method, List<Class<? extends MethodInterceptor>>>();
    //
    public AopInterceptor(GetContext getContext) {
        this.getContext = getContext;
    }
    //
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        List<Class<? extends MethodInterceptor>> list = this.methodInterceptorMap.get(targetMethod);
        //1.取得拦截器
        if (list == null) {
            list = new ArrayList<Class<? extends MethodInterceptor>>();
            Aop beforeAnno = targetMethod.getDeclaringClass().getAnnotation(Aop.class);
            if (beforeAnno != null) {
                for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                    if (interType != null)
                        list.add(interType);
            }
            beforeAnno = targetMethod.getAnnotation(Aop.class);
            if (beforeAnno != null) {
                for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                    if (interType != null)
                        list.add(interType);
            }
            this.methodInterceptorMap.put(targetMethod, list);
        }
        //2.创建对象
        AppContext appContext = getContext.getAppContext();
        return new AopChainInvocation(appContext, list, invocation).invoke(invocation);
    }
}