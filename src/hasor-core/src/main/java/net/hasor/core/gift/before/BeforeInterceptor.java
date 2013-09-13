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
package net.hasor.core.gift.before;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * À¹½ØÆ÷
 * @version : 2013-9-13
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
class BeforeInterceptor implements MethodInterceptor {
    private GetContext getContext = null;
    //
    public BeforeInterceptor(GetContext getContext) {
        this.getContext = getContext;
    }
    //
    public Object invoke(MethodInvocation invocation) throws Throwable {
        List<Class<? extends MethodInterceptor>> list = new ArrayList<Class<? extends MethodInterceptor>>();
        Method targetMethod = invocation.getMethod();
        //
        Before beforeAnno = targetMethod.getDeclaringClass().getAnnotation(Before.class);
        if (beforeAnno != null) {
            for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                if (interType != null)
                    list.add(interType);
        }
        beforeAnno = targetMethod.getAnnotation(Before.class);
        if (beforeAnno != null) {
            for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                if (interType != null)
                    list.add(interType);
        }
        //2.»ñÈ¡À¹½ØÆ÷
        AppContext appContext = getContext.getAppContext();
        return new BeforeChainInvocation(appContext, list, invocation).invoke(invocation);
    }
}