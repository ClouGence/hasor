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
package org.platform.action.support;
import org.platform.context.AppContext;
import org.platform.icache.ICache;
import com.google.inject.Provider;
/**
 * ÉùÃ÷Ò»¸öAction¡£
 * @version : 2013-3-12
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
class ActionDefinition implements Provider<Object> {
    private String        name        = null;
    private Class<ICache> cacheType   = null;
    private ICache        cacheObject = null;
    //
    public ActionDefinition(String name, Class<ICache> cacheType) {
        this.name = name;
        this.cacheType = cacheType;
    }
    public String getName() {
        return name;
    }
    public void initCache(final AppContext appContext) {
        this.cacheObject = appContext.getGuice().getInstance(this.cacheType);
        this.cacheObject.initCache(appContext);
    }
    public void destroy(AppContext appContext) {
        if (this.cacheObject != null)
            this.cacheObject.destroy(appContext);
    }
    @Override
    public ICache get() {
        return this.cacheObject;
    }
}