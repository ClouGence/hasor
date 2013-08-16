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
package org.hasor.icache.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hasor.Hasor;
import org.hasor.context.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.Module;
import org.hasor.context.anno.support.AnnoSupportModule;
import org.hasor.context.reactor.AbstractHasorModule;
import org.hasor.icache.Cache;
import org.hasor.icache.CacheDefine;
import org.hasor.icache.CacheManager;
import org.hasor.icache.DefaultCache;
import org.hasor.icache.DefaultKeyBuilder;
import org.hasor.icache.KeyBuilder;
import org.hasor.icache.KeyBuilderDefine;
import org.hasor.icache.NeedCache;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.name.Names;
/**
 * 缓存服务。启动级别：Lv_0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(description = "org.hasor.icache软件包功能支持。")
public class CacheSupportModule extends AbstractHasorModule {
    private CacheManager cacheManager = null;
    @Override
    public void configuration(ModuleSettings info) {
        info.followTarget(AnnoSupportModule.class);
    }
    /**初始化.*/
    @Override
    public void init(ApiBinder apiBinder) {
        //1.挂载Aop
        apiBinder.getGuiceBinder().bindInterceptor(new ClassNeedCacheMatcher(), new MethodPowerMatcher(), new CacheInterceptor());
        //2.载入缓存配置
        this.loadCache(apiBinder);
        this.loadKeyBuilder(apiBinder);
        //3.注册Manager
        apiBinder.getGuiceBinder().bind(CacheManager.class).to(DefaultCacheManager.class).asEagerSingleton();
    }
    @Override
    public void start(AppContext appContext) {
        this.cacheManager = appContext.getInstance(CacheManager.class);
        this.cacheManager.initManager(appContext);
    }
    @Override
    public void destroy(AppContext appContext) {
        this.cacheManager.destroyManager(appContext);
    }
    //
    /*装载KeyBuilder*/
    protected void loadKeyBuilder(ApiBinder event) {
        //1.获取
        Set<Class<?>> iKeyBuilderSet = event.getClassSet(KeyBuilderDefine.class);
        if (iKeyBuilderSet == null)
            return;
        List<Class<? extends KeyBuilder>> iKeyBuilderList = new ArrayList<Class<? extends KeyBuilder>>();
        for (Class<?> cls : iKeyBuilderSet) {
            if (KeyBuilder.class.isAssignableFrom(cls) == false) {
                Hasor.warning("loadKeyBuilder : not implemented IKeyBuilder of type %s.", cls);
            } else
                iKeyBuilderList.add((Class<? extends KeyBuilder>) cls);
        }
        //2.排序
        Collections.sort(iKeyBuilderList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                KeyBuilderDefine o1Anno = o1.getAnnotation(KeyBuilderDefine.class);
                KeyBuilderDefine o2Anno = o2.getAnnotation(KeyBuilderDefine.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册服务
        long defaultKeyBuilderIndex = Long.MAX_VALUE;
        Binder binder = event.getGuiceBinder();
        for (Class<? extends KeyBuilder> keyBuildertype : iKeyBuilderList) {
            KeyBuilderDefine keyBuilderAnno = keyBuildertype.getAnnotation(KeyBuilderDefine.class);
            Key<? extends KeyBuilder> keyBuilderKey = Key.get(keyBuildertype);
            KeyBuilderDefinition keyBuilderDefine = new KeyBuilderDefinition(keyBuilderAnno.value(), keyBuilderKey);
            binder.bind(KeyBuilderDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(keyBuilderDefine);
            Hasor.info("KeyBuilder type:" + Hasor.logString(keyBuildertype) + " mapping " + Hasor.logString(keyBuilderAnno.value()));
            //确定是否为defaut
            if (keyBuildertype.isAnnotationPresent(DefaultKeyBuilder.class) == true) {
                Hasor.warning("KeyBuilder type:" + Hasor.logString(keyBuildertype) + " is DefaultKeyBuilder on " + Hasor.logString(keyBuilderAnno.value()));
                DefaultKeyBuilder defaultKeyBuilder = keyBuildertype.getAnnotation(DefaultKeyBuilder.class);
                if (defaultKeyBuilder.value() <= defaultKeyBuilderIndex/*数越小越优先*/) {
                    defaultKeyBuilderIndex = defaultKeyBuilder.value();
                    binder.bind(KeyBuilder.class).toProvider(keyBuilderDefine);
                }
            }
        }
    }
    //
    /*装载Cache*/
    protected void loadCache(ApiBinder event) {
        //1.获取
        Set<Class<?>> cacheSet = event.getClassSet(CacheDefine.class);
        if (cacheSet == null)
            return;
        List<Class<Cache<?>>> cacheList = new ArrayList<Class<Cache<?>>>();
        if (cacheSet != null)
            for (Class<?> cls : cacheSet) {
                if (Cache.class.isAssignableFrom(cls) == false) {
                    Hasor.warning("loadCache : not implemented ICache of type %s", cls);
                } else
                    cacheList.add((Class<Cache<?>>) cls);
            }
        //3.注册服务
        long defaultCacheIndex = Long.MAX_VALUE;
        Binder binder = event.getGuiceBinder();
        Map<String, Integer> cacheIndex = new HashMap<String, Integer>();
        for (Class<Cache<?>> cacheType : cacheList) {
            CacheDefine cacheAnno = cacheType.getAnnotation(CacheDefine.class);
            for (String cacheName : cacheAnno.value()) {
                Hasor.info(cacheName + " at Cache of type " + Hasor.logString(cacheType));
                //
                int maxIndex = (cacheIndex.containsKey(cacheName) == false) ? Integer.MAX_VALUE : cacheIndex.get(cacheName);
                if (cacheAnno.sort() <= maxIndex/*数越小越优先*/) {
                    cacheIndex.put(cacheName, cacheAnno.sort());
                    //
                    CacheDefinition cacheDefine = new CacheDefinition(cacheName, cacheType);
                    binder.bind(CacheDefinition.class).annotatedWith(Names.named(cacheName)).toInstance(cacheDefine);
                    binder.bind(Cache.class).annotatedWith(Names.named(cacheName)).toProvider(cacheDefine);
                    //确定是否为defaut
                    if (cacheType.isAnnotationPresent(DefaultCache.class) == true) {
                        Hasor.warning(cacheName + " is DefaultCache!");
                        DefaultCache defaultCache = cacheType.getAnnotation(DefaultCache.class);
                        if (defaultCache.value() <= defaultCacheIndex/*数越小越优先*/) {
                            defaultCacheIndex = defaultCache.value();
                            binder.bind(Cache.class).toProvider(cacheDefine);
                        }
                    }
                }
            }
        }
    }
    /*-------------------------------------------------------------------------------------*/
    /*负责检测类是否匹配。规则：只要类型或方法上标记了@NeedCache。*/
    private class ClassNeedCacheMatcher extends AbstractMatcher<Class<?>> {
        @Override
        public boolean matches(Class<?> matcherType) {
            if (matcherType.isAnnotationPresent(NeedCache.class) == true)
                return true;
            Method[] m1s = matcherType.getMethods();
            Method[] m2s = matcherType.getDeclaredMethods();
            for (Method m1 : m1s) {
                if (m1.isAnnotationPresent(NeedCache.class) == true)
                    return true;
            }
            for (Method m2 : m2s) {
                if (m2.isAnnotationPresent(NeedCache.class) == true)
                    return true;
            }
            return false;
        }
    }
    /*负责检测方法是否匹配。规则：方法或方法所处类上标记了@NeedCache。*/
    private class MethodPowerMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method matcherType) {
            if (matcherType.isAnnotationPresent(NeedCache.class) == true)
                return true;
            if (matcherType.getDeclaringClass().isAnnotationPresent(NeedCache.class) == true)
                return true;
            return false;
        }
    }
    /*拦截器*/
    private class CacheInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            //1.获取缓存数据
            Method targetMethod = invocation.getMethod();
            NeedCache cacheAnno = targetMethod.getAnnotation(NeedCache.class);
            if (cacheAnno == null)
                cacheAnno = targetMethod.getDeclaringClass().getAnnotation(NeedCache.class);
            if (cacheAnno == null)
                return invocation.proceed();
            //2.获取Key
            StringBuilder cacheKey = new StringBuilder(targetMethod.toString());
            Object[] args = invocation.getArguments();
            if (args != null)
                for (Object arg : args) {
                    if (arg == null) {
                        cacheKey.append("NULL");
                        continue;
                    }
                    /*保证arg参数不为空*/
                    KeyBuilder keyBuilder = cacheManager.getKeyBuilder(arg.getClass());
                    cacheKey.append(keyBuilder.serializeKey(arg));
                }
            Hasor.debug("MethodInterceptor Method : %s", targetMethod);
            Hasor.debug("MethodInterceptor Cache key :%s", cacheKey.toString());
            //3.获取缓存
            Cache<Object> cacheObject = null;
            if (StringUtils.isBlank(cacheAnno.cacheName()) == true)
                cacheObject = cacheManager.getDefaultCache();
            else
                cacheObject = cacheManager.getCache(cacheAnno.cacheName());
            //4.操作缓存
            String key = cacheKey.toString();
            Object returnData = null;
            if (cacheObject.hasCache(key) == true) {
                Hasor.debug("the method return data is from Cache.");
                returnData = cacheObject.fromCache(key);
            } else {
                Hasor.debug("set data to Cache key :" + key);
                returnData = invocation.proceed();
                cacheObject.toCache(key, returnData, cacheAnno.timeout());
            }
            return returnData;
        }
    }
}