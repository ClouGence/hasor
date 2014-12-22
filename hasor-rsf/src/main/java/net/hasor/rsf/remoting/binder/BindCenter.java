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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.adapter.AbstractBindCenter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import org.more.RepeateException;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class BindCenter extends AbstractBindCenter {
    /* Group -> Name -> Version*/
    private final Map<String, Map<String, Map<String, RsfBindInfo<?>>>> rsfService1Map;
    private final Map<String, RsfBindInfo<?>>                           rsfService2Map;
    private final AbstractRsfContext                                    rsfContext;
    //
    public BindCenter(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfService1Map = new ConcurrentHashMap<String, Map<String, Map<String, RsfBindInfo<?>>>>();
        this.rsfService2Map = new ConcurrentHashMap<String, RsfBindInfo<?>>();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBindBuilder(this.rsfContext);
    }
    public RsfBindInfo<?> getService(String serviceID) {
        return this.rsfService2Map.get(serviceID);
    }
    public RsfBindInfo<?> getService(String group, String name, String version) {
        //group
        Map<String, Map<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(group);
        if (nameMap == null)
            return null;
        //name
        Map<String, RsfBindInfo<?>> versionMap = nameMap.get(name);
        if (versionMap == null)
            return null;
        //version
        return versionMap.get(version);
    }
    /**获取已经注册的所有服务名称。*/
    public String[] getServiceNames() {
        String[] sname = new String[this.rsfService1Map.size()];
        this.rsfService1Map.keySet().toArray(sname);
        return sname;
    }
    /**回收已经发布的服务*/
    public synchronized void recoverService(RsfBindInfo<?> bindInfo) {
        Map<String, Map<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(bindInfo.getBindGroup());
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
        Map<String, Map<String, RsfBindInfo<?>>> nameMap = this.rsfService1Map.get(bindInfo.getBindGroup());
        if (nameMap == null) {
            nameMap = new ConcurrentHashMap<String, Map<String, RsfBindInfo<?>>>();
            this.rsfService1Map.put(bindInfo.getBindGroup(), nameMap);
        }
        //name
        Map<String, RsfBindInfo<?>> versionMap = nameMap.get(bindInfo.getBindName());
        if (versionMap == null) {
            versionMap = new ConcurrentHashMap<String, RsfBindInfo<?>>();
            nameMap.put(bindInfo.getBindName(), versionMap);
        }
        //version
        String version = bindInfo.getBindVersion();
        versionMap.put(version, bindInfo);
        this.rsfService2Map.put(bindInfo.getBindID(), bindInfo);
    }
}