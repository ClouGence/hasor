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
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
/**
 * RSF 环境。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext extends OnlineStatus {
    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient();

    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(String targetStr) throws URISyntaxException;

    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(URI targetURL);

    /** @return 发起远程调用的客户端接口*/
    public RsfClient getRsfClient(InterAddress target);

    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String serviceID);

    /**根据别名系统来查找服务。*/
    public <T> RsfBindInfo<T> getServiceInfo(String aliasType, String aliasName);

    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType);

    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version);

    /**获取已经注册的所有服务名称。*/
    public List<String> getServiceIDs();

    /**根据别名系统来获取该别名系统下所有服务ID。*/
    public List<String> getServiceIDs(String aliasType);

    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     */
    public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo);

    /**获取运行着的协议*/
    public Set<String> runProtocols();

    /**获取默认协议*/
    public String getDefaultProtocol();

    /** 获取RSF运行的地址。 */
    public InterAddress bindAddress(String protocol);

    /** 获取RSF运行的网关地址。 */
    public InterAddress gatewayAddress(String protocol);

    /** 获取RSF运行的网关地址（如果有）或者本地绑定地址。 */
    public InterAddress publishAddress(String protocol);

    /**获取RSF配置*/
    public RsfSettings getSettings();

    /**获取IoC容器*/
    public AppContext getAppContext();

    /**获取{@link Environment}*/
    public RsfEnvironment getEnvironment();

    /**获取地址路由更新接口。*/
    public RsfUpdater getUpdater();

    /**获取类加载器。*/
    public ClassLoader getClassLoader();

    /**创建{@link RsfPublisher}。*/
    public RsfPublisher publisher();

    /**应用上线（优雅上线）*/
    public void online();

    /**应用下线（优雅停机）*/
    public void offline();
}