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
package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;

import java.util.List;
/**
 * Bean构建接口，负责创建和定义Bean对象。
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BeanBuilder {
    /**
     * 创建{@link AbstractBindInfoProviderAdapter}并注册到容器中。
     * @param bindType 声明的类型。
     * @param binderSource
     * @return 返回容器中新建的{@link AbstractBindInfoProviderAdapter}。
     */
    public <T> AbstractBindInfoProviderAdapter<T> createInfoAdapter(Class<T> bindType, Class<?> binderSource);

    /**
     * 通过{@link BindInfo}创建Bean。
     * @param bindInfo 绑定信息。
     * @return 创建并返回实例
     */
    public <T> T getInstance(BindInfo<T> bindInfo, AppContext appContext);

    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(Class<T> bindType, AppContext appContext);
    //
    /* ----------------------------------------------------------------------------------------- */
    //

    /**根据ID获取{@link BindInfo}。*/
    public <T> BindInfo<T> findBindInfoByID(String bindID);

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindInfoByType(Class<T> bindType);

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> List<BindInfo<T>> findBindInfoList(Class<T> bindType);

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     * @param withName 绑定名
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindInfo(String withName, Class<T> bindType);
}