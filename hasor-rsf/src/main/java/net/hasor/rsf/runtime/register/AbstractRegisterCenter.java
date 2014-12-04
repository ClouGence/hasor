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
package net.hasor.rsf.runtime.register;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.Provider;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RegisterCenter;
import net.hasor.rsf.runtime.RsfBindInfo;
import net.hasor.rsf.runtime.RsfBinder;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfSettings;
import org.more.RepeateException;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRegisterCenter implements RegisterCenter {
    /* Name -> Group -> Version*/
    private final Map<String, Map<String, Map<String, RsfBindInfo<?>>>> rsfServiceMap;
    //
    public AbstractRegisterCenter() {
        this.rsfServiceMap = new ConcurrentHashMap<String, Map<String, Map<String, RsfBindInfo<?>>>>();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBinderBuilder(this);
    }
    /**获取RSF配置。*/
    public abstract RsfSettings getSettings();
    /**回收已经发布的服务*/
    final void recoverService(RsfBindInfo<?> rsfInfo) {
        ServiceMetaData<?> smd = rsfInfo.getMetaData();//
        //
        Map<String, Map<String, RsfBindInfo<?>>> groupMap = this.rsfServiceMap.get(smd.getServiceName());
        if (groupMap != null) {
            Map<String, RsfBindInfo<?>> versionMap = groupMap.get(smd.getServiceVersion());
            if (versionMap != null) {
                versionMap.remove(smd.getServiceVersion());
            }
        }
    };
    /**发布服务*/
    final void publishService(RsfBindInfo<?> rsfInfo) {
        //name
        Map<String, Map<String, RsfBindInfo<?>>> groupMap = this.rsfServiceMap.get(rsfInfo.getBindName());
        if (groupMap == null) {
            groupMap = new ConcurrentHashMap<String, Map<String, RsfBindInfo<?>>>();
            this.rsfServiceMap.put(rsfInfo.getBindName(), groupMap);
        }
        //group
        Map<String, RsfBindInfo<?>> versionMap = groupMap.get(rsfInfo.getBindGroup());
        if (versionMap == null) {
            versionMap = new ConcurrentHashMap<String, RsfBindInfo<?>>();
            groupMap.put(rsfInfo.getBindGroup(), versionMap);
        }
        //version
        String version = rsfInfo.getBindVersion();
        if (versionMap.containsKey(version) == true) {
            throw new RepeateException("Repeate:" + rsfInfo.getMetaData());
        }
        //
        versionMap.put(version, rsfInfo);
    };
    /**添加全局Filter*/
    public abstract void addRsfFilter(Provider<RsfFilter> provider);
    /**获取服务上配置有效的过滤器。*/
    public <T> RsfFilter[] getRsfFilters(ServiceMetaData<T> metaData) {
        // TODO Auto-generated method stub
        return null;
    }
    /**根据服务名获取服务描述。*/
    public <T> ServiceMetaData<T> getService(String serviceName) {
        // TODO Auto-generated method stub
        return null;
    }
    /**获取已经注册的所有服务名称。*/
    public String[] getServiceNames() {
        String[] sname = new String[this.rsfServiceMap.size()];
        this.rsfServiceMap.keySet().toArray(sname);
        return sname;
    }
    /**获取元信息所描述的服务对象。*/
    public <T> T getBean(ServiceMetaData<T> metaData) {
        // TODO Auto-generated method stub
        return null;
    }
}