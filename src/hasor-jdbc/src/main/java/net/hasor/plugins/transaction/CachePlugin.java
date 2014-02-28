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
package net.hasor.plugins.transaction;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.core.plugin.Plugin;
import net.hasor.plugins.aop.matchers.AopMatchers;
import org.more.RepeateException;
import com.google.inject.matcher.Matcher;
/**
 * ª∫¥Ê∑˛ŒÒ°£∆Ù∂Øº∂±£∫Lv_0
 * @version : 2013-4-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Plugin()
public class CachePlugin extends AbstractHasorPlugin {
    /**≥ı ºªØ.*/
    public void loadPlugin(ApiBinder apiBinder) {
        //1.π“‘ÿAop
        Matcher<Object> matcher = AopMatchers.annotatedWith(Transaction.class);//
        apiBinder.getGuiceBinder().bindInterceptor(matcher, matcher, new CacheInterceptor(apiBinder));
        //2.≈≈¥Ì
        Set<Class<?>> cacheSet = apiBinder.findClass(Creator.class);
        if (cacheSet == null || cacheSet.isEmpty())
            return;
        if (cacheSet.size() > 1)
            throw new RepeateException(Hasor.formatString("repeat CacheCreator at: %s.", cacheSet));
        Class<?> cacheCreator = cacheSet.iterator().next();
        if (CacheCreator.class.isAssignableFrom(cacheCreator) == false)
            throw new ClassCastException("cannot be cast to " + CacheCreator.class.getName());
        //2.◊¢≤·∑˛ŒÒ
        apiBinder.bindingType(CacheCreator.class, (Class<CacheCreator>) cacheCreator).asEagerSingleton();
    }
}