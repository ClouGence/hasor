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
package org.moreframework.icache.support;
import static org.moreframework.icache.CacheManager.CacheConfig_Enable;
import org.moreframework.context.SettingListener;
import org.moreframework.context.Settings;
/**
 * 
 * @version : 2013-4-23
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
class CacheSettings implements SettingListener {
    private boolean enable = false; /*Ä¬ÈÏ¹Ø±Õ×´Ì¬*/
    //
    public boolean isCacheEnable() {
        return this.enable;
    }
    public void loadConfig(Settings config) {
        this.enable = config.getBoolean(CacheConfig_Enable, false);
    }
}