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
package org.platform.security.support.impl;
import static org.platform.PlatformConfig.Security_AuthSessionCache_AutoRenewal;
import static org.platform.PlatformConfig.Security_AuthSessionCache_Eternal;
import static org.platform.PlatformConfig.Security_AuthSessionCache_ThreadSeep;
import static org.platform.PlatformConfig.Security_AuthSessionCache_Timeout;
import org.platform.context.Settings;
import org.platform.icache.Cache;
import org.platform.icache.mapcache.MapCache;
import org.platform.icache.mapcache.MapCacheSettings;
import org.platform.security.support.SessionData;
/**
 * 内置的权限缓存数据
 * @version : 2013-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
@Cache(value = "AuthSessionCache", displayName = "AuthSessionMapCache", description = "内置的AuthSession数据缓存。")
public class InternalAuthSessionMapCache extends MapCache<SessionData> {
    public InternalAuthSessionMapCache() {
        super();
        this.threadName = "AuthSessionCache-Daemon";
    }
    @Override
    protected MapCacheSettings getMapCacheSettings() {
        return new InternalAuthSessionMapCacheSettings();
    }
    /*--------------------------------------------------------------------------------------*/
    /**负责InternalAuthSessionMapCache，类的配置文件监听工作*/
    class InternalAuthSessionMapCacheSettings extends MapCacheSettings {
        @Override
        public void loadConfig(Settings newConfig) {
            this.setCacheEnable(true);
            this.setDefaultTimeout(newConfig.getLong(Security_AuthSessionCache_Timeout));
            this.setEternal(newConfig.getBoolean(Security_AuthSessionCache_Eternal));
            this.setAutoRenewal(newConfig.getBoolean(Security_AuthSessionCache_AutoRenewal));
            this.setThreadSeep(newConfig.getLong(Security_AuthSessionCache_ThreadSeep));
        }
    }
}