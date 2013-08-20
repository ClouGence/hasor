/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package org.hasor.context.anno.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hasor.Hasor;
import org.hasor.context.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.context.anno.Before;
import org.hasor.context.matcher.AopMatchers;
import com.google.inject.matcher.Matcher;
/**
 * Ö§³ÖBean×¢½â¹¦ÄÜ¡£
 * @version : 2013-4-8
 * @author ÕÔÓÀ´º (zyc@hasor.net)
 */
class BeforeSupport {
    private GetContext getContext = null;
    //
    public BeforeSupport(ApiBinder apiBinder, GetContext getContext) {
        if (apiBinder.getInitContext().getSettings().getBoolean("hasor.annotation") == false) {
            Hasor.warning("init Annotation false!");
            return;
        }
        this.getContext = getContext;
        Matcher<Object> matcher = AopMatchers.annotatedWith(Before.class);//
        apiBinder.getGuiceBinder().bindInterceptor(matcher, matcher, new BeforeInterceptor());
    }
    /*-------------------------------------------------------------------------------------*/
    /*À¹½ØÆ÷*/
    private class BeforeInterceptor implements MethodInterceptor {
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
}