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
package net.hasor.quick.cache;
import java.lang.reflect.Method;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.quick.plugin.Plugin;
import org.more.RepeateException;
/**
 * 缓存服务。启动级别：Lv_0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin()
public class CachePlugin implements Module {
    /**初始化.*/
    public void loadModule(ApiBinder apiBinder) {
        //1.挂载Aop
        Matcher<Class<?>> matcherCass = AopMatchers.annotatedWithClass(NeedCache.class);//
        Matcher<Method> matcherMethod = AopMatchers.annotatedWithMethod(NeedCache.class);//
        apiBinder.bindInterceptor(matcherCass, matcherMethod, new CacheInterceptor(apiBinder));
        //2.排错
        Set<Class<?>> cacheSet = apiBinder.findClass(Creator.class);
        if (cacheSet == null || cacheSet.isEmpty())
            return;
        if (cacheSet.size() > 1)
            throw new RepeateException(Hasor.formatString("repeat CacheCreator at: %s.", cacheSet));
        Class<?> cacheCreator = cacheSet.iterator().next();
        if (CacheCreator.class.isAssignableFrom(cacheCreator) == false)
            throw new ClassCastException("cannot be cast to " + CacheCreator.class.getName());
        //2.注册服务
        apiBinder.bindType(CacheCreator.class, (Class<CacheCreator>) cacheCreator).asEagerSingleton();
    }
}