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
package net.hasor.rsf.adapter;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.RsfFilter;
/**
 * 获取服务上配置有效的过滤器。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBindDefine<T> extends CustomerProvider<T> {
    /**获取Provider对象，可以直接取得对象实例。*/
    public Provider<T> getCustomerProvider();
    /**获取服务上配置有效的过滤器*/
    public Provider<RsfFilter>[] getFilterProvider();
    /**查找注册的Filter*/
    public RsfFilter getFilter(String filterID);
}