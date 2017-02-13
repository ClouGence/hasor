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
package net.test.hasor.core._03_aop.myaop;
import net.hasor.core.ApiBinder;
import net.hasor.core.Matcher;
import net.hasor.core.Module;
import net.hasor.core.classcode.matcher.AopMatchers;
import net.test.hasor.core._03_aop.simple.SimpleInterceptor;

import java.lang.reflect.Method;
/**
 * 让自定义MyAop注解生效。
 * @version : 2015年11月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class MyAopSetup implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //1.任意类
        Matcher<Class<?>> atClass = AopMatchers.anyClass();
        //2.有MyAop注解的方法
        Matcher<Method> atMethod = AopMatchers.annotatedWithMethod(MyAop.class);
        //3.让@MyAop注解生效
        apiBinder.bindInterceptor(atClass, atMethod, new SimpleInterceptor());
    }
}