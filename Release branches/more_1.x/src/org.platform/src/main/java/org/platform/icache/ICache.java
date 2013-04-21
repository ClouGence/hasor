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
import org.platform.context.AppContext;
import org.platform.context.setting.Config;
import com.google.inject.ImplementedBy;
/**
 * 缓存使用入口，缓存的实现由系统自行提供。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
@ImplementedBy(NoneCache.class)
public interface ICache {
    /**初始化Cache*/
    public void initCache(AppContext appContext, Config config);
    /**销毁*/
    public void destroy();
    /**将一个对象放入缓存。*/
    public void toCache(String key, Object value);
    /**将一个对象放入缓存。*/
    public void toCache(String key, Object value, long timeout);
    /**根据key从缓存中获取缓存对象。*/
    public Object fromCache(String key);
    /**判断缓存中是否有要求的对象。*/
    public boolean hasCache(String key);
    /**删除某个缓存的内容*/
    public void remove(String key);
    /**清空缓存*/
    public void clear();
}