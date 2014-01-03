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
package org.hasor.test.simple.aop.interceptor;
import net.hasor.plugins.aop.GlobalAop;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * ÃÌº”≥¨¡¥Ω”
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@GlobalAop("*")
public class AopInterceptor_A implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //System.out.println("before A");
        Object returnData = invocation.proceed();
        //System.out.println("after A");
        return "<a href='alert();'>" + returnData + "</a>";
    }
}