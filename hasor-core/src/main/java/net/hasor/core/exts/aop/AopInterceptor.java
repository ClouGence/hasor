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
import net.hasor.core.spi.AppContextAware;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Aop拦截器
 * @version : 2013-11-8
 * @author 赵永春 (zyc@hasor.net)
 */
class AopInterceptor implements MethodInterceptor, AppContextAware {
    private WeakHashMap<Method, List<Class<? extends MethodInterceptor>>> methodInterceptorMap = new WeakHashMap<Method, List<Class<? extends MethodInterceptor>>>();
    private AppContext                                                    appContext           = null;
    //
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    //
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        List<Class<? extends MethodInterceptor>> list = this.methodInterceptorMap.get(targetMethod);
        //1.取得拦截器链
        if (list == null) {
            list = new ArrayList<>();
            //b.类级拦截器
            list.addAll(collectAnno(targetMethod.getDeclaringClass()));
            //c.方法级拦截器
            list.addAll(collectAnno(targetMethod));
            //d.缓存结果
            this.methodInterceptorMap.put(targetMethod, list);
        }
        //2.创建对象
        return new AopChainInvocation(appContext, list, invocation).invoke(invocation);
    }
    private List<Class<? extends MethodInterceptor>> collectAnno(AnnotatedElement element) {
        Aop[] annoSet = element.getAnnotationsByType(Aop.class);
        if (annoSet == null) {
            annoSet = new Aop[0];
        }
        return Arrays.stream(annoSet).flatMap(//
                (Function<Aop, Stream<Class<? extends MethodInterceptor>>>) aop -> Arrays.stream(aop.value())//
        ).collect(Collectors.toList());
    }
}