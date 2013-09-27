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
package net.hasor.web.controller.interceptor;
import net.hasor.web.controller.AbstractController;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public abstract class ControllerInterceptor implements MethodInterceptor {
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        Object controllerObject = invocation.getThis();
        if (controllerObject instanceof AbstractController == false)
            return invocation.proceed();
        //
        AbstractController controller = (AbstractController) controllerObject;
        return this.invoke(new ControllerInvocation(controller, invocation));
    }
    public abstract Object invoke(ControllerInvocation invocation) throws Throwable;
}