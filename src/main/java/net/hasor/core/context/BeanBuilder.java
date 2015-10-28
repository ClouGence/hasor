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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BeanBuilder {
    /**创建一个AbstractBindInfoProviderAdapter*/
    public <T> AbstractBindInfoProviderAdapter<T> createBindInfoByType(Class<T> bindType);
    /**
     * 通过{@link BindInfo}创建Bean。
     * @param bindInfo 绑定信息。
     * @return 创建并返回实例
     */
    public <T> T getInstance(BindInfo<T> bindInfo, AppContext appContext);
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(Class<T> bindType, AppContext appContext);
}