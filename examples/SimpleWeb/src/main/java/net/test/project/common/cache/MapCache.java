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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import net.hasor.plugins.cache.Cache;
/**
 * 
 * @version : 2014-1-9
 * @author 赵永春(zyc@hasor.net)
 */
public class MapCache extends HashMap<Serializable, Object> implements Cache {
    private static final long serialVersionUID = -6982312180862792707L;
    public boolean toCache(Serializable key, Object value) {
        this.put(key, value);
        return true;
    }
    public Object fromCache(Serializable key) {
        return this.get(key);
    }
    public boolean hasCache(Serializable key) {
        return this.containsKey(key);
    }
    public boolean remove(Serializable key) {
        super.remove(key);
        return true;
    }
    public boolean clearCache() {
        super.clear();
        return true;
    }
    public int size() {
        return super.size();
    }
    public Set<Serializable> keys() {
        return super.keySet();
    }
    public void close() {
        super.clear();
    }
}