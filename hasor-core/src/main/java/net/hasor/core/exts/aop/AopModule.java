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
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Matcher;
import net.hasor.core.Module;
import net.hasor.core.classcode.matcher.AopMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
/**
 * 提供 <code>@Aop</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //Aop拦截器
        Matcher<Class<?>> matcherClass = AopMatchers.annotatedWithClass(Aop.class);//
        Matcher<Method> matcherMethod = AopMatchers.annotatedWithMethod(Aop.class);//
        //
        logger.info("aops -> matcherClass = {}, matcherMethod ={}.", matcherClass, matcherMethod);
        AopInterceptor aopInterceptor = Hasor.autoAware(apiBinder.getEnvironment(), new AopInterceptor());
        apiBinder.bindInterceptor(matcherClass, matcherMethod, aopInterceptor);
    }
}