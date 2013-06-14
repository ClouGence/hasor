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
package org.platform.icache.support;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.icache.CacheManager;
import org.platform.icache.CacheFace;
import org.platform.icache.KeyBuilderFace;
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
    private CacheFace<Object>           defaultCache      = null;
    private KeyBuilderFace              defaultKeyBuilder = null;
    @Override
    public void initManager(AppContext appContext) {
        this.appContext = appContext;
        //
        this.cacheManager = new ManagedCacheManager();
        this.keyBuilderManager = new ManagedKeyBuilderManager();
        this.cacheManager.initManager(appContext);
        this.keyBuilderManager.initManager(appContext);
        //
        this.defaultCache = appContext.getGuice().getInstance(CacheFace.class);
        this.defaultKeyBuilder = appContext.getGuice().getInstance(KeyBuilderFace.class);
        Platform.info("CacheManager initialized.");
    }
    @Override
    public void destroyManager(AppContext appContext) {
        Platform.info("destroy CacheManager...");
        this.cacheManager.destroyManager(this.appContext);
        this.keyBuilderManager.destroyManager(this.appContext);
    }
    @Override
    public CacheFace<Object> getDefaultCache() {
        return this.defaultCache;
    }
    @Override
    public CacheFace<Object> getCache(String cacheName) {
        CacheFace<Object> icache = this.cacheManager.getCache(cacheName, this.appContext);
        if (icache == null) {
            Platform.warning("use defaultCache . '%s' is not exist.", cacheName);
            return this.defaultCache;
        }
        return icache;
    }
    @Override
    public KeyBuilderFace getKeyBuilder(Class<?> sampleType) {
        KeyBuilderFace keyBuilder = this.keyBuilderManager.getKeyBuilder(sampleType, this.appContext);
        if (keyBuilder == null) {
            Platform.warning("use defaultKeyBuilder . '%s' is not register.", sampleType);
            return this.defaultKeyBuilder;
        }
        return keyBuilder;
    }
}