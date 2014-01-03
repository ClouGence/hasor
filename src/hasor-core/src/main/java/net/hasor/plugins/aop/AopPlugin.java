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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.core.plugin.Plugin;
import net.hasor.plugins.aop.matchers.AopMatchers;
import org.aopalliance.intercept.MethodInterceptor;
import com.google.inject.matcher.Matcher;
/**
 * 提供 <code>@Aop</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class AopPlugin extends AbstractHasorPlugin {
    public void loadPlugin(ApiBinder apiBinder) {
        //1.@GlobalAop全局拦截器
        List<Class<? extends MethodInterceptor>> globalInterceptorList = new ArrayList<Class<? extends MethodInterceptor>>();
        Set<Class<?>> globalAopSet = apiBinder.getClassSet(GlobalAop.class);//
        if (globalAopSet == null || globalAopSet.isEmpty()) {
            Hasor.logInfo("no Global Aop.");
        } else {
            //A.循环 globalAopSet 将合格的 GlobalAop 添加到  globalInterceptor。
            for (Class<?> globalAop : globalAopSet) {
                if (MethodInterceptor.class.isAssignableFrom(globalAop) == false) {
                    Hasor.logWarn("class %s not implement MethodInterceptor.", globalAop);
                    continue;
                }
                Hasor.logInfo("add Global Aop : %s.", globalAop);
                globalInterceptorList.add((Class<? extends MethodInterceptor>) globalAop);
            }
            //B.如果存在两个以上 Global Aop 为它们建立顺序。
            if (globalInterceptorList.size() > 1) {
                Collections.sort(globalInterceptorList, new Comparator<Class<? extends MethodInterceptor>>() {
                    public int compare(Class<? extends MethodInterceptor> o1, Class<? extends MethodInterceptor> o2) {
                        GlobalAop annoO1 = o1.getAnnotation(GlobalAop.class);
                        GlobalAop annoO2 = o2.getAnnotation(GlobalAop.class);
                        if (annoO1.index() > annoO2.index())
                            return 1;
                        else if (annoO1.index() == annoO2.index())
                            return 0;
                        else
                            return -1;
                    }
                });
            }
        }
        //2.@Aop拦截器
        Matcher<Object> matcherAop = AopMatchers.annotatedWith(Aop.class);//
        if (globalInterceptorList.size() != 0)
            matcherAop = AopMatchers.any();
        apiBinder.getGuiceBinder().bindInterceptor(matcherAop, matcherAop, new AopInterceptor(globalInterceptorList, apiBinder));
    }
}