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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import net.hasor.core.Provider;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.more.RepeateException;
import org.more.util.StringUtils;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultBindCenter implements BindCenter {
    /* Group -> Name -> Version*/
    private final ConcurrentMap<String, Provider<? extends RsfFilter>>                                rsfFilter1;
    private final List<Provider<? extends RsfFilter>>                                                 rsfFilter2;
    private final ConcurrentMap<String, ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>>> rsfService1Map;
    private final ConcurrentMap<String, RsfBindInfo<?>>                                               rsfService2Map;
    private final AbstractRsfContext                                                                  rsfContext;
    //
    public DefaultBindCenter(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfFilter1 = new ConcurrentSkipListMap<String, Provider<? extends RsfFilter>>();
        this.rsfFilter2 = new ArrayList<Provider<? extends RsfFilter>>();
        this.rsfService1Map = new ConcurrentHashMap<String, ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>>>();
        this.rsfService2Map = new ConcurrentHashMap<String, RsfBindInfo<?>>();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBindBuilder(this.rsfContext);
    }
    public <T> RsfBindInfo<T> getServiceByID(String serviceID) {
        return (RsfBindInfo<T>) this.rsfService2Map.get(serviceID);
    }
    public <T> RsfBindInfo<T> getService(Class<T> serviceType) {
        RsfSettings rsfSettings = this.rsfContext.getSettings();
        String serviceGroup = rsfSettings.getDefaultGroup();
        String serviceName = serviceType.getName();
        String serviceVersion = rsfSettings.getDefaultVersion();
        //覆盖
        RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (StringUtils.isBlank(serviceInfo.group()) == false)
                serviceGroup = serviceInfo.group();
            if (StringUtils.isBlank(serviceInfo.name()) == false)
                serviceName = serviceInfo.name();
            if (StringUtils.isBlank(serviceInfo.version()) == false)
                serviceVersion = serviceInfo.version();
        }
        return getService(serviceGroup, serviceName, serviceVersion);
    }
    public <T> RsfBindInfo<T> getServiceByName(String serviceName) {
        RsfSettings rsfSettings = this.rsfContext.getSettings();
        return getService(rsfSettings.getDefaultGroup(), serviceName, rsfSettings.getDefaultVersion());
    }
    public <T> RsfBindInfo<T> getService(String group, String name, String version) {
        //group
        ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(group);
        if (nameMap == null)
            return null;
        //name
        Map<String, RsfBindInfo<?>> versionMap = nameMap.get(name);
        if (versionMap == null)
            return null;
        //version
        return (RsfBindInfo<T>) versionMap.get(version);
    }
    /**获取已经注册的所有服务名称。*/
    public String[] getServiceNames() {
        String[] sname = new String[this.rsfService1Map.size()];
        this.rsfService1Map.keySet().toArray(sname);
        return sname;
    }
    /**回收已经发布的服务*/
    public synchronized void recoverService(RsfBindInfo<?> bindInfo) {
        ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(bindInfo.getBindGroup());
        if (nameMap != null) {
            Map<String, RsfBindInfo<?>> versionMap = nameMap.get(bindInfo.getBindName());
            if (versionMap != null) {
                versionMap.remove(bindInfo.getBindVersion());
                this.rsfService2Map.remove(bindInfo.getBindID());
            }
        }
    }
    /**发布服务*/
    public synchronized void publishService(RsfBindInfo<?> bindInfo) {
        /*重复检查*/
        if (this.rsfService2Map.containsKey(bindInfo.getBindID()) == true) {
            throw new RepeateException("Repeate:" + bindInfo.getBindID());
        }
        //
        //group
        ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(bindInfo.getBindGroup());
        if (nameMap == null) {
            nameMap = new ConcurrentHashMap<String, ConcurrentMap<String, RsfBindInfo<?>>>();
            this.rsfService1Map.put(bindInfo.getBindGroup(), nameMap);
        }
        //name
        ConcurrentMap<String, RsfBindInfo<?>> versionMap = nameMap.get(bindInfo.getBindName());
        if (versionMap == null) {
            versionMap = new ConcurrentHashMap<String, RsfBindInfo<?>>();
            nameMap.put(bindInfo.getBindName(), versionMap);
        }
        //version
        String version = bindInfo.getBindVersion();
        versionMap.put(version, bindInfo);
        this.rsfService2Map.put(bindInfo.getBindID(), bindInfo);
    }
    //
    /**获取全局{@link RsfFilter}*/
    public Provider<RsfFilter>[] publicFilters() {
        return this.rsfFilter2.toArray(new Provider[this.rsfFilter2.size()]);
    }
    //
    /**查找一个Filter*/
    public <T extends RsfFilter> T findFilter(String filterID) {
        return (T) this.rsfFilter1.get(filterID).get();
    }
    /**发布一个Filter*/
    public synchronized void bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
        if (this.rsfFilter1.containsKey(filterID) == true) {
            throw new RepeateException("repeate filterID " + filterID);
        }
        this.rsfFilter1.put(filterID, provider);
        this.rsfFilter2.add(provider);
    }
}