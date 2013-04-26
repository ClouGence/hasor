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
package org.platform.icache;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.platform.Platform;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * 缓存使用入口，缓存的实现由系统自行提供。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
class ManagedCacheManager {
    private Map<String, CacheDefinition> cacheDefinitionMap = null;
    //
    public void initManager(AppContext appContext) {
        Platform.info("init ManagedCacheManager...");
        this.cacheDefinitionMap = collectCacheDefinitionMap(appContext.getGuice());
        for (Entry<String, CacheDefinition> cacheDefinitionEnt : cacheDefinitionMap.entrySet()) {
            cacheDefinitionEnt.getValue().initCache(appContext);
        }
    }
    private Map<String, CacheDefinition> collectCacheDefinitionMap(Injector injector) {
        Map<String, CacheDefinition> cacheDefinitionMap = new HashMap<String, CacheDefinition>();
        TypeLiteral<CacheDefinition> CACHE_DEFS = TypeLiteral.get(CacheDefinition.class);
        for (Binding<CacheDefinition> entry : injector.findBindingsByType(CACHE_DEFS)) {
            CacheDefinition define = entry.getProvider().get();
            for (String name : define.getNames())
                cacheDefinitionMap.put(name, define);
        }
        return cacheDefinitionMap;
    }
    public void destroyManager(AppContext appContext) {
        Platform.info("destroy ManagedCacheManager...");
        for (Entry<String, CacheDefinition> cacheDefinitionEnt : cacheDefinitionMap.entrySet()) {
            cacheDefinitionEnt.getValue().destroy(appContext);
        }
    }
    public ICache getCache(String cacheName, AppContext appContext) {
        CacheDefinition define = this.cacheDefinitionMap.get(cacheName);
        if (define == null)
            return null;
        return appContext.getGuice().getInstance(define.getCacheKey());
    }
}