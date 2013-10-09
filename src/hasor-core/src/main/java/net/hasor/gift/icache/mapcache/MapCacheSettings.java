/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.gift.icache.mapcache;
import net.hasor.core.SettingsListener;
import net.hasor.gift.setting.Settings;
/**
 * ≈‰÷√–≈œ¢
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Settings
public class MapCacheSettings implements SettingsListener {
    private long    defaultTimeout = 10;
    private boolean eternal        = false;
    private boolean autoRenewal    = false;
    private long    threadSeep     = 500;
    //
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
    protected String getMapCacheSettingElementName() {
        return "cacheSettings.mapCache";
    }
    public void onLoadConfig(net.hasor.core.Settings newConfig) {
        this.defaultTimeout = newConfig.getLong(getMapCacheSettingElementName() + ".timeout");
        this.eternal = newConfig.getBoolean(getMapCacheSettingElementName() + ".eternal");
        this.autoRenewal = newConfig.getBoolean(getMapCacheSettingElementName() + ".autoRenewal");
        this.threadSeep = newConfig.getLong(getMapCacheSettingElementName() + ".threadSeep");
    }
}