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
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import org.more.util.Iterators;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.setting.Config;
import org.platform.context.setting.Settings;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 声明一个Cache，该Cache需要实现{@link ICache}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
class CacheDefinition implements Provider<ICache> {
    private String[]              names       = null;
    private Key<? extends ICache> cacheKey    = null;
    private Map<String, String>   initParams  = null;
    private ICache                cacheObject = null;
    //
    public CacheDefinition(String[] names, Key<? extends ICache> cacheKey, Map<String, String> initParams) {
        this.names = names;
        this.cacheKey = cacheKey;
        this.initParams = initParams;
    }
    public Key<? extends ICache> getCacheKey() {
        return cacheKey;
    }
    public String[] getNames() {
        return names;
    }
    public void initCache(final AppContext appContext) {
        this.cacheObject = appContext.getGuice().getInstance(this.cacheKey);
        this.cacheObject.initCache(appContext, new Config() {
            public ServletContext getServletContext() {
                return appContext.getInitContext().getServletContext();
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
            @Override
            public Settings getSettings() {
                return appContext.getSettings();
            }
            @Override
            public void addSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().addSettingsListener(settingsListener);
            }
            @Override
            public void removeSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().removeSettingsListener(settingsListener);
            }
        });
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