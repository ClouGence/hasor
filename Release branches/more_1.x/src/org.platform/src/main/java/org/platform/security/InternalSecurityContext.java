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
package org.platform.security;
import static org.platform.PlatformConfig.Security_AuthSessionCache;
import static org.platform.PlatformConfig.Security_AuthSessionCache_AutoRenewal;
import static org.platform.PlatformConfig.Security_AuthSessionCache_Eternal;
import static org.platform.PlatformConfig.Security_AuthSessionCache_ThreadSeep;
import static org.platform.PlatformConfig.Security_AuthSessionCache_Timeout;
import static org.platform.PlatformConfig.Security_AuthSessionTimeout;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.setting.Settings;
import org.platform.icache.Cache;
import org.platform.icache.ICache;
import org.platform.icache.mapcache.MapCache;
import org.platform.icache.mapcache.MapCacheSettings;
import com.google.inject.Key;
import com.google.inject.name.Names;
/**
 * 内置SecurityContext类实现
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalSecurityContext extends SecurityContext {
    private ICache<SessionData> authSessionCache = null;
    private AppContext          appContext       = null;
    private SettingListener     settingListener  = new SessionDataCacheSettingListener();
    private long                sessionTimeOut   = 0;
    //
    //
    @Override
    public synchronized void initSecurity(AppContext appContext) {
        super.initSecurity(appContext);
        this.appContext = appContext;
        this.settingListener.loadConfig(appContext.getSettings());
        appContext.getInitContext().getConfig().addSettingsListener(settingListener);
    }
    @Override
    public synchronized void destroySecurity(AppContext appContext) {
        super.destroySecurity(appContext);
        appContext.getInitContext().getConfig().removeSettingsListener(settingListener);
    }
    //
    @Override
    protected void removeSessionData(String sessionDataID) {
        this.authSessionCache.remove(sessionDataID);
    }
    @Override
    protected void updateSessionData(String sessionID) {
        this.authSessionCache.refreshCache(sessionID);
    }
    @Override
    protected void updateSessionData(String sessionDataID, SessionData newSessionData) {
        this.authSessionCache.toCache(sessionDataID, newSessionData, this.sessionTimeOut);
    }
    @Override
    protected SessionData getSessionData(String sessionDataID) {
        return this.authSessionCache.fromCache(sessionDataID);
    }
    /*--------------------------------------------------------------------------------------*/
    /**负责监听SessionDataCache部分配置文件改动的生效*/
    class SessionDataCacheSettingListener implements SettingListener {
        @Override
        public void loadConfig(Settings newConfig) {
            Key cacheKey = Key.get(ICache.class, Names.named(newConfig.getString(Security_AuthSessionCache)));
            authSessionCache = appContext.getGuice().getInstance(cacheKey);
            sessionTimeOut = newConfig.getLong(Security_AuthSessionTimeout);
        }
    }
    /**负责InternalAuthSessionMapCache，类的配置文件监听工作*/
    static class InternalAuthSessionMapCacheSettings extends MapCacheSettings {
        @Override
        public void loadConfig(Settings newConfig) {
            this.setCacheEnable(true);
            this.setDefaultTimeout(newConfig.getLong(Security_AuthSessionCache_Timeout));
            this.setEternal(newConfig.getBoolean(Security_AuthSessionCache_Eternal));
            this.setAutoRenewal(newConfig.getBoolean(Security_AuthSessionCache_AutoRenewal));
            this.setThreadSeep(newConfig.getLong(Security_AuthSessionCache_ThreadSeep));
        }
    }
    /**内置的权限缓存数据*/
    @Cache(value = "AuthSessionCache", displayName = "AuthSessionMapCache", description = "内置的AuthSession数据缓存。")
    static class InternalAuthSessionMapCache extends MapCache<SessionData> {
        public InternalAuthSessionMapCache() {
            super();
            this.threadName = "AuthSessionCache-Daemon";
        }
        @Override
        protected MapCacheSettings getMapCacheSettings() {
            return new InternalAuthSessionMapCacheSettings();
        }
    }
}
