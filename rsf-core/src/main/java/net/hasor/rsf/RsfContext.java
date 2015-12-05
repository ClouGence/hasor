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
import java.util.List;
import net.hasor.core.Provider;
/**
 * RSF 环境。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    /**停止工作*/
    public void shutdown();
    /** @return 获取RSF配置*/
    public RsfSettings getSettings();
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient();
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    public <T> T getBean(RsfBindInfo<T> bindInfo);
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String serviceID);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version);
    /**获取已经注册的所有服务名称。*/
    public List<String> getServiceIDs();
}