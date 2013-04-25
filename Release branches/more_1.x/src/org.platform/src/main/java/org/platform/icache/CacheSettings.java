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
import static org.platform.PlatformConfig.CacheConfig_Enable;
import org.platform.context.SettingListener;
import org.platform.context.setting.Settings;
/**
 * 
 * @version : 2013-4-23
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class CacheSettings implements SettingListener {
    private boolean enable = false; /*Ä¬ÈÏ¹Ø±Õ×´Ì¬*/
    //
    public boolean isCacheEnable() {
        return this.enable;
    }
    @Override
    public void reLoadConfig(Settings oldConfig, Settings newConfig) {
        this.enable = newConfig.getBoolean(CacheConfig_Enable, false);
    }
    public void loadConfig(Settings config) {
        this.reLoadConfig(null, config);
    }
}