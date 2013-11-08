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
import net.hasor.core.AppContext;
/**
 * 用于创建指定名称的缓存器。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface CacheCreator {
    /**
     * 根据名称创建或返回一个缓存器
     * @param appContext 应用程序环境
     * @param groupName 缓存器
     */
    public Cache getCacheByName(AppContext appContext, String groupName);
}