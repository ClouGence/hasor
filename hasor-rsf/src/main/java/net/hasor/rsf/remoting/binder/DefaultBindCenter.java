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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.adapter.AbstractBindCenter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import org.more.RepeateException;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultBindCenter extends AbstractBindCenter {
    /* Group -> Name -> Version*/
    private final ConcurrentMap<String, Provider<RsfFilter>>                                          rsfFilter1;
    private final List<Provider<RsfFilter>>                                                           rsfFilter2;
    private final ConcurrentMap<String, ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>>> rsfService1Map;
    private final ConcurrentMap<String, RsfBindInfo<?>>                                               rsfService2Map;
    private final AbstractRsfContext                                                                  rsfContext;
    //
    public DefaultBindCenter(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfFilter1 = new ConcurrentSkipListMap<String, Provider<RsfFilter>>();
        this.rsfFilter2 = new ArrayList<Provider<RsfFilter>>();
        this.rsfService1Map = new ConcurrentHashMap<String, ConcurrentMap<String, ConcurrentMap<String, RsfBindInfo<?>>>>();
        this.rsfService2Map = new ConcurrentHashMap<String, RsfBindInfo<?>>();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBindBuilder(this.rsfContext);
    }
    public <T> RsfBindInfo<T> getService(String serviceID) {
        return (RsfBindInfo<T>) this.rsfService2Map.get(serviceID);
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
    public Provider<RsfFilter>[] publicFilters() {
        return this.rsfFilter2.toArray(new Provider[this.rsfFilter2.size()]);
    }
    public <T extends RsfFilter> T findFilter(String filterID) {
        return (T) this.rsfFilter1.get(filterID).get();
    }
    public synchronized void bindFilter(String filterID, Provider<RsfFilter> provider) {
        if (this.rsfFilter2.contains(filterID) == true) {
            throw new RepeateException("repeate filterID " + filterID);
        }
        this.rsfFilter1.put(filterID, provider);
        this.rsfFilter2.add(provider);
    }
}