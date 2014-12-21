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
package net.hasor.rsf.remoting.binder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.Provider;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.common.metadata.ServiceMetaData;
import net.hasor.rsf.remoting.address.AddressManager;
import org.more.RepeateException;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractBindCenter implements BindCenter {
    /* Name -> Group -> Version*/
    private final Map<String, Map<String, Map<String, ServiceDefine<?>>>> rsfServiceMap;
    private final Map<ServiceMetaData<?>, ServiceDefine<?>>               rsfDefineMap;
    private AddressManager                                                addressManager;
    private final RsfContext                                              rsfContext;
    //
    public AbstractBindCenter(RsfContext rsfContext) {
        this.rsfServiceMap = new ConcurrentHashMap<String, Map<String, Map<String, ServiceDefine<?>>>>();
        this.rsfDefineMap = new ConcurrentHashMap<ServiceMetaData<?>, ServiceDefine<?>>();
        this.rsfContext = rsfContext;
        this.addressManager = new AddressManager();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBindBuilder(this);
    }
    /**回收已经发布的服务*/
    final synchronized void recoverService(ServiceDefine<?> rsfInfo) {
        ServiceMetaData<?> smd = rsfInfo.getMetaData();
        //
        Map<String, Map<String, ServiceDefine<?>>> groupMap = this.rsfServiceMap.get(smd.getServiceName());
        if (groupMap != null) {
            Map<String, ServiceDefine<?>> versionMap = groupMap.get(smd.getServiceVersion());
            if (versionMap != null) {
                versionMap.remove(smd.getServiceVersion());
                this.rsfDefineMap.remove(rsfInfo.getMetaData());
            }
        }
    };
    /**发布服务*/
    final synchronized void publishService(ServiceDefine<?> rsfInfo) {
        //name
        Map<String, Map<String, ServiceDefine<?>>> groupMap = this.rsfServiceMap.get(rsfInfo.getBindName());
        if (groupMap == null) {
            groupMap = new ConcurrentHashMap<String, Map<String, ServiceDefine<?>>>();
            this.rsfServiceMap.put(rsfInfo.getBindName(), groupMap);
        }
        //group
        Map<String, ServiceDefine<?>> versionMap = groupMap.get(rsfInfo.getBindGroup());
        if (versionMap == null) {
            versionMap = new ConcurrentHashMap<String, ServiceDefine<?>>();
            groupMap.put(rsfInfo.getBindGroup(), versionMap);
        }
        //version
        String version = rsfInfo.getBindVersion();
        if (versionMap.containsKey(version) == true) {
            throw new RepeateException("Repeate:" + rsfInfo.getMetaData());
        }
        //
        this.rsfDefineMap.put(rsfInfo.getMetaData(), rsfInfo);
        versionMap.put(version, rsfInfo);
    }
    /**根据服务名获取服务描述。*/
    public <T> RsfBindInfo<T> getService(String name, String group, String version) {
        //name
        Map<String, Map<String, ServiceDefine<?>>> groupMap = this.rsfServiceMap.get(name);
        if (groupMap == null)
            return null;
        //group
        Map<String, ServiceDefine<?>> versionMap = groupMap.get(group);
        if (versionMap == null)
            return null;
        //version
        ServiceDefine<?> rsfBindInfo = versionMap.get(version);
        if (rsfBindInfo == null)
            return null;
        return (ServiceMetaData<T>) rsfBindInfo.getMetaData();
    }
    /**获取已经注册的所有服务名称。*/
    public String[] getServiceNames() {
        String[] sname = new String[this.rsfServiceMap.size()];
        this.rsfServiceMap.keySet().toArray(sname);
        return sname;
    }
    /**获取服务上配置有效的过滤器。*/
    public <T> Provider<RsfFilter>[] getFilters(RsfBindInfo<T> metaData) {
        ServiceDefine<?> define = this.rsfDefineMap.get(metaData);
        if (define == null)
            return null;
        return define.getFilterProvider();
    }
    /**获取元信息所描述的服务对象。*/
    public <T> T getBean(RsfBindInfo<T> metaData) {
        ServiceDefine<?> define = this.rsfDefineMap.get(metaData);
        if (define == null)
            return null;
        return (T) this.createBean(define);
    }
    /**获取RSF配置。*/
    public RsfSettings getSettings() {
        return this.rsfContext.getSettings();
    };
    /**获取地址管理器。*/
    public AddressManager getAddressManager() {
        return this.addressManager;
    }
    /**创建 RSF 对象*/
    protected abstract <T> T createBean(RsfBindInfo<T> define);
}