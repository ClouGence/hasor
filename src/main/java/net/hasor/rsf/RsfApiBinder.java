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
package net.hasor.rsf;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
/**
 * 服务配置器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfApiBinder extends RsfPublisher, ApiBinder {
    /**
     * 获取 {@link RsfEnvironment}
     * @return return {@link RsfEnvironment}
     */
    public RsfEnvironment getEnvironment();

    /**
     * 绑定一个类型到RSF环境。
     * @param bindInfo 服务类型
     */
    public <T> ConfigurationBuilder<T> rsfService(BindInfo<T> bindInfo);

    /**
     * 绑定一个类型到RSF环境。
     * @param bindInfo 服务类型
     */
    public <T> Provider<T> converToProvider(RsfBindInfo<T> bindInfo);
}