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
package net.hasor.gift.result;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import net.hasor.controller.interceptor.ControllerInterceptor;
import net.hasor.controller.interceptor.ControllerInvocation;
/**
 * 
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ResultCaller extends ControllerInterceptor {
    private GetContext                          context   = null;
    private Map<Class<?>, Class<ResultProcess>> defineMap = null;
    //
    public ResultCaller(GetContext context, Map<Class<?>, Class<ResultProcess>> defineMap) {
        this.defineMap = defineMap;
        this.context = context;
    }
    /***/
    public Object invoke(ControllerInvocation invocation) throws Throwable {
        Method javaMethod = invocation.getControllerMethod();
        Annotation[] annos = javaMethod.getAnnotations();
        if (annos == null)
            return invocation.proceed();
        //
        for (Annotation anno : annos) {
            Class<?> resultType = anno.annotationType();
            if (resultType != null) {
                Class<ResultProcess> processType = this.defineMap.get(resultType);
                if (processType != null) {
                    Object returnData = invocation.proceed();
                    ResultProcess process = this.context.getAppContext().getInstance(processType);
                    process.process(invocation.getRequest(), invocation.getResponse(), anno, returnData);
                    return returnData;
                }
            }
        }
        return invocation.proceed();
    }
}