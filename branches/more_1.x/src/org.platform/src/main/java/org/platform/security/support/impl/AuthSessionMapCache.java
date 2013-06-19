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
import org.platform.icache.Cache;
import org.platform.icache.mapcache.MapCache;
import org.platform.icache.mapcache.MapCacheSettings;
/**
 * 使用Map作为缓存，MapCache缓存仅作为内置提供的一个默认实现。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Cache(value = "AuthSessionCache", displayName = "AuthSessionMapCache", description = "内置的AuthSession数据缓存。")
public class AuthSessionMapCache<T> extends MapCache<T> {
    @Override
    protected MapCacheSettings getMapCacheSettings() {
        return new MapCacheSettings() {
            @Override
            protected String getMapCacheSettingElementName() {
                return "security.internalAuthSessionMapCache";
            }
        };
    }
    @Override
    protected String getThreadName() {
        return "AuthSessionCache-Daemon";
    }
}