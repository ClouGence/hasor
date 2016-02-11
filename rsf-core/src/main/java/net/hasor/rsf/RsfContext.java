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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.rsf.address.InterAddress;
/**
 * RSF 环境。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient();
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(String targetStr) throws URISyntaxException;
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(URI targetURL);
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(InterAddress target);
    //
    //
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String serviceID);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version);
    /**获取已经注册的所有服务名称。*/
    public List<String> getServiceIDs();
    //
    //
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     */
    public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo);
    //
    //
    //    /**停止工作*/
    //    public void shutdown();
    /**获取RSF配置*/
    public RsfSettings getSettings();
    /**获取地址路由更新接口。*/
    public RsfUpdater getUpdater();
    //    /**获取RSF配置*/
    //    public RsfEnvironment getEnvironment();
    /**获取类加载器。*/
    public ClassLoader getClassLoader();
    /**创建{@link RsfBinder}。*/
    public RsfBinder binder();
}