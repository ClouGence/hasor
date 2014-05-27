/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.project.common.cache;
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.AppContext;
import net.hasor.plugins.cache.Cache;
import net.hasor.plugins.cache.CacheCreator;
import net.hasor.plugins.cache.Creator;
/**
 * 
 * @version : 2014-1-9
 * @author 赵永春(zyc@hasor.net)
 */
@Creator
public class MapCacheCreator implements CacheCreator {
    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();
    public Cache getCacheByName(AppContext appContext, String groupName) {
        Cache cache = cacheMap.get(groupName);
        if (cache != null)
            return cache;
        cache = new MapCache();
        cacheMap.put(groupName, cache);
        return cache;
    }
}