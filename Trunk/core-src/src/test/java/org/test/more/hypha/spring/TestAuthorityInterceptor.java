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
package org.test.more.hypha.spring;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
public class TestAuthorityInterceptor implements MethodInterceptor {
    // invoke方法返回调用的结果   
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //        String methodName = invocation.getMethod().getName();
        //        if (user.getUsername().equals("unRegistedUser")) {
        //            System.out.println("你的身份是未注册用户,没有权限回复,删除帖子!");
        //            return null;
        //        }
        //        if ((user.getUsername().equals("user")) && (methodName.equals("deleteTopic"))) {
        //            System.out.println("你的身份是注册用户,没有权限删除帖子");
        //            return null;
        //        }
        //        // proceed()方法对连接点的整个拦截器链起作用,拦截器链中的每个拦截器都执行该方法,并返回它的返回值   
        System.out.println("a");
        return invocation.proceed();
    }
}
