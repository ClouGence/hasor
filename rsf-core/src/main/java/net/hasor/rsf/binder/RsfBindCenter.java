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
package net.hasor.rsf.binder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.RepeateException;
import org.more.util.StringUtils;
/**
 * 本地服务注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBindCenter implements BindCenter {
    /* Group -> Name -> Version*/
    private final ConcurrentMap<String, RsfBindInfo<?>> rsfServiceMap;
    private final AbstractRsfContext                    rsfContext;
    //
    //
    public RsfBindCenter(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfServiceMap = new ConcurrentHashMap<String, RsfBindInfo<?>>();
    }
    //
    public RsfBinder getRsfBinder() {
        return new RsfBindBuilder(this.rsfContext);
    }
    //
    public <T> RsfBindInfo<T> getServiceByID(String serviceID) {
        return (RsfBindInfo<T>) this.rsfServiceMap.get(serviceID);
    }
    //
    public <T> RsfBindInfo<T> getService(Class<T> serviceType) {
        RsfSettings rsfSettings = this.rsfContext.getSettings();
        String serviceGroup = rsfSettings.getDefaultGroup();
        String serviceName = serviceType.getName();
        String serviceVersion = rsfSettings.getDefaultVersion();
        //覆盖
        RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (StringUtils.isBlank(serviceInfo.group()) == false) {
                serviceGroup = serviceInfo.group();
            }
            if (StringUtils.isBlank(serviceInfo.name()) == false) {
                serviceName = serviceInfo.name();
            }
            if (StringUtils.isBlank(serviceInfo.version()) == false) {
                serviceVersion = serviceInfo.version();
            }
        }
        return getService(serviceGroup, serviceName, serviceVersion);
    }
    //
    public <T> RsfBindInfo<T> getServiceByName(String serviceName) {
        RsfSettings rsfSettings = this.rsfContext.getSettings();
        return getService(rsfSettings.getDefaultGroup(), serviceName, rsfSettings.getDefaultVersion());
    }
    //
    public <T> RsfBindInfo<T> getService(String group, String name, String version) {
        String serviceID = RsfRuntimeUtils.bindID(group, name, version);
        return (RsfBindInfo<T>) this.rsfServiceMap.get(serviceID);
    }
    //
    /**获取已经注册的所有服务名称。*/
    public List<String> getServiceIDs() {
        return new ArrayList<String>(this.rsfServiceMap.keySet());
    }
    //
    /**回收已经发布的服务*/
    public void recoverService(RsfBindInfo<?> bindInfo) {
        String serviceID = bindInfo.getBindID();
        this.rsfServiceMap.remove(serviceID);
    }
    /**发布服务*/
    public void publishService(RsfBindInfo<?> bindInfo) {
        String serviceID = bindInfo.getBindID();
        if (this.rsfServiceMap.containsKey(serviceID) == true) {
            throw new RepeateException("Repeate:" + serviceID); /*重复检查*/
        }
        this.rsfServiceMap.putIfAbsent(serviceID, bindInfo);
    }
}