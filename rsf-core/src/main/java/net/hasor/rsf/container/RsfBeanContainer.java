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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.more.RepeateException;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.domain.RsfBindInfoWrap;
/**
 * 
 * @version : 2015年12月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBeanContainer {
    protected Logger                                           logger;
    private final static Provider<RsfFilter>[]                 EMPTY_FILTER = new Provider[0];
    private final ConcurrentMap<String, ServiceInfo<?>>        serviceMap;
    private final List<FilterDefine>                           filterList;
    private final Object                                       filterLock;
    private final RsfEnvironment                               environment;
    private AddressPool                                        addressPool;
    private final ConcurrentMap<String, Provider<RsfFilter>[]> filterCache;
    //
    public RsfBeanContainer(RsfEnvironment rsfEnvironment) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.serviceMap = new ConcurrentHashMap<String, ServiceInfo<?>>();
        this.filterList = new ArrayList<FilterDefine>();
        this.filterLock = new Object();
        this.environment = rsfEnvironment;
        this.addressPool = new AddressPool(rsfEnvironment);
        this.filterCache = new ConcurrentHashMap<String, Provider<RsfFilter>[]>();
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
            this.filterCache.clear();
        }
    }
    /**
     * 计算指定服务上配置的过滤器。{@link RsfFilter}按照配置方式分为共有和私有。
     * 共有Filter的生效范围是所有Service，私有Filter的生效范围仅Service。
     * 每一个Filter在配置的时候都需要指定ID，根据ID私有Filter可以覆盖共有Filter的配置。
     * @param serviceID 服务ID
     */
    public Provider<RsfFilter>[] getFilterProviders(String serviceID) {
        ServiceInfo<?> info = this.serviceMap.get(serviceID);
        if (info == null) {
            return EMPTY_FILTER;
        }
        Provider<RsfFilter>[] result = filterCache.get(serviceID);
        if (result == null) {
            List<String> cacheIds = new LinkedList<String>();
            Map<String, FilterDefine> cacheFilters = new HashMap<String, FilterDefine>();
            //2.计算最终结果。
            List<FilterDefine> publicList = this.filterList;
            if (publicList != null && !publicList.isEmpty()) {
                for (FilterDefine filter : publicList) {
                    String filterID = filter.filterID();
                    cacheFilters.put(filterID, filter);
                    cacheIds.add(filterID);
                }
            }
            List<FilterDefine> snapshotsList = info.getFilterSnapshots();
            if (snapshotsList != null && !snapshotsList.isEmpty()) {
                for (FilterDefine filter : snapshotsList) {
                    String filterID = filter.filterID();
                    cacheFilters.put(filterID, filter);//保存或覆盖已有。
                    if (cacheIds.contains(filterID)) {
                        cacheIds.remove(filterID);//如果全局Filter已经定义了这个ID，那么从已有顺序中删除，在尾部追加私有Filter。
                    }
                    cacheIds.add(filterID);
                }
            }
            //3.产出最终结果。
            List<Provider<RsfFilter>> filterArrays = new ArrayList<Provider<RsfFilter>>(cacheIds.size());
            for (String filterID : cacheIds) {
                FilterDefine define = cacheFilters.get(filterID);
                filterArrays.add(define);
            }
            result = (Provider<RsfFilter>[]) filterArrays.toArray(new Provider[filterArrays.size()]);
            this.filterCache.put(serviceID, result);
        }
        return result;
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
        return info.getCustomerProvider();
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
        String serviceID = "[" + group + "]" + name + "-" + version;//String.format("[%s]%s-%s", group, name, version);
        return this.getRsfBindInfo(serviceID);
    }
    /**获取所有已经注册的服务名称。*/
    public List<String> getServiceIDs() {
        return new ArrayList<String>(this.serviceMap.keySet());
    }
    /**获取环境对象。*/
    public RsfEnvironment getEnvironment() {
        return environment;
    }
    public AddressPool getAddressPool() {
        return addressPool;
    }
    /**
     * 发布服务
     * @param serviceDefine 服务定义。
     */
    public <T> RegisterReference<T> publishService(ServiceInfo<T> serviceDefine) {
        String serviceID = serviceDefine.getDomain().getBindID();
        if (this.serviceMap.containsKey(serviceID)) {
            logger.error("service {} is exist.", serviceID);
            throw new RepeateException("service " + serviceID + " is exist.");
        }
        logger.info("service to public, id= {}", serviceID);
        ServiceInfo<?> info = this.serviceMap.putIfAbsent(serviceID, serviceDefine);
        return new RegisterReferenceInfoWrap<T>(this, serviceDefine);
    }
    /**
     * 回收发布的服务
     * @param serviceDefine 服务定义。
     */
    public boolean recoverService(String serviceID) {
        this.getAddressPool().removeBucket(serviceID);
        if (this.serviceMap.containsKey(serviceID)) {
            this.serviceMap.remove(serviceID);
            return true;
        }
        return false;
    }
    /**创建{@link RsfBinder}。*/
    public RsfBinder createBinder() {
        return new RsfBindBuilder() {
            protected RsfBeanContainer getContainer() {
                return RsfBeanContainer.this;
            }
        };
    }
}
class RegisterReferenceInfoWrap<T> extends RsfBindInfoWrap<T>implements RegisterReference<T> {
    private RsfBeanContainer rsfContainer;
    public RegisterReferenceInfoWrap(RsfBeanContainer rsfContainer, ServiceInfo<T> serviceDefine) {
        super(serviceDefine.getDomain());
        this.rsfContainer = rsfContainer;
    }
    @Override
    public boolean unRegister() {
        String serviceID = this.getBindID();
        return this.rsfContainer.recoverService(serviceID);
    }
}