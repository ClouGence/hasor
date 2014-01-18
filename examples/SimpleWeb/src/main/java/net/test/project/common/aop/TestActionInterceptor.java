/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.project.common.aop;
import javax.servlet.http.HttpServletRequest;
import net.hasor.core.Hasor;
import net.hasor.plugins.aop.GlobalAop;
import net.hasor.plugins.controller.interceptor.ControllerInterceptor;
import net.hasor.plugins.controller.interceptor.ControllerInvocation;
/**
 * 全局Aop，负责拦截所有 Controller 调用，并输出  Action 调用日志记录 
 * @version : 2013-12-23
 * @author 赵永春(zyc@hasor.net)
 */
@GlobalAop("*net.test.project.*")
public class TestActionInterceptor extends ControllerInterceptor {
    /* 
     * 1.@GlobalAop 注解生命该拦截器为全局拦截器，并且拦截所有类的所有方法
     * 2.ControllerInterceptor 类型的拦截器，只会拦截 Controller 的 Action 方法。
     */
    public Object invoke(ControllerInvocation invocation) throws Throwable {
        try {
            HttpServletRequest reqest = invocation.getRequest();
            Hasor.logInfo("调用 Action :%s.", reqest.getRequestURI());
            return invocation.proceed();
        } catch (Exception e) {
            throw e;
        }
    }
}