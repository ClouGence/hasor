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
/** 
 * 缓存使用入口，缓存的实现由系统自行提供。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
public interface CacheManager {
    /**是否启用缓存系统.*/
    public static final String CacheConfig_Enable = "cacheConfig.enable";
    /**获取默认缓存*/
    public <T> Cache<T> getDefaultCache();
    /**获取缓存*/
    public <T> Cache<T> getCache(String cacheName);
    /**根据样本类型获取该类型的Key生成器。*/
    public KeyBuilder getKeyBuilder(Class<?> sampleType);
    /**初始化启动缓存服务。*/
    public void initManager(AppContext appContext);
    /**销毁缓存服务*/
    public void destroyManager(AppContext appContext);
}