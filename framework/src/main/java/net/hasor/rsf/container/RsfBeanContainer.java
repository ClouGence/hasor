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
package net.hasor.rsf.container;
import net.hasor.core.*;
import net.hasor.rsf.*;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.RouteTypeEnum;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfEvent;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 *
 * @version : 2015年12月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBeanContainer {
    protected            Logger     logger       = LoggerFactory.getLogger(getClass());
    private final static Provider[] EMPTY_FILTER = new Provider[0];
    private final ConcurrentMap<String, ServiceDefine<?>>              serviceMap;
    private final ConcurrentMap<String, ConcurrentMap<String, String>> aliasNameMap;
    private final List<FilterDefine>                                   filterList;
    private final Object                                               filterLock;
    private final AddressPool                                          addressPool;
    private final ConcurrentMap<String, Provider<RsfFilter>[]>         filterCache;
    //
    public RsfBeanContainer(AddressPool addressPool) {
        this.serviceMap = new ConcurrentHashMap<String, ServiceDefine<?>>();
        this.aliasNameMap = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
        this.filterList = new ArrayList<FilterDefine>();
        this.filterLock = new Object();
        this.addressPool = addressPool;
        this.filterCache = new ConcurrentHashMap<String, Provider<RsfFilter>[]>();
    }
    /**
     * 计算指定服务上配置的过滤器。{@link RsfFilter}按照配置方式分为共有和私有。
     * 共有Filter的生效范围是所有Service，私有Filter的生效范围仅Service。
     * 每一个Filter在配置的时候都需要指定ID，根据ID私有Filter可以覆盖共有Filter的配置。
     * @param serviceID 服务ID
     */
    public Provider<RsfFilter>[] getFilterProviders(String serviceID) {
        ServiceDefine<?> info = this.serviceMap.get(serviceID);
        if (info == null) {
            return EMPTY_FILTER;
        }
        Provider<RsfFilter>[] result = filterCache.get(serviceID);
        if (result == null) {
            List<String> cacheIds = new LinkedList<String>();
            Map<String, FilterDefine> cacheFilters = new HashMap<String, FilterDefine>();
            //2.计算最终结果。
            List<FilterDefine> publicList = this.filterList;
            if (!publicList.isEmpty()) {
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
            //3.产出最终结果(过滤器链前端是public，后段是private)。
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
     * @param rsfBindInfo 服务ID。
     * @return 服务提供者
     */
    public <T> Provider<T> getProvider(RsfBindInfo<T> rsfBindInfo) {
        ServiceDefine<?> info = this.serviceMap.get(rsfBindInfo.getBindID());
        if (info == null)
            return null;
        Provider<?> target = info.getCustomerProvider();
        if (target != null) {
            return (Provider<T>) target;
        }
        return null;
    }
    /**
     * 根据服务id获取服务元信息。
     * @param serviceID 服务ID。
     */
    public RsfBindInfo<?> getRsfBindInfo(String serviceID) {
        ServiceDefine<?> info = this.serviceMap.get(serviceID);
        if (info == null)
            return null;
        return info.getDomain();
    }
    /**
     * 根据服务id获取服务元信息。
     * @param aliasType 名字分类。
     * @param aliasName 别名。
     */
    public RsfBindInfo<?> getRsfBindInfo(String aliasType, String aliasName) {
        ConcurrentMap<String, String> aliasNameMaps = this.aliasNameMap.get(aliasType);
        if (aliasNameMaps == null) {
            return null;
        }
        String serviceID = aliasNameMaps.get(aliasName);
        if (serviceID == null) {
            return null;
        }
        return this.serviceMap.get(serviceID);
    }
    /**
     * 根据类型获取服务元信息。如果类型上配置了{@link RsfService @RsfService}注解，则使用该注解的配置信息。
     * 否则将使用RSF默认配置下的Group、Version。
     * @param serviceType 服务类型。
     */
    public <T> RsfBindInfo<T> getRsfBindInfo(Class<T> serviceType) {
        RsfSettings rsfSettings = this.addressPool.getRsfEnvironment().getSettings();
        String serviceGroup = rsfSettings.getDefaultGroup();
        String serviceName = serviceType.getName();
        String serviceVersion = rsfSettings.getDefaultVersion();
        //覆盖
        RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (!StringUtils.isBlank(serviceInfo.group())) {
                serviceGroup = serviceInfo.group();
            }
            if (!StringUtils.isBlank(serviceInfo.name())) {
                serviceName = serviceInfo.name();
            }
            if (!StringUtils.isBlank(serviceInfo.version())) {
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
    /**根据别名系统获取所有已经注册的服务名称。*/
    public List<String> getServiceIDs(String category) {
        ConcurrentMap<String, String> aliasNameMaps = this.aliasNameMap.get(category);
        if (aliasNameMaps == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<String>(aliasNameMaps.keySet());
    }
    /**获取环境对象。*/
    public RsfEnvironment getEnvironment() {
        return this.addressPool.getRsfEnvironment();
    }
    //
    /* ----------------------------------------------------------------------------------------- */
    //
    /**创建{@link RsfApiBinder}。*/
    public RsfPublisher createPublisher(final RsfBeanContainer container, final RsfContext rsfContext) {
        return new ContextRsfBindBuilder() {
            @Override
            protected RsfBeanContainer getContainer() {
                return container;
            }
            @Override
            protected RsfContext getRsfContext() {
                return rsfContext;
            }
        };
    }
    /**
     * 添加一个全局服务过滤器。
     * @param define 过滤器对象。
     */
    public void publishFilter(FilterDefine define) {
        String filterID = Hasor.assertIsNotNull(define.filterID());
        synchronized (this.filterLock) {
            for (FilterDefine filter : this.filterList) {
                if (filterID.equals(filter.filterID())) {
                    throw new IllegalStateException("repeate filterID :" + filterID);
                }
            }
            this.filterList.add(define);
            this.filterCache.clear();
        }
    }
    /**
     * 发布服务
     * @param serviceDefine 服务定义。
     */
    public synchronized <T> boolean publishService(ServiceDefine<T> serviceDefine) {
        String serviceID = serviceDefine.getDomain().getBindID();
        if (this.serviceMap.containsKey(serviceID)) {
            String serviceType = this.serviceMap.get(serviceID).getDomain().getServiceType().name();
            String logMessage = "a " + serviceType + " of the same name already exists , serviceID -> " + serviceID;
            this.logger.error(logMessage);
            throw new IllegalStateException(logMessage);
        }
        this.logger.info("service to public, id= {}", serviceID);
        ServiceDefine<?> info = this.serviceMap.putIfAbsent(serviceID, serviceDefine);
        //
        EventContext eventContext = this.addressPool.getRsfEnvironment().getEventContext();
        if (RsfServiceType.Provider == serviceDefine.getServiceType()) {
            //服务提供者
            if (serviceDefine.getCustomerProvider() == null) {
                throw new RsfException(ProtocolStatus.Forbidden, "Provider Not set the implementation class.");
            }
            try {
                eventContext.fireSyncEvent(RsfEvent.Rsf_ProviderService, serviceDefine);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            //服务消费者
            try {
                eventContext.fireSyncEvent(RsfEvent.Rsf_ConsumerService, serviceDefine);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        // .收录别名
        Set<String> aliasTypes = serviceDefine.getAliasTypes();
        for (String aliasType : aliasTypes) {
            ConcurrentMap<String, String> aliasMap = this.aliasNameMap.get(aliasType);
            if (aliasMap == null) {
                aliasMap = new ConcurrentHashMap<String, String>();
                this.aliasNameMap.putIfAbsent(aliasType, aliasMap);
            }
            String aliasName = serviceDefine.getAliasName(aliasType);
            if (StringUtils.isBlank(aliasName)) {
                continue;
            }
            aliasMap.putIfAbsent(aliasName, serviceID);
        }
        //
        // .追加地址
        this.addressPool.appendStaticAddress(serviceID, serviceDefine.getAddressSet());
        // .更新流控
        String flowControl = serviceDefine.getFlowControl();
        if (StringUtils.isNotBlank(flowControl)) {
            this.addressPool.updateFlowControl(serviceID, flowControl);
        }
        // .更新路由
        Map<RouteTypeEnum, String> scriptMap = serviceDefine.getRouteScript();
        if (scriptMap != null && !scriptMap.isEmpty()) {
            for (Map.Entry<RouteTypeEnum, String> routeEnt : scriptMap.entrySet()) {
                this.addressPool.updateRoute(serviceID, routeEnt.getKey(), routeEnt.getValue());
            }
        }
        //
        return true;
    }
    /**
     * 回收发布的服务
     * @param serviceID 服务定义。
     */
    public synchronized boolean recoverService(String serviceID) {
        if (this.serviceMap.containsKey(serviceID)) {
            //
            // .发布删除消息( 1.Center解除注册、2.地址本回收)
            EventContext eventContext = this.getEnvironment().getEventContext();
            RsfBindInfo<?> rsfBindInfo = this.serviceMap.get(serviceID);
            try {
                eventContext.fireSyncEvent(RsfEvent.Rsf_DeleteService, rsfBindInfo);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
            //
            // .回收服务
            this.serviceMap.remove(serviceID);
            //
            for (Map.Entry<String, ConcurrentMap<String, String>> aliasEntry : this.aliasNameMap.entrySet()) {
                ConcurrentMap<String, String> aliasSet = aliasEntry.getValue();
                ArrayList<String> toRemove = new ArrayList<String>();
                for (Map.Entry<String, String> entry : aliasSet.entrySet()) {
                    if (serviceID.equals(entry.getValue())) {
                        toRemove.add(entry.getKey());
                    }
                }
                //
                for (String key : toRemove) {
                    aliasSet.remove(key);
                }
            }
            //
            return true;
        }
        return false;
    }
    //
    /* ----------------------------------------------------------------------------------------- */
    //
    /**
     * 发布的服务
     * @param appContext 用于查找服务的容器上下文。
     */
    public void lookUp(AppContext appContext) {
        List<BindInfo<FilterDefine>> filterList = appContext.findBindingRegister(FilterDefine.class);
        for (BindInfo<FilterDefine> defile : filterList) {
            FilterDefine fd = appContext.getInstance(defile);
            if (fd != null) {
                this.publishFilter(fd);
            }
        }
        List<BindInfo<ServiceDefine>> serviceList = appContext.findBindingRegister(ServiceDefine.class);
        for (BindInfo<ServiceDefine> defile : serviceList) {
            ServiceDefine sd = appContext.getInstance(defile);
            if (sd != null) {
                this.publishService(sd);
            }
        }
        //
        this.logger.info("lookUp finish.");
    }
}