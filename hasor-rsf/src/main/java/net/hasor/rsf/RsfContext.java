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
package net.hasor.rsf;
/**
 * RSF 环境。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    /** @return 获取注册中心*/
    public BindCenter getBindCenter();
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    public <T> T getBean(RsfBindInfo<T> bindInfo);
    /** @return 获取配置*/
    public RsfSettings getSettings();
    /** @return 获取客户端*/
    public RsfClient getRsfClient();
    /**
     * 查找一个{@link RsfFilter}
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(String filterID);
    /**
     * 获取服务上的{@link RsfFilter}
     * @param serviceID 服务ID
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(String serviceID, String filterID);
    /**
     * 查找一个{@link RsfFilter}<br>
     *  如果在Binder阶段注册的服务通过{@link RsfBinder}指定过Group、Name、Version任意一个值则该方法不确定会成功返回。
     * @param servicetType 服务类型
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(Class<?> servicetType, String filterID);
    /**停止工作*/
    public void shutdown();
}