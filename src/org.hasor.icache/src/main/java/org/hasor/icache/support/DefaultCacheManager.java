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
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.icache.Cache;
import org.hasor.icache.CacheManager;
import org.hasor.icache.KeyBuilder;
import com.google.inject.Singleton;
/**
 * 缓存使用入口，缓存的实现由系统自行提供。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
class DefaultCacheManager implements CacheManager {
    private ManagedCacheManager      cacheManager      = null;
    private ManagedKeyBuilderManager keyBuilderManager = null;
    private AppContext               appContext        = null;
    private Cache<Object>            defaultCache      = null;
    private KeyBuilder               defaultKeyBuilder = null;
    @Override
    public void initManager(AppContext appContext) {
        this.appContext = appContext;
        //
        this.cacheManager = new ManagedCacheManager();
        this.keyBuilderManager = new ManagedKeyBuilderManager();
        this.cacheManager.initManager(appContext);
        this.keyBuilderManager.initManager(appContext);
        //
        this.defaultCache = appContext.getGuice().getInstance(Cache.class);
        this.defaultKeyBuilder = appContext.getGuice().getInstance(KeyBuilder.class);
        Hasor.info("CacheManager initialized.");
    }
    @Override
    public void destroyManager(AppContext appContext) {
        Hasor.info("destroy CacheManager...");
        this.cacheManager.destroyManager(this.appContext);
        this.keyBuilderManager.destroyManager(this.appContext);
    }
    @Override
    public Cache<Object> getDefaultCache() {
        return this.defaultCache;
    }
    @Override
    public Cache<Object> getCache(String cacheName) {
        Cache<Object> icache = this.cacheManager.getCache(cacheName, this.appContext);
        if (icache == null) {
            Hasor.warning("use defaultCache . '%s' is not exist.", cacheName);
            return this.defaultCache;
        }
        return icache;
    }
    @Override
    public KeyBuilder getKeyBuilder(Class<?> sampleType) {
        KeyBuilder keyBuilder = this.keyBuilderManager.getKeyBuilder(sampleType, this.appContext);
        if (keyBuilder == null) {
            Hasor.warning("use defaultKeyBuilder . '%s' is not register.", sampleType);
            return this.defaultKeyBuilder;
        }
        return keyBuilder;
    }
}