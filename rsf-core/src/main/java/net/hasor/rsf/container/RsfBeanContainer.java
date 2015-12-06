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
package net.hasor.rsf.container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.more.RepeateException;
import org.more.util.StringUtils;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
/**
 * 
 * @version : 2015年12月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBeanContainer {
    //Group - ServiceInfo
    private final ConcurrentMap<String, ServiceInfo<?>> serviceMap;
    private final List<FilterDefine>                    filterList;
    private final Object                                filterLock;
    private final RsfEnvironment                        environment;
    private final static RsfFilter[]                    EMPTY_FILTER = new RsfFilter[0];
    //
    public RsfBeanContainer(RsfEnvironment environment) {
        this.serviceMap = new ConcurrentHashMap<String, ServiceInfo<?>>();
        this.filterList = new ArrayList<FilterDefine>();
        this.filterLock = new Object();
        this.environment = environment;
    }
    //
    /** 
     * 添加一个全局服务过滤器。
     * @param filterID 过滤器ID，不可重复定义相同id的RsfFilter。
     * @param instance 过滤器对象。
     */
    public void addFilter(String filterID, RsfFilter instance) {
        this.addFilter(filterID, new InstanceProvider<RsfFilter>(Hasor.assertIsNotNull(instance)));
    }
    /**
     * 添加一个全局服务过滤器。
     * @param filterID 过滤器ID，不可重复定义相同id的RsfFilter。
     * @param provider 过滤器对象。
     */
    public void addFilter(String filterID, Provider<? extends RsfFilter> provider) {
        synchronized (this.filterLock) {
            for (FilterDefine define : this.filterList) {
                if (StringUtils.equals(filterID, define.filterID())) {
                    throw new RepeateException("repeate filterID :" + filterID);
                }
            }
            this.filterList.add(new FilterDefine(filterID, provider));
        }
    }
    public RsfFilter[] getFilter(String serviceID) {
        ServiceInfo<?> info = this.serviceMap.get(serviceID);
        if (info == null) {
            return EMPTY_FILTER;
        }
        List<FilterDefine> publicList = this.filterList;
        List<RsfFilter> snapshotsList = info.getFilterSnapshots();
        int maxSize = publicList.size() + snapshotsList.size();
        List<RsfFilter> filterArrays = new ArrayList<RsfFilter>(maxSize);
        //
        //
    }
    /**
     * 根据服务id获取服务对象。如果服务未定义或者服务未声明提供者，则返回null。
     * @param serviceID 服务ID。
     * @return 服务提供者
     */
    public Provider<?> getProvider(String serviceID) {
        ServiceInfo<?> info = this.serviceMap.get(serviceID);
        if (info == null)
            return null;
        return info.getProvider();
    }
    /**
     * 根据服务id获取服务元信息。
     * @param serviceID 服务ID。
     */
    public RsfBindInfo<?> getRsfBindInfo(String serviceID) {
        ServiceInfo<?> info = this.serviceMap.get(serviceID);
        if (info == null)
            return null;
        return info.getDomain();
    }
    /**
     * 根据类型获取服务元信息。如果类型上配置了{@link RsfService @RsfService}注解，则使用该注解的配置信息。
     * 否则将使用RSF默认配置下的Group、Version。
     * @param serviceType 服务类型。
     */
    public <T> RsfBindInfo<T> getRsfBindInfo(Class<T> serviceType) {
        RsfSettings rsfSettings = this.environment.getSettings();
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
        return (RsfBindInfo<T>) getRsfBindInfo(serviceGroup, serviceName, serviceVersion);
    }
    /**
     * 根据服务坐标获取服务元信息。
     * @param group 组别
     * @param name 服务名
     * @param version 服务版本
     */
    public RsfBindInfo<?> getRsfBindInfo(String group, String name, String version) {
        String serviceID = String.format("[%s]%s-%s", group, name, version);
        return this.getRsfBindInfo(serviceID);
    }
    /**获取所有已经注册的服务名称。*/
    public List<String> getServiceIDs() {
        return new ArrayList<String>(this.serviceMap.keySet());
    }
}