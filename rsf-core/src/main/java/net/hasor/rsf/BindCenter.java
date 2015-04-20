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
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindCenter {
    /**获取RsfBinder*/
    public RsfBinder getRsfBinder();
    /**获取服务对象*/
    public <T> Provider<T> getProvider(RsfBindInfo<T> bindInfo);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getService(String serviceID);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getService(Class<T> serviceType);
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getService(String group, String name, String version);
    /**获取已经注册的所有服务名称。*/
    public List<String> getServiceIDs();
    //
    /**回收已经发布的服务*/
    public void recoverService(RsfBindInfo<?> bindInfo);
}