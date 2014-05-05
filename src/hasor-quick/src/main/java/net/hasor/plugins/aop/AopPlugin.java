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
package net.hasor.plugins.aop;
import net.hasor.core.ApiBinder;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.plugins.aop.matchers.AopMatchers;
import net.hasor.quick.plugin.Plugin;
import com.google.inject.matcher.Matcher;
/**
 * 提供 <code>@Aop</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class AopPlugin extends AbstractHasorPlugin {
    public void loadPlugin(ApiBinder apiBinder) {
        //2.@Aop拦截器
        Matcher<Object> matcherAop = AopMatchers.annotatedWith(Aop.class);//
        apiBinder.getGuiceBinder().bindInterceptor(AopMatchers.anyClass(), matcherAop, new AopInterceptor(apiBinder));
    }
}