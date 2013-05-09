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
package org.platform.icache.mapcache;
import static org.platform.PlatformConfig.CacheConfig_Enable;
import static org.platform.PlatformConfig.CacheConfig_MapCache_AutoRenewal;
import static org.platform.PlatformConfig.CacheConfig_MapCache_Eternal;
import static org.platform.PlatformConfig.CacheConfig_MapCache_ThreadSeep;
import static org.platform.PlatformConfig.CacheConfig_MapCache_Timeout;
import org.platform.context.SettingListener;
import org.platform.context.Settings;
/**
 * ≈‰÷√–≈œ¢
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class MapCacheSettings implements SettingListener {
    private boolean cacheEnable    = false;
    private long    defaultTimeout = 10;
    private boolean eternal        = false;
    private boolean autoRenewal    = false;
    private long    threadSeep     = 500;
    //
    public boolean isCacheEnable() {
        return cacheEnable;
    }
    public long getDefaultTimeout() {
        return defaultTimeout;
    }
    public boolean isEternal() {
        return eternal;
    }
    public boolean isAutoRenewal() {
        return autoRenewal;
    }
    public long getThreadSeep() {
        return threadSeep;
    }
    protected void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }
    protected void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }
    protected void setEternal(boolean eternal) {
        this.eternal = eternal;
    }
    protected void setAutoRenewal(boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
    protected void setThreadSeep(long threadSeep) {
        this.threadSeep = threadSeep;
    }
    @Override
    public void loadConfig(Settings newConfig) {
        this.cacheEnable = newConfig.getBoolean(CacheConfig_Enable);
        this.defaultTimeout = newConfig.getLong(CacheConfig_MapCache_Timeout);
        this.eternal = newConfig.getBoolean(CacheConfig_MapCache_Eternal);
        this.autoRenewal = newConfig.getBoolean(CacheConfig_MapCache_AutoRenewal);
        this.threadSeep = newConfig.getLong(CacheConfig_MapCache_ThreadSeep);
    }
}