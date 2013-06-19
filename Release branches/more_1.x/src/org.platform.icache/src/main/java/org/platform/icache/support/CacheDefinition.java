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
import org.platform.icache.Cache;
import com.google.inject.Provider;
/**
 * 声明一个Cache，该Cache需要实现{@link Cache}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
class CacheDefinition implements Provider<Cache<?>> {
    private String          name        = null;
    private Class<Cache<?>> cacheType   = null;
    private Cache<?>        cacheObject = null;
    //
    public CacheDefinition(String name, Class<Cache<?>> cacheType) {
        this.name = name;
        this.cacheType = cacheType;
    }
    public String getName() {
        return name;
    }
    public void initCache(AppContext appContext) {
        Platform.info("initCache [%s] bind %s.", name, cacheType);
        this.cacheObject = appContext.getGuice().getInstance(this.cacheType);
        this.cacheObject.initCache(appContext);
    }
    public void destroy(AppContext appContext) {
        if (this.cacheObject != null)
            this.cacheObject.destroy(appContext);
    }
    @Override
    public Cache<?> get() {
        return this.cacheObject;
    }
}