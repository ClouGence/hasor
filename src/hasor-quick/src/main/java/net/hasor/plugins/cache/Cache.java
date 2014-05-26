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
package net.hasor.plugins.cache;
import java.io.Serializable;
import java.util.Set;
/**
 * 缓存接口。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Cache {
    /**将一个对象放入缓存。*/
    public boolean toCache(Serializable key, Object value);
    /**根据key从缓存中获取缓存对象。*/
    public Object fromCache(Serializable key);
    /**判断缓存中是否有要求的对象。*/
    public boolean hasCache(Serializable key);
    /**删除某个缓存的内容。*/
    public boolean remove(Serializable key);
    /**清空缓存。*/
    public boolean clearCache();
    /**目前缓存大小。*/
    public int size();
    /**获取缓存中Keys。*/
    public Set<Serializable> keys();
    /**关闭并停用缓存*/
    public void close();
}